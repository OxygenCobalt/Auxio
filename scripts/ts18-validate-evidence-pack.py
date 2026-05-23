#!/usr/bin/env python3
import json
import sys
from pathlib import Path

REQ_MAN = [
    "pack_id",
    "pack_kind",
    "device_label",
    "scenario_id",
    "capture_phase",
    "created_at",
    "auxio_package",
    "files",
    "redaction_status",
    "capture_tool_version",
]
REQ_FILES = [
    "media_session.txt",
    "audio.txt",
    "notification.txt",
    "appwidget.txt",
    "shortcut.txt",
    "activity_top.txt",
    "package_state.txt",
]


def _write_summary(derived: Path, out: dict) -> None:
    derived.mkdir(parents=True, exist_ok=True)
    (derived / "validation-summary.json").write_text(
        json.dumps(out, indent=2) + "\n",
        encoding="utf-8",
    )
    (derived / "validation-summary.md").write_text(
        f"# Validation Summary\n- status: {out['status']}\n- fixture: {out['fixture']}\n",
        encoding="utf-8",
    )


def _read_lower(raw: Path, name: str) -> str:
    path = raw / name
    if not path.exists():
        return ""
    return path.read_text(encoding="utf-8", errors="replace").lower()


def main() -> int:
    if len(sys.argv) < 2:
        print("usage: ts18-validate-evidence-pack.py <pack>")
        return 1

    pack = Path(sys.argv[1])
    raw = pack / "raw"
    manifest = pack / "manifest.json"
    derived = pack / "derived"
    out = {
        "status": "invalid_pack",
        "missing_required": [],
        "manifest_errors": [],
        "fixture": False,
    }

    if not raw.is_dir() or not manifest.is_file():
        _write_summary(derived, out)
        return 1

    try:
        data = json.loads(manifest.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        out["manifest_errors"].append("manifest_json_invalid")
        _write_summary(derived, out)
        return 1

    out["fixture"] = data.get("pack_kind") == "fixture"
    out["manifest_errors"] = [key for key in REQ_MAN if key not in data]
    missing_required = [name for name in REQ_FILES if not (raw / name).exists()]
    out["missing_required"] = missing_required

    has_capture_status = (raw / "capture-status.txt").exists()
    if out["manifest_errors"]:
        out["status"] = "invalid_pack"
    elif has_capture_status:
        out["status"] = "blocked"
    elif len(missing_required) == 0:
        out["status"] = "needs_manual_review"
    elif len(missing_required) >= 5:
        out["status"] = "not_captured"
    else:
        out["status"] = "partial"

    out["signals"] = {
        "auxio_package_installed": (
            "org.oxycblt.auxio" in _read_lower(raw, "package_state.txt")
            or "org.oxycblt.auxio" in _read_lower(raw, "packages.txt")
        ),
        "auxio_activity_visible": "org.oxycblt.auxio" in _read_lower(raw, "activity_top.txt"),
        "media_session_present": "org.oxycblt.auxio" in _read_lower(raw, "media_session.txt"),
    }

    _write_summary(derived, out)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
