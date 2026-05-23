#!/usr/bin/env python3
import json,sys
from pathlib import Path

REQ_MAN=["pack_id","pack_kind","device_label","scenario_id","capture_phase","created_at","auxio_package","files","redaction_status","capture_tool_version"]
REQ_FILES=["media_session.txt","audio.txt","notification.txt","appwidget.txt","shortcut.txt","activity_top.txt","package_state.txt"]
STATUS=["pass","fail","partial","blocked","not_captured","needs_manual_review","invalid_pack"]

def main():
 if len(sys.argv)<2: print('usage: ts18-validate-evidence-pack.py <pack>'); return 1
 p=Path(sys.argv[1]); raw=p/'raw'; mani=p/'manifest.json'; d=p/'derived'; d.mkdir(exist_ok=True)
 out={"status":"invalid_pack","missing_required":[],"manifest_errors":[],"fixture":False}
 if not raw.exists() or not mani.exists():
  (d/'validation-summary.json').write_text(json.dumps(out,indent=2)+'\n'); return 1
 m=json.loads(mani.read_text()); out['fixture']=m.get('pack_kind')=='fixture'
 for k in REQ_MAN:
  if k not in m: out['manifest_errors'].append(k)
 miss=[x for x in REQ_FILES if not (raw/x).exists()]
 out['missing_required']=miss
 if out['manifest_errors']: out['status']='invalid_pack'
 elif len(miss)==0: out['status']='needs_manual_review'
 elif len(miss)>=5: out['status']='not_captured'
 elif 'capture-status.txt' in [x.name for x in raw.glob('*')]: out['status']='blocked'
 else: out['status']='partial'
 sig={}
 tx=lambda n:(raw/n).read_text(errors='replace').lower() if (raw/n).exists() else ''
 sig['auxio_package_installed']='org.oxycblt.auxio' in tx('package_state.txt') or 'org.oxycblt.auxio' in tx('packages.txt')
 sig['auxio_activity_visible']='org.oxycblt.auxio' in tx('activity_top.txt')
 sig['media_session_present']='org.oxycblt.auxio' in tx('media_session.txt')
 out['signals']=sig
 (d/'validation-summary.json').write_text(json.dumps(out,indent=2)+'\n')
 (d/'validation-summary.md').write_text(f"# Validation Summary\n- status: {out['status']}\n- fixture: {out['fixture']}\n")
 return 0
if __name__=='__main__': raise SystemExit(main())
