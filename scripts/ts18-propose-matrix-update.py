#!/usr/bin/env python3
import json,sys
from pathlib import Path

def main():
 p=Path(sys.argv[1]); d=p/'derived'; out=d/'matrix-proposals'; out.mkdir(parents=True,exist_ok=True)
 s=json.loads((d/'summary.json').read_text()); m=json.loads(Path('docs/templates/TS18_VALIDATION_SCENARIO_MAP.json').read_text())['scenarios']; by={x['scenario_id']:x for x in m}
 fixture=s.get('pack_kind')=='fixture'; items=[]
 for sid,row in s['scenario_results'].items():
  items.append({
   'scenario_id':sid,'surface':by[sid]['surface'],'current_matrix_row':by[sid]['matrix_row'],'proposed_status':row['status'],
   'evidence_references':['derived/summary.json'],'confidence_label':row['confidence_label'],'porting_decision_label':row['porting_decision_label'],
   'reasoning_summary':'Fixture-derived proposal; manual review required' if fixture else 'Evidence-derived proposal; manual review required',
   'manual_review_required':True,'tier3_candidate_status':'not eligible' if fixture else 'candidate if classifier gates satisfied'
  })
 (out/'matrix-update-proposal.json').write_text(json.dumps({'fixture_mode':fixture,'items':items},indent=2)+'\n')
 md=['# Matrix Update Proposal','',f'- fixture_mode: {fixture}','- canonical docs are NOT auto-edited','']
 for it in items: md.append(f"- {it['scenario_id']} | {it['proposed_status']} | {it['current_matrix_row']} | manual_review_required=true")
 (out/'matrix-update-proposal.md').write_text('\n'.join(md)+'\n')
 return 0
if __name__=='__main__': raise SystemExit(main())
