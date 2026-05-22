#!/usr/bin/env python3
import json,sys
from pathlib import Path

def main():
    if len(sys.argv)<2:
        print("usage: ts18-propose-gap-matrix-update.py <pack_dir>"); return 1
    pack=Path(sys.argv[1]); derived=pack/"derived"
    data=json.loads((derived/"scenario-results.json").read_text())
    out=["# Proposed TS18 Native Parity Gap Matrix Update","","Generated from evidence summary; review manually before applying.",""]
    for sid,row in data["scenarioResults"].items():
      status=row["status"]
      need="No" if status=="pass" else ("Maybe" if status=="requires manual review" else "Yes")
      out+= [f"## {sid}",f"- Evidence result: {status}",f"- Suggested Native/private investigation needed: {need}",f"- Suggested confidence: {row['confidence']}",f"- Suggested porting decision: {row['portingDecision']}","- Suggested next action: Collect more TS18 hardware evidence before any Tier 3 proposal.",""]
    (derived/"parity-gap-matrix-update.md").write_text("\n".join(out))
    return 0
if __name__=='__main__': raise SystemExit(main())
