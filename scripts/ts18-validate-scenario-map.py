#!/usr/bin/env python3
import json,sys
from pathlib import Path

CONF={"Observed","Inferred","Hypothesis","Requires TS18 validation","Unsupported"}
PORT={"Directly reusable requirement","Reusable validation idea","Useful as evidence only","Obsolete due to Auxio architecture","Requires TS18 runtime validation","Unsafe to port","Should be explicitly avoided"}
REQ=["scenario_id","surface","goal","tier1_implementation","evidence_required","adb_commands","manual_steps","expected_android_standard_signal","expected_ts18_surface_signal","pass_condition","fail_condition","partial_condition","blocked_condition","matrix_row","native_investigation_trigger","confidence_label","porting_decision_label","severity","user_impact","native_investigation_priority"]

def main():
 p=Path(sys.argv[1] if len(sys.argv)>1 else 'docs/templates/TS18_VALIDATION_SCENARIO_MAP.json')
 d=json.loads(p.read_text())
 sc=d.get('scenarios',[])
 ids=[]; errs=[]
 for i,s in enumerate(sc):
  for k in REQ:
   if k not in s: errs.append(f"missing {k} at index {i}")
  sid=s.get('scenario_id','')
  ids.append(sid)
  if not sid.startswith('TS18-STD-'): errs.append(f'bad scenario_id {sid}')
  if s.get('confidence_label') not in CONF: errs.append(f'bad confidence {sid}')
  if s.get('porting_decision_label') not in PORT: errs.append(f'bad porting {sid}')
 if len(sc)!=17: errs.append(f'expected 17 scenarios got {len(sc)}')
 expected=[f'TS18-STD-{i:03d}' for i in range(1,18)]
 if sorted(ids)!=expected: errs.append('scenario ids must be TS18-STD-001..017 exactly once')
 if errs:
  print('\n'.join(errs)); return 1
 print('scenario map valid')
 return 0
if __name__=='__main__': raise SystemExit(main())
