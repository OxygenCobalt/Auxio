#!/usr/bin/env python3
import json,sys
from pathlib import Path

def det(flag): return 'detected' if flag else 'not_detected'
def main():
 p=Path(sys.argv[1]); raw=p/'raw'; d=p/'derived'; d.mkdir(exist_ok=True)
 t=lambda n:(raw/n).read_text(errors='replace').lower() if (raw/n).exists() else ''
 sig={
  'auxio_package_installed':det('org.oxycblt.auxio' in t('package_state.txt') or 'org.oxycblt.auxio' in t('packages.txt')),
  'auxio_activity_visible':det('org.oxycblt.auxio' in t('activity_top.txt')),
  'media_session_entry':det('org.oxycblt.auxio' in t('media_session.txt')),
  'metadata_title_artist':det('title' in t('media_session.txt') and 'artist' in t('media_session.txt')),
  'playback_state':det('state=' in t('media_session.txt') or 'playbackstate' in t('media_session.txt')),
  'notification_entry':det('org.oxycblt.auxio' in t('notification.txt')),
  'notification_actions':det(any(x in t('notification.txt') for x in ['play','pause','next','previous'])),
  'widget_visibility':det('auxio' in t('appwidget.txt')),
  'shortcut_entries':det('auxio' in t('shortcut.txt')),
  'audio_focus_hints':det('focus' in t('audio.txt') or 'duck' in t('audio.txt')),
  'media_key_traces':det('keycode_media' in t('logcat_filtered.txt') or 'headset' in t('logcat_filtered.txt')),
  'zlink_tlink_hints':det('zlink' in t('packages_filtered.txt') or 'tlink' in t('packages_filtered.txt')),
  'twtheme_ilauncher_hints':det('theme' in t('packages_filtered.txt') or 'ilauncher' in t('packages_filtered.txt'))
 }
 m=json.loads((p/'manifest.json').read_text())
 scenarios={}
 for i in range(1,18):
  sid=f'TS18-STD-{i:03d}'; scenarios[sid]={"status":"needs_manual_review","confidence_label":"Requires TS18 validation","porting_decision_label":"Requires TS18 runtime validation"}
 if sig['media_session_entry']=='detected': scenarios['TS18-STD-008']['status']='partial'
 if sig['notification_entry']=='detected': scenarios['TS18-STD-015']['status']='partial'
 if sig['widget_visibility']=='not_detected' and sig['media_session_entry']=='detected': scenarios['TS18-STD-004']['status']='partial'
 out={'pack_kind':m.get('pack_kind','real'),'signal_detection':sig,'scenario_results':scenarios}
 (d/'summary.json').write_text(json.dumps(out,indent=2)+'\n')
 (d/'summary.md').write_text('# Summary\n'+'\n'.join([f"- {k}: {v}" for k,v in sig.items()])+'\n')
 return 0
if __name__=='__main__': raise SystemExit(main())
