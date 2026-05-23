#!/usr/bin/env python3
import json,sys
from pathlib import Path

def main():
 p=Path(sys.argv[1]); smap=json.loads(Path(sys.argv[2]).read_text())['scenarios']; by={x['scenario_id']:x for x in smap}
 s=json.loads((p/'derived/summary.json').read_text()); outd=p/'derived/gap-candidates'; outd.mkdir(parents=True,exist_ok=True)
 fixture=s.get('pack_kind')=='fixture'; gen=[]
 for sid,row in s['scenario_results'].items():
  st=row['status']
  if st not in {'fail','partial'}: continue
  cls='insufficient evidence'; tier3='not eligible'
  sig=s['signal_detection']
  if sig['media_session_entry']=='not_detected' and sig['auxio_package_installed']=='not_detected': cls='hardware/setup blocked'
  elif st=='fail': cls='likely Tier 1 app bug'
  elif st=='partial' and sig['media_session_entry']=='detected': cls='likely TS18 parity gap'
  else: cls='needs manual review'
  if cls=='likely TS18 parity gap' and not fixture and by[sid]['native_investigation_trigger']!='none': tier3='candidate'
  md=outd/f'{sid}.md'; md.write_text(f"# {sid}\n- classification: {cls}\n- tier3_candidate_status: {tier3}\n- confidence_label: {row['confidence_label']}\n- porting_decision_label: {row['porting_decision_label']}\n- evidence_references: derived/summary.json\n- next_action: collect real TS18 evidence and manual review.\n")
  gen.append(str(md))
 (outd/'index.json').write_text(json.dumps({'fixture_mode':fixture,'generated':gen},indent=2)+'\n')
 return 0
if __name__=='__main__': raise SystemExit(main())
