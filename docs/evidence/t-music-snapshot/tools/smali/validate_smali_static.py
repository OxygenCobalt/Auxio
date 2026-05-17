#!/usr/bin/env python3
"""
Static smali assembler/verifier hazard checker.

Checks (high-confidence defects only — no style warnings):
  - move-result* not immediately after invoke/filled-new-array
  - move-result variant mismatch vs invoke return type
    (e.g. move-result after J-returning invoke instead of move-result-wide)
  - void-return invoke followed by move-result*
  - malformed method descriptor in invoke
  - invoke register count / slot mismatch vs parsed descriptor
  - non-range invoke with more than 5 registers
  - wide arg (J/D) missing second register in non-range invoke
  - /range slot count mismatch vs descriptor
  - malformed /range register syntax
  - /range using mixed register families
  - AndroidX (Landroidx/) or Media3 (Lcom/google/android/exoplayer2/) descriptors
    in the com/tw/music/media bridge files (pollution guard)
"""
import re
from pathlib import Path

SMALI_ROOT = Path('app/apktool')

INVOKE_RE = re.compile(r'^\s*(invoke-[\w/-]+)\s+\{([^}]*)\},\s*(\S+)')
MOVE_RESULT_TYPED_RE = re.compile(r'^\s*(move-result(-object|-wide)?)\b')
FILL_NEW_ARRAY_RE = re.compile(r'^\s*filled-new-array(?:/range)?\s+\{[^}]*\},\s*(\S+)')
METHOD_RE = re.compile(r'^\s*\.method\b')
END_METHOD_RE = re.compile(r'^\s*\.end\s+method\b')
RANGE_RE = re.compile(r'^\s*invoke-[\w-]+/range\s+\{\s*([vp]\d+)\s*\.\.\s*([vp]\d+)\s*\},\s*(\S+)')
DESC_RE = re.compile(r'^([\[ZBCSIFJDV]|L[^;]+;)+$')
CLASS_DESC_RE = re.compile(r'^(?:L[^;]+;|\[+(?:[ZBCSIFJD]|L[^;]+;))$')

# Descriptors that must not appear in the bridge files (pollution guard).
BRIDGE_SMALI_PATTERN = re.compile(r'com/tw/music/media/')
POLLUTION_RE = re.compile(
    r'Landroidx/'
    r'|Lcom/google/android/exoplayer2/'
    r'|Lcom/google/android/exoplayer3/'
)


def parse_descriptor(method_desc: str):
    """Parse a method descriptor string into (args_list, return_type) or None on error."""
    if '->' not in method_desc:
        return None
    cls, rest = method_desc.split('->', 1)
    if not CLASS_DESC_RE.match(cls):
        return None
    m = re.match(r'[^\(]+\((.*)\)(.+)$', rest)
    if not m:
        return None
    args_blob, ret = m.groups()
    args = []
    i = 0
    while i < len(args_blob):
        c = args_blob[i]
        if c in 'ZBCSIFJD':
            args.append(c)
            i += 1
        elif c == 'L':
            j = args_blob.find(';', i)
            if j == -1:
                return None
            args.append(args_blob[i:j+1])
            i = j + 1
        elif c == '[':
            j = i
            while j < len(args_blob) and args_blob[j] == '[':
                j += 1
            if j >= len(args_blob):
                return None
            if args_blob[j] == 'L':
                k = args_blob.find(';', j)
                if k == -1:
                    return None
                args.append(args_blob[i:k+1])
                i = k + 1
            elif args_blob[j] in 'ZBCSIFJD':
                args.append(args_blob[i:j+1])
                i = j + 1
            else:
                return None
        else:
            return None
    if ret != 'V' and not DESC_RE.match(ret):
        return None
    return args, ret


def slots_for_arg(a: str):
    return 2 if a in ('J', 'D') else 1


def parse_reg_list(text: str):
    regs = [x.strip() for x in text.split(',') if x.strip()]
    for r in regs:
        if not re.match(r'^[vp]\d+$', r):
            return None
    return regs


def expected_move_result_op(ret: str) -> str:
    """Return the correct move-result opcode for a given return type."""
    if ret in ('J', 'D'):
        return 'move-result-wide'
    if ret.startswith('L') or ret.startswith('['):
        return 'move-result-object'
    return 'move-result'


issues = []
smali_files = sorted(SMALI_ROOT.glob('smali*/**/*.smali'))

for f in smali_files:
    lines = f.read_text(encoding='utf-8', errors='replace').splitlines()
    in_method = False
    pending_result_ok = False
    last_ret = None       # return type of most recent invoke (None = no pending result)

    is_bridge_file = BRIDGE_SMALI_PATTERN.search(str(f)) is not None

    for idx, line in enumerate(lines, 1):
        s = line.strip()

        # Pollution guard: check every line in bridge smali files
        if is_bridge_file and POLLUTION_RE.search(line):
            issues.append((f, idx, f'AndroidX/Media3 descriptor in bridge file: {s[:100]}'))

        if METHOD_RE.match(line):
            in_method = True
            pending_result_ok = False
            last_ret = None
            continue
        if END_METHOD_RE.match(line):
            in_method = False
            pending_result_ok = False
            last_ret = None
            continue
        if not in_method:
            continue

        mm = MOVE_RESULT_TYPED_RE.match(line)
        if mm:
            actual_op = mm.group(1).strip()
            if not pending_result_ok:
                issues.append((f, idx, 'move-result* not immediately after invoke/filled-new-array'))
            elif last_ret is not None:
                if last_ret == 'V':
                    issues.append((f, idx, f'move-result* after void-return invoke'))
                else:
                    expected_op = expected_move_result_op(last_ret)
                    if actual_op != expected_op:
                        issues.append((
                            f, idx,
                            f'move-result type mismatch: invoke returns {last_ret!r}, '
                            f'expected {expected_op!r} but got {actual_op!r}'
                        ))
            pending_result_ok = False
            last_ret = None
            continue

        if s and not s.startswith('.') and not s.startswith(':') and not s.startswith('#'):
            pending_result_ok = False
            last_ret = None

        fm = FILL_NEW_ARRAY_RE.match(line)
        if fm:
            pending_result_ok = True
            last_ret = fm.group(1)   # array type descriptor (e.g. [I, [Ljava/lang/String;)
            continue

        if s.startswith('invoke-'):
            pending_result_ok = True

        im = INVOKE_RE.match(line)
        if not im:
            continue
        op, regs_blob, method_desc = im.groups()
        pending_result_ok = True

        parsed = parse_descriptor(method_desc)
        if parsed is None:
            issues.append((f, idx, f'malformed method descriptor: {method_desc}'))
            last_ret = None
            continue

        args, ret = parsed
        last_ret = ret

        # Void return: move-result* after this would be a verifier error
        if ret == 'V':
            pending_result_ok = False  # no valid move-result can follow a void invoke

        is_static = 'invoke-static' in op
        expected_slots = sum(slots_for_arg(a) for a in args) + (0 if is_static else 1)
        if '/range' in op:
            rr = RANGE_RE.match(line)
            if not rr:
                issues.append((f, idx, 'malformed /range register syntax'))
                continue
            start, end, _ = rr.groups()
            if start[0] != end[0]:
                issues.append((f, idx, '/range must use same register family'))
                continue
            start_n = int(start[1:]); end_n = int(end[1:])
            actual_slots = end_n - start_n + 1
            if actual_slots != expected_slots:
                issues.append((f, idx, f'/range slots mismatch expected {expected_slots} got {actual_slots}'))
        else:
            regs = parse_reg_list(regs_blob)
            if regs is None:
                issues.append((f, idx, 'malformed invoke register list'))
                continue
            if len(regs) > 5:
                issues.append((f, idx, 'non-range invoke has more than 5 registers'))
            slot_count = len(regs)
            slot_ptr = 0
            all_args = ([None] if not is_static else []) + args
            for a in all_args:
                if slot_ptr >= len(regs):
                    break
                if a in ('J', 'D'):
                    if slot_ptr + 1 >= len(regs):
                        issues.append((f, idx, f'wide arg {a} missing second register'))
                    else:
                        r1, r2 = regs[slot_ptr], regs[slot_ptr + 1]
                        if r1[0] != r2[0]:
                            issues.append((f, idx,
                                f'wide arg {a} registers must use same family: {r1}, {r2}'))
                        elif int(r2[1:]) != int(r1[1:]) + 1:
                            issues.append((f, idx,
                                f'wide arg {a} registers must be consecutive: {r1}, {r2}'))
                    slot_ptr += 2
                else:
                    slot_ptr += 1
            if slot_count != expected_slots:
                issues.append((f, idx, f'invoke register count mismatch expected {expected_slots} got {slot_count}'))

for f, ln, msg in issues:
    print(f'{f}:{ln}: {msg}')

print(f'Scanned {len(smali_files)} smali files; issues={len(issues)}')
exit(1 if issues else 0)
