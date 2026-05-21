#!/usr/bin/env python3
import json
import sys
from pathlib import Path

_SCENARIO_MAP = Path(__file__).resolve().parents[1] / "docs/templates/TS18_VALIDATION_SCENARIO_MAP.json"
_EXPECTED_SCENARIOS = [f"TS18-STD-{i:03d}" for i in range(1, 18)]


def _load_scenarios():
    if _SCENARIO_MAP.exists():
        data = json.loads(_SCENARIO_MAP.read_text())
        ids = [s["id"] for s in data.get("scenarios", [])]
        if sorted(ids) != _EXPECTED_SCENARIOS or len(set(ids)) != len(ids):
            raise ValueError("Scenario map must contain unique IDs TS18-STD-001..017")
        return ids
    print(f"WARNING: {_SCENARIO_MAP} not found; using generated scenario ID list. Verify sync.", file=sys.stderr)
    return _EXPECTED_SCENARIOS

SCENARIOS = _load_scenarios()


def read(path: Path):
    if not path.exists():
        return ""
    return path.read_text(encoding="utf-8", errors="replace")


def has_any(text, *needles):
    low = text.lower()
    return any(needle.lower() in low for needle in needles)


def has_all(text, *needles):
    low = text.lower()
    return all(needle.lower() in low for needle in needles)


def main():
    if len(sys.argv) < 2:
        print("usage: ts18-summarise-evidence-pack.py <pack_dir>")
        return 1
    pack = Path(sys.argv[1])
    raw = pack / "raw"
    derived = pack / "derived"
    derived.mkdir(exist_ok=True)
    files = {k: raw / f"{k}.txt" for k in ["media_session", "notification", "audio", "appwidget", "shortcut", "activity"]}
    text = {k: read(v) for k, v in files.items()}
    missing = [k for k, v in files.items() if not v.exists()]

    signals = {
      "auxio_in_media_session": has_any(text["media_session"], "org.oxycblt.auxio"),
      "metadata_fields_present": all(has_any(text["media_session"], key) for key in ["title", "artist", "album"]),
      "playback_state_present": has_any(text["media_session"], "state=", "playbackstate"),
      "auxio_notification_present": has_any(text["notification"], "org.oxycblt.auxio"),
      "notification_media_controls_present": has_any(text["notification"], "play", "pause", "next", "previous"),
      "widget_reference_present": has_all(text["appwidget"], "auxio", "widget"),
      "shortcut_reference_present": has_all(text["shortcut"], "auxio", "shortcut"),
      "audio_focus_reference_present": has_any(text["audio"], "org.oxycblt.auxio", "focus"),
      "open_actions_attempted": (raw / "action_open_now_playing.txt").exists() and (raw / "action_open_queue.txt").exists(),
    }

    def status(flag):
        return "pass" if flag else "requires manual review"

    scenario_results = {
        scenario: {
            "status": "not captured",
            "confidence": "Requires TS18 validation",
            "portingDecision": "Requires TS18 runtime validation",
            "nativeInvestigationCandidate": False,
            "notes": "No scenario-specific parser yet.",
        }
        for scenario in SCENARIOS
    }
    scenario_results["TS18-STD-001"]["status"] = status(signals["auxio_in_media_session"] and signals["metadata_fields_present"])
    scenario_results["TS18-STD-002"]["status"] = status(signals["auxio_notification_present"] and signals["notification_media_controls_present"])
    scenario_results["TS18-STD-003"]["status"] = "blocked" if not signals["open_actions_attempted"] else "requires manual review"
    scenario_results["TS18-STD-011"]["status"] = status(signals["widget_reference_present"])
    if signals["auxio_in_media_session"] and signals["auxio_notification_present"] and not signals["widget_reference_present"]:
        scenario_results["TS18-STD-011"]["nativeInvestigationCandidate"] = True
        scenario_results["TS18-STD-011"]["notes"] = "Widget signal absent despite core media signals; manual parity investigation candidate."

    (derived / "scenario-results.json").write_text(json.dumps({"missingFiles": missing, "signals": signals, "scenarioResults": scenario_results}, indent=2))
    (derived / "validation-summary.md").write_text("\n".join([
      "# TS18 Validation Summary",
      f"- Missing files: {', '.join(missing) if missing else 'none'}",
      *[f"- {k}: {'Observed' if v else 'Requires manual review'}" for k, v in signals.items()]
    ]))
    gaps = [k for k, v in scenario_results.items() if v["status"] in {"fail", "blocked", "requires manual review", "not captured"}]
    (derived / "parity-gap-candidates.md").write_text("# Parity Gap Candidates\n" + "\n".join([f"- {gap}" for gap in gaps]))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
