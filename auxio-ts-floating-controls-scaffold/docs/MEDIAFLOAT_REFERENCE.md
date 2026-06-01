# MediaFloat Reference Notes

MediaFloat is the closest open-source behaviour reference found for this work.

Relevant ideas to study and adapt:

- narrow floating media-control purpose;
- compact overlay bar;
- previous/play-pause/next controls;
- saved overlay position;
- width/opacity/theme settings;
- foreground service posture;
- readiness checks;
- start/stop/toggle launcher shortcuts;
- recovery affordance such as triple-tap stop.

Do not copy blindly. Auxio-TS should not need notification listener control of another app. It should control its own playback directly.

Licence note: MediaFloat is described as Apache-2.0 licensed. If any code is copied, preserve licence notices and add clear attribution in Auxio-TS notices. Prefer clean-room implementation of the concept unless direct code reuse is clearly beneficial and legally handled.
