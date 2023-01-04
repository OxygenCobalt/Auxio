# Changelog

## 3.0.1

#### What's New
- Added support for album date ranges (ex. 2010 - 2013)

#### What's Improved
- Formalized whitespace handling
- Value lists are now properly localized
- Queue no longer primarily shows previous songs when opened
- Added reset button to ReplayGain pre-amp configuration dialog 

#### What's Changed
- R128 ReplayGain tags are now only used when playing OPUS files

#### What's Fixed
- Fixed mangled multi-value ID3v2 tags when UTF-16 is used
- Fixed crash when playing certain MP3 files
- Detail UI will no longer crash if the music library is unavailable

#### Dev/Meta
- Add CI workflow

## 3.0.0

#### What's New
- Added multi-value tags support
	- Added support for multiple artists
	- Added support for multiple genres
- Artists and album artists are now both given UI entires
	- Added setting to hide "collaborator" artists
- Upgraded music ID management:
	- Added support for MusicBrainz IDs (MBIDs)
	- Use a more unique hash of metadata when MBIDs can't be used
- Genres now display a list of artists
- Added toggle to load non-music (Such as podcasts)
- Music loader now caches parsed metadata for faster load times
- Redesigned icon
	- Added animated splash screen on Android 12+
- Added support for MP4 ReplayGain (`----`) atoms

#### What's Improved
- Sorting now takes accented characters into account
- Added support for compilation sub-release-types like (DJ) Mix
- Album dates now start from the earliest date instead of latest date
- Reshuffling the queue will no longer drop any songs you have added/removed
- Allowed light/dark theme to be customized on Android 12+
- All information now scrolls in the playback view
- A month is now shown for song/album dates when available
- Added loading indicator to song properties view
- List items have been made more compact

#### What's Fixed
- Fixed issue where the scroll popup would not display correctly in landscape mode [#230]
- Fixed issue where the playback progress would continue in the notification when
audio focus was lost
- Fixed issue where the artist name would not be shown in the OS audio switcher menu
- Fixed issue where the search view would not update if the library changed
- Fixed visual bug with transitions in the black theme
- Fixed toolbar flickering when fast-scrolling in the home UI

#### What's Changed
- Ignore MediaStore tags is now Auxio's default and unchangeable behavior. The option has been removed.
- "Use alternate notification action" is now "Custom notification action"
- "Show covers" and "Ignore MediaStore covers" have been unified into "Album covers"

#### Dev/Meta
- Created new wiki with more information about app functionality
- Switched to issue forms
- Completed migration to reactive playback system
- Refactor music backends into a unified chain of extractors
- Add bluetooth connection receiver (No functionality in app yet)

## 2.6.4

#### What's Fixed
- Fixed issue where invalid position values would crash the app
- Fixed issue where opening the song menu in the genre view would crash the app

## 2.6.3

#### What's New
- Improved playing indicators [#218]
    - Search and library now show playing indicators
    - Playing indicators are now animated when playback is ongoing
- Added smooth seeking [#130]

#### What's Improved
- Moved the "more" button in the playback view back to the top

#### What's Fixed
- Fixed issue where fast scroller popup would not appear

## 2.6.2

#### What's New
- Added Android 13 support [#129]
    - Switch to new storage permissions
    - Add themed icon
    - Fix issue where widget covers would not load
    - Use new media notification panel style
    - Add predictive back navigation
- Auxio has a new, more refined icon style
- Added a way to access the system equalizer from the playback menu

#### What's Improved
- Playback bar now has a marquee effect
- Play/pause button now changes from square to circle depending on the state

#### What's Fixed
- Fixed "@android:string/<ok/cancel>" strings from appearing in dialog
buttons
- Fixed issue where LG phones would not show metadata in the notification 
- Fixed issue where the transition would be wrong when navigating out of the
detail views after a rotation
- Fixed issue where widget resizing would be far different than intended
- Fixed broken playback layout on small portrait screens

#### What's Changed
- ReplayGain can now no longer be disabled. Remove ReplayGain tags from
files if such functionality is not desired

## 2.6.1

#### What's New
- Added basic equalizer support in external apps like Wavelet [#211]
- Added option to control the type of action shown on the playback bar
- Detail UI now displays the type of item shown (ex. the release type)

#### What's Improved
- Queue now scrolls to currently playing song instead of the song after it

#### What's Fixed
- Fixed incorrect font being used in the queue title
- Fixed missing fast scroll indicator with date added scrolling

#### What's Changed
- Use X-axis transitions instead of Z-axis (Avoids visual clipping)
- Queue no longer has rounded corners for consistency

## 2.6.0

#### What's New
- Added option to ignore `MediaStore` tags, allowing more correct metadata
at the cost of longer loading times
	- Added support for sort tags [#172, dependent on this feature]
	- Added support for date tags, including more fine-grained dates [#159, dependent on this feature]
	- Added support for release types signifying EPs, Singles, Compilations, and more [#158, dependent on this feature]
	- Added basic awareness of multi-value vorbis tags [#197, dependent on this feature]
- Completely reworked the main playback UI [#92]
	- Queue can now be swiped up
	- Playing song is now shown in queue
	- Added ability to play songs from queue
	- Added ability to see previous songs in queue
	- Added dual-pane view of queue and playback on landscape and tablets
- Added Last Added sorting
- Search now takes sort tags and file names in account [#184]
- Added option to clear playback state in settings
- Added ability to play songs from queue

#### What's Improved
- Migrated to better-looking motion transitions
- App now exposes an (immutable) queue to the MediaSession
- Improved widget sizing/cover management

#### What's Fixed
- Fixed default material theme being used before app shows up
- Fixed shuffle shortcut and file opening not working on startup on some devices
- Fixed issue where the notification position would not match if one seeked when paused
- Fixed issue where widget could not be sized to it's smallest form
- Fixed issue where restored state would override a song if it was played early enough
in startup 

#### What's Changed
- Play and skip icons are filled again
- Updated music hashing (Will wipe playback state)
- Removed unnecessary capitalization

#### Dev/Meta
- Migrated to BottomSheetBehavior

## 2.5.0

#### What's New
- Massively overhauled how music is loaded [#72]:
	- Auxio can now reload music without requiring a restart
	- Added a new option to reload music when device files change
- Added a shuffle shortcut
- Widgets now have a more sleek and consistent button layout
- "Rounded album covers" is now "Round mode"
- Added option to customize what occurs when a song is played from an album/artist/genre [#164]

#### What's Improved
- Made "timeline" elements (like playback controls) always left-to-right
- Improved performance when ReplayGain is not enabled

#### What's Fixed
- Fixed broken tablet layouts
- Fixed seam that would appear on some album covers
- Fixed visual issue with the queue opening animation
- Fixed miscellaneous startup issues
- Fixed crash if settings was navigated away before playback state
finished saving
- Fixed broken album menu
- Fixed crash that would occur when opening a file on some devices [#176]
- Fixed issue where the search filter menu would not display the correct mode
- Fixed crash when search filter mode was changed
- Fixed shuffle button appearing below playback bar on Android 10 and lower
- Fixed incorrect song being shown in the notification in some cases [#179]
- Fixed issue where toolbar will be clipped on Lollipop devices
- Fixed infinite loading if one had no music folders set [#182]

#### What's Changed
- Reworked typography and iconography to be more aligned with material design guidelines
- Old excluded directories from 2.3.1 will no longer be migrated

#### Dev/Meta
- Migrated preferences from shared object to utility
- Removed 2.0.0 compat code
- Updated ExoPlayer to 2.18.0
- Reworked sorting to be even more efficient

## v2.4.0

#### What's New
- Excluded directories has been revamped into "Music folders"
    - Folders on external drives can now be excluded [#134]
    - Added new "Include" option to restrict indexing to a particular folder [#154]
- Added a new view for song properties (Such as Bitrate) [#144]
- The playback bar now has a new design, with an improved progress indicator and a skip action

#### What's Improved
- The toolbar in the home UI now collapses when scrolling
- The toolbar layout is now consistent with Material Design 3
- Genre parsing now handles multiple integer values and cover/remix indicators (May wipe playback state)
- "Rounded album covers" option is no longer dependent on "Show album covers" option [#152]
- Added song actions to the playback panel
- Playback controls are now easier to reach when gesture navigation is enabled
- Added Play Next/Add to Queue options to artists and genres
- Covers in the detail views now show an indicator when playing
- Made dynamic colors the default color scheme on Android 12

#### What's Fixed
- Playback bar now picks the larger inset in case that gesture inset is missing [#149]
- Fixed unusable excluded directory UI
- Songs with no data (i.e size of 0) are now filtered out
- Fixed nonsensical menu items from appearing on songs
- Fixed issue where multiple menus would open if long-clicks occurred simultaneously

#### Dev/Meta
- New translations [Fjuro -> Czech, Konstantin Tutsch -> German]
- Moved music loading to a foreground service [#72]
- Phased out `ImageButton` for `MaterialButton`
- Unified icon sizing
- Properly handle volumes throughout the entire music loading process
- Added original date support to ExoPlayer parser (Not exposed in app)

## v2.3.1

#### What's Improved
- Loading UI is now more clear and easy-to-use
- Made album/artist/genre grouping order consistent (May change genre images)

#### What's Fixed
- Fixed crash when seeking to the end of a track as the track changed to a track with a lower duration 
- Fixed regression where GadgetBridge media controls would no longer work
- Fixed bug where music would be incorrectly reloaded on a hot restart
- Fixed issue where the album/artist/genre would not be correctly restored
- Fixed issue where items would not highlight properly in the detail UI
- Fixed hypothetical issue where widget would cause an OOM on large devices

#### Dev/Meta
- New translations [yurical -> Korean, qwerty287 -> German]
- Switched from `LiveData` to `StateFlow`
- Use `notifyItemChanged` instead of directly mutating `ViewHolder` instances.
- Added highly experimental ExoPlayer metadata backend (not enabled in-app)

## v2.3.0

#### What's New
- Added disc number support
- Added ReplayGain support for below-reference volume tracks [i.e positive ReplayGain values] 
- Added ReplayGain pre-amp customization
- About screen now shows counts for multiple types of library items, alongside a total duration
- New disc, track, song count, and duration sorting modes

#### What's Improved
- Re-enabled theme customization on Android 12
- The tab selector now hides itself when there is only one tab
- Made the cover on the thin widget larger

#### What's Fixed
- Fixed incorrect ellipsizing on song items
- Fixed a variety of esoteric crashes with queue state
- Fixed music indexing error when the OS would not provide a file name
- Fixed icon corruptions on lollipop devices

#### What's Changed
- Audio focus is no longer configurable
- Made the layout of album songs more similar to other songs

#### Dev/Meta
- Updated translations [Konstantin Tutsch -> German, cccClyde -> Chinese, Gsset -> Russian, enricocid -> Italian]
- Switched to spotless and ktfmt instead of ktlint
- Migrated constants to centralized table
- Introduced new RecyclerView framework
- Use native ExoPlayer AudioFocus implementation
- Make ReplayGain functionality use AudioProcessor instead of volume
- Removed databinding [Greatly reduces compile times]
- Start using Material You dialogs
- An uncountable amount of internal codebase improvements

## v2.2.2
#### What's New
- New spanish translations and metadata [courtesy of n-berenice]

#### What's Improved
- Rounded images are more nuanced
- Shuffle and Repeat mode buttons now have more contrast when they are turned on

#### What's Fixed
- Fixed crash on certain devices running Android 10 and lower when a differing theme
from the system theme was used [#80]
- Fixed music loading failure that would occur when certain paths were parsed [#84]
- Fixed incorrect track numbers when the tag was formatted as NN/TT [#88]
- Fixed years deliberately set as "0" showing up as "No Date"
- Fixed headset management unexpectedly starting audio when the app initially opens
- Fixed crash that would occur during a playback restore with specific queue states [#89]
- Partially fixed buggy behavior when multiple queue items were dragged in quick
succession

#### What's Changed
- All cover art is now cropped to a 1:1 aspect ratio
- Headset focus has been replaced with headset autoplay. It can no longer be disabled.

#### Dev/Meta
- Enabled elevation drop shadows below Android P for consistency
- Switches now have a disabled state
- Reworked dynamic color usage
- Reworked logging
- Upgrade ExoPlayer to v2.17.0 [Eliminates custom fork]

## v2.2.1
#### What's Improved
- Updated chinese translations [courtesy of cccClyde]
- Use proper material you top app bars
- Use body typography in correct places
- Expose file opening functionality better

#### What's Fixed
- Fixed issue where playback would start unexpectedly when opening the app

#### What's Changed
- Disabled audio focus customization on Android 12 [#75]

## v2.2.0
#### What's New:
- Added Arabic translations [Courtesy of hasanpasha]
- Improved Russian translations [Courtesy of lisiczka43]
- Added option to reload the music library

#### What's Improved:
- Songs now show their specific artist name instead of the name of the
artist they are grouped up in
- Artists are now grouped up case-insensitively
- Songs of different file formats are now grouped up into a single album
- Reworked typography slightly
- Invalid track numbers [i.e 0] will now be shown as a generic song icon

#### What's Fixed:
- Fixed crash on some devices configured to use French or Czech translations
- Malformed indices should now be corrected when the playback state is restored
- Fixed issue where track numbers would not be shown in the native language's numeric format
- Fixed issue where the preference view would apply the M3 switches inconsistently
- Fixed issue where the now playing indicator on the playback screen would use an internal name

#### Dev/Meta:
- Removed 1.4.X compat
- Added new changelog document
- Reworked contribution info and templates

## v2.1.0
#### What's New:
- Switched to a single queue system [i.e Play Next/Add to queue]
- Added ReplayGain support [Experimental]
- New russian translations [Courtesy of Vladimir Kosolapov]
- New chinese translations [Courtesy of cccClyde]
- Android 12L support
- Added option to round album covers for visual cohesion
- Added FLAC support for devices on Android Oreo and lower
- Added edge-to-edge support on devices on Android Oreo and lower

#### What's Improved:
- Increased mosaic quality
- Enabled black theme on Android 12+
- Content now fades when the playback view is expanding
- Improved layouts on small and large screens
- Improved how the app handles audio focus
- Improved how invalid years and durations are handled
- Use Material 3 switches in the settings menu

#### What's Fixed:
- Fixed issue where the playback view would be hard to swipe up
to when using gesture navigation
- Band-aided completely broken layouts in split screen mode
- Fixed crash in the playback view when a song's duration was 0
- Fixed issue where apps like GadgetBridge would not detect Auxio

#### Dev/Meta:
- ExoPlayer is now a local dependency
- Added ExoPlayer metadata support for Ogg Vorbis and Opus

## v2.0.1
#### What's Fixed:
- Fixed problem where the compact playback controls would not work
- Fixed unusable playback layout on small screens

## v2.0.0
#### What's New:
- Auxio has a new look derived from Material 3
- Material You support on Android 12
- Library and song view have been merged into a unified view
- Shuffle can now be accessed everywhere
- Media indexer now supports album artists
- Accents are now more vibrant and varied
- One can now slide up the compact playback view to reveal the full playback view
- Redesigned widgets to respect album art and increase visual cohesion
- Added song sorting [#16]
- Added default tab customization [#12]
- Added album, artist, and year sorting options
- Added descending order to all sorting options
- Added czech translations [Courtesy of Fjuro]
- Fast scroller has been replaced with a scrollbar with fast scroll capabilities

#### What's Improved:
- Improved playback persistence [State will be wiped]
- Improved accessibility everywhere
- Streamlined the search UI
- Improved queue UI
- Improved detail UI
- Unified appbar behavior
- Songs with accented characters will now show up in search when using their non-accented counterparts
- Removed loading screen
- Artist/Genre images now respect the "Ignore MediaStore Covers" setting
- Ascending order now works properly with years
- Fixed poor UI on Lollipop devices

#### What's Fixed:
- Switched to a new play icon [Fixes seam/alignment issues]
- Fixed issue where notifications would not be colored on samsung phones
- Re-added the german translations that were accidentally removed in 1.4.2
- Fixed issue where links could not be opened on Android 11+
- Fix crash that would occur when rotating the dialog
- Fixed issue where cover art could not be loaded at all on some devices [#51]
- Fixed issue where widgets would have unusable UIs on certain device configurations
- Fixed issue where older launchers will not show a widget preview on android 12
- Fixed duplicate songs appearing on some devices

#### What's Changed:
- Removed colorize notification option
- Removed deep orange and blue grey accents

#### Dev/Meta:
- Migrated to material entirely
- Reworked UI dimensions to line up with material design
- Use color selectors in more places
- Eliminated legacy size classifiers
- Created new architecture document

## v1.4.2
#### What's New:
- Added Widgets
- Android 12 support

#### What's Improved:
- Fast scroller now truncates more aggressively when there is not enough space
- Minor improvements to layout hierarchy
- Detail text/track numbers will no longer shrink
- Loading screen has been tweaked to line up with the rest of Auxio

#### What's Fixed:
- Fixed issue where the new about screen would be cut off in landscape mode
- Fixed issue where songs from two albums with the same year would be incorrectly shown in the artist view

#### Dev/Meta:
- Added license boilerplate

## v1.4.1
#### What's New:
- Added black dark theme
- Added a fast-scroller to the library view
- Redesigned the about screen
- Added full spanish translations [Courtesy of tesphil]
- Added an option to pause when a song repeats [#29]

#### What's Improved:
- Article sort is now used everywhere
- Improved german translations [Courtesy of qwerty287]

#### What's Fixed:
- Fixed problem where cover art would disappear on the lock screen
- Fixed problem where playback controls would not work on the lock screen [#20]
- Fixed issue where fast-scroller indicators would not line up for titles starting with "An"

#### Dev/Meta:
- Updated ExoPlayer to 2.14.2
- Completely refactored UI styling
- Added permission documentation [#22]
- Removed the `ACCESS_NETWORK_STATE` permission [#22]
- Added icon to metadata [#25]

## v1.4.0
#### What's New
- Artist view now shows a list of songs
- Loop functionality now has a new, more sensible behavior
- Dialogs have been revamped with a new style
- Added complete dutch translation [Courtesy of [timnea](https://github.com/timnea)]

#### What's Improved
- Changed the header font to be cleaner
- Completely rolled custom dialog system
- Blacklisted directories are now chosen through the built-in file picker
- Improved opening links in the about dialog
- Restore system now uses unique identifiers, increasing reliability and speed [Will wipe previous state]
- Grey accent in dark mode has been made more visible
- The queue will now reflect the current album/artist/genre sort
- Album/artist/genre sort is now remembered when the app restarts

#### What's Fixed
- Fixed issue where the scroll thumb would briefly display on the Songs UI
- Fixed issue where fast scrolling could be triggered outside the bounds of the indicators
- Fixed issue where the wrong playing item would be highlighted if the names were identical
- Fixed a crash when the thumb was moved above the fast scroller [Back-ported to 1.3.3, included in this release officially]

#### Dev/Meta
- Migrated fully to material design
- Int preferences are now used everywhere
- Upgraded ExoPlayer to 2.13.3
- Eliminated dependence on JCenter
- Eliminated Material Dialogs and Browser dependencies

## v1.3.3
#### What's Fixed
- Fixed crash that would occur when the app would shut down, preventing the playback state from being saved

#### Dev/Meta
- Explicitly declared dependencies
- Completely integrated fast-scroller code into codebase

## v1.3.2
#### What's New
- Added the ability to exclude directories from indexing [#6]
- Accents have been redone to improve visibility and UI simplicity
- Enabled wake lock functionality

#### What's Improved
- Queue UI no longer navigates away when playing from a file
- Songs UI no longer keeps scroll momentum when fast scrolling
- Improved handling of old genre names
- Changed the header font to be cleaner
- Improved mosaic quality

#### What's Fixed
- Fixed issue where prominent genre would display incorrectly on artist view 
- Fixed issue where AudioFocus would begin playback spontaneously
- Fixed issue where AudioFocus would not restore volume to 100% after ducking
- Fixed issue where the last item in the queue would be behind the navigation bar in edge-to-edge mode
- Fixed issues with the playback restore process (Current state will be wiped on update)
- Fixed buggy behavior when shuffle is toggled inside queue UI

#### Dev/Meta
- Updated exoplayer to 2.13.2
- Updated navigation to 2.3.4

## v1.3.1
#### What's New
- Added the ability to play a song from a file
- Added ability to manually save the playback state

#### What's Improved
- Optimized icons
- Updated the animation of the compact controls to be faster
- Songs without genres are now placed into an unknown genre

#### What's Fixed
- Fixed issue where the music load would fail from repeated genre applications [#4]
- Fixed crash that would occur on the songs UI due to bad music loading [#5]

#### Dev/Meta
- New tagline and description
- Rewrote loading UI
- Rewrote notification code

## v1.3.0
#### What's New:
- Added west-european translations [German, Spanish, French, Italian, Dutch, Portugese]
- Added east-european translations [Romanian, Greek, Russian, Ukranian, Polish, Hungarian]
- Added asian translations [Hindi, Indonesian, Chinese, Korean]
- Added middle-eastern translations [Turkish]

#### What's Improved:
- Optimized image loading even further
- Improved the UI on smaller tablets
- Updated the playback UIs to look better on all devices
- Improved the look of the play/pause button
- Compact controls slide up instead of fade in

#### What's Fixed:
- Fixed RTL layout issues
- Fixed elevation problems on the compact controls
- Fixed issue where a seam would show up on the play icon on certain displays
- Fixed issue where you could still collapse the toolbar on the search view with no results
- Fixed issue where an album would not show up as playing if played from the artist UI

#### Dev/Meta:
- Added fastlane metadata
- Updated Exoplayer to 2.12.3
- Updated Coil to 1.1.1
- Updated support libraries to 1.3.0
- Added architecture document
- Simplified themes

## v1.2.0
#### What's New
- The detail UIs have been redesigned to show the Play and Shuffle options front-and-center
- The Toolbars on the detail UIs have been made more visually appealing
- Images on the detail UIs now have a shadow applied to them
- Albums now have a "Go to artist" option in their menu
- Navigation has been made much for fluid and straightforward
- Search has been moved to a dedicated tab
- Added option to filter searches by Song, Album, Artist, and Genre

#### What's Improved
- The sorting menu is now a dedicated menu instead of an overflow menu, improving accessibility
- Disk-Caching with Coil is now completely turned off
- Tablet layouts have been made more visually appealing
- Made the icons in the Playback UI look better
- Queues are now properly sorted when not shuffled

#### What's Fixed
- Fixed issue where audio focus would resume after an interruption even if explicitly paused by the user
- Fixed a crash that would occur when a song with no genre was played from its genre
- Fixed a crash that would occur from the settings being accessed before they were created
- Fixed an issue where the keyboard will stay visible when navigating to something
- Fixed multiple memory leaks
- Fixed problem where the fast scroll indicator on the Songs UI would be slightly off
- Fixed issue where rewinding wouldn't cause the playback to start again
- Fixed problem where the artist play action wouldn't work

#### What's Changed
- "Remember Shuffle" is now on by default

## v1.1.0
#### What's New
- Rewrote the music loading system to be much faster
- Genres are now song-based instead of artist-based
- When an album is being played, that album will be highlighted in the artist UI
- If a song is playing from a genre, that song will be highlighted in the genre UI
- Switched to a new audio focus system that allows for volume reduction & auto-resuming
- Added option not to load cover art
- Added option to ignore MediaStore cover art
- Added option to play a song from its genre

#### What's Improved
- Made Genre/Artist/Album UIs more efficient
- Playback state restores are now more reliable if the music library changes
- Optimized ExoPlayer for audio playback
- Landscape support is now better for phones/tablets
- Optimized how Coil is used
- Items are now shown in two columns instead of three when a phone is in landscape

#### What's Fixed
- Stop the play/pause button from animating on the Now Playing screen
- Stopped coil from increasing the app size over time due to needless disk caching
- Enabled constant bitrate seeking, allowing for AAC/certain MP3s to be seekable

#### What's Changed
- Rewind threshold option has been removed
- "Play from artist", "Play from album", and "Play from All Songs" have been removed from the song menu in favor of "Go to artist" and "Go to album"
- The currently playing song on the Album UI will now only show if the song is actually playing from the album

## v1.0.0
- Initial release
