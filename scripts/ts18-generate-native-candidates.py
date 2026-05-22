#!/usr/bin/env python3
import json, re, sys
from pathlib import Path

MAP=Path('docs/templates/TS18_VALIDATION_SCENARIO_MAP.json')
TPL=Path('docs/templates/TS18_NATIVE_INVESTIGATION_CANDIDATE.md')


def slug(text):
    return re.sub(r'[^a-z0-9]+','-',text.lower()).strip('-')


def main():
    if len(sys.argv)<2:
        print('usage: ts18-generate-native-candidates.py <pack_dir>')
        return 1
    pack=Path(sys.argv[1]); derived=pack/'derived'
    results=json.loads((derived/'scenario-results.json').read_text())
    smap=json.loads(MAP.read_text())
    by_id={s['id']:s for s in smap['scenarios']}
    out_dir=derived/'native-investigation-candidates'
    out_dir.mkdir(exist_ok=True)
    generated=[]
    for sid,row in results['scenarioResults'].items():
        st=row.get('status','not captured')
        if st in {'pass','not captured'}:
            continue
        if st in {'blocked'} and any(k in (results.get('missingFiles') or []) for k in ['media_session','notification','audio','appwidget','shortcut','activity']):
            continue
        if st != 'fail' and not row.get('nativeInvestigationCandidate', False):
            continue
        meta=by_id.get(sid,{})
        if st == 'requires manual review':
            priority='medium'
        elif st == 'fail':
            priority='high'
        else:
            priority='low'
        area=(meta.get('parityRows') or ['unknown-area'])[0]
        fn=out_dir/f"TS18-NATIVE-CANDIDATE-{slug(area)}-{sid}.md"
        txt=f"""# TS18 Native/Private Investigation Candidate

- Parity gap summary: {sid} in area '{area}' has status '{st}'.
- User-visible impact: {meta.get('user_impact','degrades head-unit UX')}
- Affected TS18/TWTHEME surface: {area}
- Evidence pack link: {pack}
- Failed Tier 1 mechanism: Android-standard Tier 1 path did not fully satisfy parity signal.
- Candidate native/private contract: Evidence-only placeholder; manual triage required against inventory.
- Source/evidence basis: {pack/'derived/scenario-results.json'} and {pack/'derived/parity-gap-matrix-update.md'}
- Risk classification: Requires human review before any implementation.
- Privilege requirements: Unknown; assume non-privileged only unless proven otherwise.
- Package identity requirements: Must not require com.tw.music impersonation.
- Non-impersonation feasibility: Required.
- Fallback behavior: Retain Tier 1 Android-standard behavior.
- Non-TS18 behavior impact: Must remain unchanged.
- Validation plan: External-only validation on TS18 hardware.
- Rollback plan: Do not merge production code without separate approved design PR.

## Generated metadata
- Scenario: {sid}
- Suggested priority: {priority}
- Confidence: {row.get('confidence','Requires TS18 validation')}
- Porting decision: {row.get('portingDecision','Requires TS18 runtime validation')}
- Manual review required: yes
"""
        fn.write_text(txt)
        generated.append(str(fn))
    (derived/'native-candidate-index.json').write_text(json.dumps({'generated':generated},indent=2))
    print(f'Generated {len(generated)} candidate drafts in {out_dir}')
    return 0

if __name__=='__main__':
    raise SystemExit(main())
