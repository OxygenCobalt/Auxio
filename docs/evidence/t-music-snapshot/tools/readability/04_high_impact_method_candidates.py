#!/usr/bin/env python3
from __future__ import annotations
import csv, datetime as dt, re, sys
from dataclasses import dataclass
from pathlib import Path
from collections import Counter

ROOT=Path('.')
SMALI_ROOTS=sorted(p for p in (ROOT/'app'/'apktool').glob('smali*') if p.is_dir())
OUT=ROOT/'docs'/'reports'/'readability-high-impact-methods.md'
LEDGER_MANUAL=ROOT/'docs'/'reports'/'readability-method-boundary-review.tsv'
LEDGER_GEN=ROOT/'docs'/'reports'/'readability-method-triage-generated.tsv'
PREV_TRUE_ACTIONABLE=1397
INCLUDE_PREFIXES=("Lcom/tw/music/","Lcom/eckom/xtlibrary/")
EXCLUDE_PREFIXES=("Landroid/","Landroidx/","Landroid/arch/","Landroid/support/","Lcom/bumptech/","Lcom/google/","Lorg/","Lantlr/","Lokhttp3/","Lokio/","Ljava/","Ljavax/","Ldalvik/")
LIFECYCLE={"onCreate","onDestroy","onStart","onStop","onResume","onPause","onStartCommand","onBind","onUnbind","onReceive","onClick","onAttachedToWindow","onDetachedFromWindow","onMeasure","onLayout","onDraw","onTouchEvent","onKeyDown","onKeyUp"}
LOW_VALUE={"getCount","getItem","getItemId","getView","getItemViewType","getViewTypeCount","isEnabled","areAllItemsEnabled","compare"}
ACTIVE={"unresolved_actionable","deferred_static","runtime_needed"}
RESOLVED={"confirmed","closed","low_value","generated_or_bridge","readable_framework_lifecycle","readable_android_callback","simple_delegate","trivial_accessor","constructor_or_clinit","no_readability_value","reviewed_but_grouped"}
ALL=ACTIVE|RESOLVED
CLASS_RE=re.compile(r"^\.class\b.*?(L[\w/$]+;)",re.M)
SOURCE_RE=re.compile(r'^\.source\s+"([^"]+)"',re.M)
METHOD_BLOCK_RE=re.compile(r"^\.method\s+(.+?)\s+([^\s(]+)\(([^)]*)\)(\S+)\s*\n(.*?)^\.end method",re.M|re.S)
SIG_RE=re.compile(r"(?:(?:public|private|protected|static|final|abstract|synthetic|bridge|native|strictfp|synchronized|declared-synchronized|constructor)\s+)*([^\s(]+)\(([^)]*)\)(\S+)")

@dataclass(frozen=True)
class C:
    desc:str; source:str; method:str; path:str; family:str; key:str; name:str; args:str; ret:str; flags:str; ins:int; invokes:int; fields:int; branches:int; is_return_only:bool

def norm_owner(owner):
    owner=owner.strip().strip('`')
    if owner.startswith('L'): owner=owner[1:]
    if owner.endswith(';'): owner=owner[:-1]
    return owner

def parse_sig(sig):
    m=SIG_RE.search(sig.strip().strip('`'))
    return (m.group(1),m.group(2),m.group(3)) if m else ('','','')

def canonical(owner,sig):
    n,a,r=parse_sig(sig); return f"{norm_owner(owner)}|{n}|{a}|{r}"

def should_include(desc): return desc.startswith(INCLUDE_PREFIXES) and not desc.startswith(EXCLUDE_PREFIXES)

def family(desc, source, name):
    t=f"{desc} {source} {name}"
    if '/b/f/' in desc or 'Music' in t: return 'Music'
    if '/b/a/' in desc or 'BT' in t: return 'BT'
    if '/b/h/' in desc or 'Radio' in t: return 'Radio'
    if '/b/k/' in desc or 'Video' in t: return 'Video'
    if '/b/d/' in desc or 'Launcher' in t: return 'Launcher'
    if 'Adapter' in t or 'ViewHolder' in t: return 'Adapter/ViewHolder'
    return 'Unknown app-owned'

def body_feats(body):
    lines=[x.strip() for x in body.splitlines() if x.strip() and not x.strip().startswith('.') and not x.strip().startswith(':') and not x.strip().startswith('#')]
    ins=len(lines)
    invokes=sum(1 for l in lines if l.startswith('invoke-'))
    fields=sum(1 for l in lines if l.startswith(('iget','iput','sget','sput')))
    branches=sum(1 for l in lines if l.startswith(('if-','packed-switch','sparse-switch','goto')))
    is_return_only=ins<=2 and all(l.startswith('return') or l.startswith('const/') for l in lines)
    return ins,invokes,fields,branches,is_return_only

def load_ledger(path):
    s={}
    if not path.exists(): return s
    with path.open() as f:
        for r in csv.DictReader(f,delimiter='\t'):
            st=r.get('status','').strip()
            if st and st in ALL:
                s[canonical(r.get('owner_descriptor',''),r.get('method_signature',''))]=st
    return s

def load_ledgers():
    # generated loaded first; manual loaded second so manual overrides generated
    s={}
    for p in (LEDGER_GEN,LEDGER_MANUAL):
        if not p.exists(): continue
        with p.open() as f:
            for r in csv.DictReader(f,delimiter='\t'):
                st=r.get('status','').strip()
                if st and st in ALL:
                    s[canonical(r.get('owner_descriptor',''),r.get('method_signature',''))]=st
    return s

def auto_status(c:C):
    fl=f" {c.flags} "
    if c.name in {'<init>','<clinit>'}: return 'constructor_or_clinit','constructor/static init'
    if ' synthetic ' in fl or ' bridge ' in fl: return 'generated_or_bridge','synthetic/bridge flag'
    if c.name in LIFECYCLE: return 'readable_framework_lifecycle','framework lifecycle callback'
    if c.name in LOW_VALUE or c.family=='Adapter/ViewHolder': return 'no_readability_value','adapter/comparator boilerplate'
    if c.name.startswith(('get','set','is')) and c.branches==0 and c.invokes<=1 and c.ins<=8: return 'trivial_accessor','small accessor'
    if c.is_return_only: return 'no_readability_value','return-only helper'
    if c.invokes==1 and c.branches==0 and c.fields<=1 and c.ins<=10 and c.name not in {'handleMessage','run','onReceive','onClick'}:
        return 'simple_delegate','single-call forwarding wrapper'
    if c.invokes<=1 and c.branches==0 and c.ins<=6 and c.name not in {'handleMessage','run'}:
        return 'no_readability_value','tiny helper no branching'
    if c.name in {'handleMessage','run','doInBackground','onPostExecute','onPreExecute'}: return 'unresolved_actionable','task/handler boundary'
    if ' abstract ' in fl and c.name.startswith('on'): return 'readable_android_callback','abstract Android callback'
    if ' abstract ' in fl: return 'unresolved_actionable','interface boundary'
    if c.branches>=2 or c.invokes>=3 or c.ins>=22: return 'unresolved_actionable','non-trivial flow/payload behavior'
    return 'reviewed_but_grouped','grouped alias/overload behind representative'

def main():
    cands=[]
    for root in SMALI_ROOTS:
        for p in sorted(root.rglob('*.smali')):
            txt=p.read_text(encoding='utf-8',errors='replace')
            cm=CLASS_RE.search(txt)
            if not cm: continue
            desc=cm.group(1)
            if not should_include(desc): continue
            source=SOURCE_RE.search(txt).group(1) if SOURCE_RE.search(txt) else ''
            for flags,name,args,ret,body in METHOD_BLOCK_RE.findall(txt):
                ins,inv,fields,br,is_ret=body_feats(body)
                sig=f"{flags} {name}({args}){ret}"
                cands.append(C(desc,source,sig,str(p),family(desc,source,name),canonical(desc,sig),name,args,ret,flags,ins,inv,fields,br,is_ret))

    # load_ledgers: generated first, manual second (manual takes precedence)
    existing=load_ledgers()
    manual=load_ledger(LEDGER_MANUAL)
    write_gen='--write-gen' in sys.argv
    gen_rows=[]; active=[]; all_statuses=[]
    for c in cands:
        st=existing.get(c.key)
        if not st:
            st,reason=auto_status(c)
            gen_rows.append({'family':c.family,'owner_descriptor':c.desc,'method_signature':c.method,'status':st,'reason':reason,'path':c.path})
        else:
            reason='preserved'
            if c.key not in manual:
                gen_rows.append({'family':c.family,'owner_descriptor':c.desc,'method_signature':c.method,'status':st,'reason':reason,'path':c.path})
        all_statuses.append(st)
        if st in ACTIVE: active.append((c,st))

    if write_gen:
        with LEDGER_GEN.open('w',encoding='utf-8',newline='') as f:
            w=csv.DictWriter(f,fieldnames=['family','owner_descriptor','method_signature','status','reason','path'],delimiter='\t')
            w.writeheader(); w.writerows(gen_rows)
        print(f"wrote {LEDGER_GEN}")

    counts=Counter(all_statuses)
    true_remaining=len(active)
    resolved_nonactionable=sum(counts[s] for s in RESOLVED)
    reviewed_total=len(all_statuses)
    reviewed_actionable=sum(counts[s] for s in ACTIVE)
    byfam=Counter(c.family for c,_ in active)

    lines=["# Readability High-Impact Method Candidates","",f"Generated: {dt.datetime.now(dt.timezone.utc).strftime('%Y-%m-%d %H:%M:%S UTC')}","",f"raw_method_candidates: {len(cands)}",f"reviewed_total: {reviewed_total}",f"resolved_nonactionable: {resolved_nonactionable}",f"reviewed_but_still_actionable: {reviewed_actionable}",f"true_remaining_actionable: {true_remaining}",f"previous_true_remaining_actionable: {PREV_TRUE_ACTIONABLE}","",'## Counts by status','']
    for s in sorted(ALL): lines.append(f"- {s}: {counts.get(s,0)}")
    lines += ["","## Active actionable by family","","| Family | Count |","|---|---:|"]
    for k,v in sorted(byfam.items(), key=lambda kv:(-kv[1],kv[0])): lines.append(f"| {k} | {v} |")
    lines += ["","## Top 150 true actionable rows","","| Family | Class | Source | Method | Status | Path |","|---|---|---|---|---|---|"]
    for c,st in sorted(active, key=lambda x:(x[0].family,x[0].desc,x[0].method))[:150]:
        lines.append(f"| {c.family} | `{c.desc}` | `{c.source}` | `{c.method}` | `{st}` | `{c.path}` |")
    OUT.write_text('\n'.join(lines)+'\n')
    print(f"wrote {OUT}")

if __name__=='__main__':
    main()
