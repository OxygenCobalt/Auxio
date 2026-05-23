#!/usr/bin/env python3
import json
import sys
from pathlib import Path


def det(flag):
    return "detected" if flag else "not_detected"


def _read_lower(raw: Path, name: str) -> str:
    file = raw / name
    if not file.exists():
        return ""
    return file.read_text(encoding="utf-8", errors="replace").lower()


def _write_invalid_summary(derived: Path, reason: str) -> None:
    out = {
        "status": "invalid_pack",
        "reason": reason,
        "pack_kind": "unknown",
        "signal_detection": {},
        "scenario_results": {},
    }
    derived.mkdir(parents=True, exist_ok=True)
    (derived / "summary.json").write_text(json.dumps(out, indent=2) + "\n", encoding="utf-8")
    (derived / "summary.md").write_text(
        f"# Summary\n- status: invalid_pack\n- reason: {reason}\n",
        encoding="utf-8",
    )


def main():
    if len(sys.argv) < 2:
        print("usage: ts18-summarise-validation-pack.py <pack>", file=sys.stderr)
        return 1

    pack = Path(sys.argv[1])
    raw = pack / "raw"
    derived = pack / "derived"
    manifest = pack / "manifest.json"

    if not pack.is_dir():
        _write_invalid_summary(derived, f"pack_not_found:{pack}")
        return 1
    if not raw.is_dir():
        _write_invalid_summary(derived, "raw_missing")
        return 1
    if not manifest.is_file():
        _write_invalid_summary(derived, "manifest_missing")
        return 1

    try:
        manifest_data = json.loads(manifest.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        _write_invalid_summary(derived, "manifest_json_invalid")
        return 1

    sig = {
        "auxio_package_installed": det(
            "org.oxycblt.auxio" in _read_lower(raw, "package_state.txt")
            or "org.oxycblt.auxio" in _read_lower(raw, "packages.txt")
        ),
        "auxio_activity_visible": det("org.oxycblt.auxio" in _read_lower(raw, "activity_top.txt")),
        "media_session_entry": det("org.oxycblt.auxio" in _read_lower(raw, "media_session.txt")),
        "metadata_title_artist": det(
            "title" in _read_lower(raw, "media_session.txt")
            and "artist" in _read_lower(raw, "media_session.txt")
        ),
        "playback_state": det(
            "state=" in _read_lower(raw, "media_session.txt")
            or "playbackstate" in _read_lower(raw, "media_session.txt")
        ),
        "notification_entry": det("org.oxycblt.auxio" in _read_lower(raw, "notification.txt")),
        "notification_actions": det(
            any(
                x in _read_lower(raw, "notification.txt")
                for x in ["play", "pause", "next", "previous"]
            )
        ),
        "widget_visibility": det("auxio" in _read_lower(raw, "appwidget.txt")),
        "shortcut_entries": det("auxio" in _read_lower(raw, "shortcut.txt")),
        "audio_focus_hints": det(
            "focus" in _read_lower(raw, "audio.txt")
            or "duck" in _read_lower(raw, "audio.txt")
        ),
        "media_key_traces": det(
            "keycode_media" in _read_lower(raw, "logcat_filtered.txt")
            or "headset" in _read_lower(raw, "logcat_filtered.txt")
        ),
        "zlink_tlink_hints": det(
            "zlink" in _read_lower(raw, "packages_filtered.txt")
            or "tlink" in _read_lower(raw, "packages_filtered.txt")
        ),
        "twtheme_ilauncher_hints": det(
            "theme" in _read_lower(raw, "packages_filtered.txt")
            or "ilauncher" in _read_lower(raw, "packages_filtered.txt")
        ),
    }

    scenarios = {}
    for i in range(1, 18):
        sid = f"TS18-STD-{i:03d}"
        scenarios[sid] = {
            "status": "needs_manual_review",
            "confidence_label": "Requires TS18 validation",
            "porting_decision_label": "Requires TS18 runtime validation",
        }

    if sig["media_session_entry"] == "detected":
        scenarios["TS18-STD-008"]["status"] = "partial"
    if sig["notification_entry"] == "detected":
        scenarios["TS18-STD-015"]["status"] = "partial"
    if (
        sig["widget_visibility"] == "not_detected"
        and sig["media_session_entry"] == "detected"
    ):
        scenarios["TS18-STD-004"]["status"] = "partial"

    out = {
        "pack_kind": manifest_data.get("pack_kind", "real"),
        "status": "ok",
        "signal_detection": sig,
        "scenario_results": scenarios,
    }
    derived.mkdir(parents=True, exist_ok=True)
    (derived / "summary.json").write_text(json.dumps(out, indent=2) + "\n", encoding="utf-8")
    (derived / "summary.md").write_text(
        "# Summary\n" + "\n".join([f"- {k}: {v}" for k, v in sig.items()]) + "\n",
        encoding="utf-8",
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
