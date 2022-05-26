# Architecture
This document is designed to provide an overview of Auxio's architecture and design decisions. It will be updated as Auxio changes.

## Core Facets
Auxio has a couple of core systems or concepts that should be understood when working with the codebase.

#### Package Structure
Auxio's package structure is strictly feature-oriented. For example, playback code is exclusively in the `playback` package,
and detail code is exclusively in the `detail` package. Sub-packages can be related to the code it contains, such as `detail.recycler`
for the detail UI adapters, or they can be related to a sub-feature, like `playback.queue` for the queue UI.

The outliers here are `.ui` and `.util`, which are generic utility or component packages.

A full run-down of Auxio's current package structure as of the latest version is shown below.

```
org.oxycblt.auxio  # Main UIs
├──.detail         # Album/Artist/Genre detail UIs
│  └──.recycler    # RecyclerView components for detail UIs 
├──.home           # Home UI
│  ├──.fastscroll  # Fast scroller UI
│  ├──.list        # Home item lists
│  └──.tabs        # Home tab customization
├──.image          # Image loading components
├──.music          # Music data and loading
│  └──.excluded    # Excluded Directories UI + Systems
├──.playback       # Playback UI + Systems
│  ├──.queue       # Queue UI
│  ├──.replaygain  # ReplayGain System + UIs
│  ├──.state       # Playback state backend
│  └──.system      # System-side playback [Services, ExoPlayer]
├──.search         # Search UI
├──.settings       # Settings UI + Systems
│  └──.pref        # Int preference add-on
├──.ui             # Shared views and models
│  └──.accent      # Color Scheme UI + Systems
├──.util           # Shared utilities
└──.widgets        # AppWidgets
```

Each package is gone over in more detail later on.

#### UI Structure
Auxio only has one activity, `MainActivity`. Do not try to add more activities to the codebase. Instead, a new UI
should be added as a new `Fragment` implementation and added to one of the two navigation graphs:

- `nav_main`: Navigation *from* `MainFragment`
- `nav_explore`: Navigation *in* `MainFragment` 

Fragments themselves are based off a super class called `ViewBindingFragment` that takes a view-binding and then
leverages it within the fragment lifecycle. 

- Create variables [Bindings, Adapters, etc]
- Set up the UI
- Set up ViewModel instances and LiveData observers

`findViewById` is to **only** be used when interfacing with non-Auxio views. Otherwise, view-binding should be
used in all cases. Code that involves retrieving the binding should be isolated into its own function, with
the binding being obtained by calling `requireBinding`.

At times it may be more appropriate to use a `View` instead of a full blown fragment. This is okay as long as
view-binding is still used.

Auxio uses `RecyclerView` for all list information. Due to the complexities of Auxio, the way one defines an
adapter differs quite heavily from the normal library. Generally, start with `MonoAdapter` for a list with one
type of data and `MultiAdapter` for lists with many types of data, then follow the documentation to see how
to fully implement the class.

#### Object communication
Auxio's codebase is mostly centered around 4 different types of code that communicates with each-other.

- UIs: Fragments, RecyclerView items, and Activities are part of this class. All of them should have little data logic
in them and should primarily focus on displaying information in their UIs.
- ViewModels: These usually contain data and values that a UI can display, along with doing data processing. The data
often takes the form of `MutableLiveData` or `LiveData`, which can be observed.
- Shared Objects: These are the fundamental building blocks of Auxio, and exist at the process level. These are usually
retrieved using `getInstance` or a similar function. Shared Objects should be avoided in UIs, as their volatility can
cause problems. Its better to use a ViewModel and their exposed data instead.
- Utilities: These are largely found in the `.util` package, taking the form of standalone or extension functions 
that can be used anywhere.

Ideally, UIs should only be talking to ViewModels, ViewModels should only be talking to the Shared Objects, and Shared Objects
should only be talking to other shared objects.  All objects can use the utility functions where appropriate.

#### Data objects
Auxio represents data in multiple ways.

`Item` is the base class for most music and UI data in Auxio, with a single ID field meant to mark it as unique.

It has the following implementations:
- `Music` is a `Item` that represents music. It adds a `name` field that represents the raw name of the music (from `MediaStore`),
and a `resolveName` method meant to resolve the name in context of the UI.
- `MusicParent` is a type of `Music` that contains children. 
- `Header` corresponds to a simple header with a title and no interaction functionality. There are also the detail-specific
`DiscHeader` and `SortHeader`, however these are largely unrelated to `Header`.

Other data types represent a specific UI configuration or state:
- Sealed classes like `Sort` contain an ascending state that can be modified immutably.
- Enums like `DisplayMode` and `RepeatMode` only contain static data, such as a string resource.

Things to keep in mind while working with music data:
- `id` is not derived from the `MediaStore` ID of the music data. It is actually a hash of the unique fields of the music data.
Attempting to use it as a `MediaStore` ID will result in errors.
- Any field or method beginning with `internal` is off-limits. These fields are meant for use within `MusicLoader` and generally
provide poor UX to the user. The only reason they are public is to make the loading process not have to rely on separate "Raw"
objects.
- `rawName` is used when doing internal work, such as saving music data or diffing items
- `sortName` is used in the fast scroller indicators and sorting. Avoid it wherever else.
- `resolveName()` should be used when displaying any kind of music data to the user.
- For songs, `individualArtistRawName` and `resolveIndividualArtistName` should always be used when displaying the artist of
a song, as it will always show collaborator information first before defaulting to the album artist.

#### Music Access
All music on a system is asynchronously loaded into the shared object `MusicStore`. More specifically, it is accessible within
the `Library` construct. By the nature of music loading, **`Library` may not be available at all times.**

- ViewModels should try to await or gracefully exit the called method if `Library` is not available
- In the case that a ViewModel needs a `Library` instance to function, it can be asserted with `requireNotNull`. This should be done sparingly.
- Other shared objects that rely on `MusicStore` [like `PlaybackStateManager`] will no-op if music is not available.

If the loading status needs to be shown in a UI, `MusicViewModel` can be used to observe the current music loader response.

#### Playback System
Auxio's playback system is somewhat unorthodox, as it avoids much of the android-provided APIs in favor of a more controllable and sensible system.
The diagram below highlights the overall structure and connections:

```
                   ┌──────────────────── PlaybackService ────────────────┐
                   │                             │                       │
PlaybackStateManager [Communicates with]         │                       │
                   │                             │ [Contains]            │
                   │                             │                       │
                   │                             ├ WidgetComponent       ┤
                   │                             ├ NotificationComponent ┤
                   │                             ├ MediaSessionComponent ┤
                   │                             └ Player                ┘
                   │
                   │
                   └──────────────────── PlaybackViewModel  ───────────────────── UIs
                                                             [Communicates With]
```

`PlaybackStateManager` is the shared object that contains the master copy of the playback state, doing all operations on it. This object should
***NEVER*** be used in a UI, as it does not sanitize input and can cause major problems if a Volatile UI interacts with it. It's callback system
is also prone to memory leaks if not cleared when done. `PlaybackViewModel` should be used instead, as it exposes stable data and safe functions
that UIs can use to interact with the playback state.

`PlaybackService`'s job is to use the playback state to manage the ExoPlayer instance, the notification, the media session, the widget, and
also modify the state depending on system events, such as when a button is pressed on a headset. It should **never** be bound to, mostly because 
there is no need given that `PlaybackViewModel` exposes the same data in a much safer fashion.

#### Data Integers
Integer representations of data/UI elements are used heavily in Auxio, primarily for efficiency. To prevent any strange bugs, all integer 
representations must be unique. To see a table of all current integers, see the `C` class within the project.

Some datatypes [like `Tab` and `Sort`] have even more fine-grained integer representations for other data. More information can be found in
the documentation for those datatypes.

## Package-by-package rundown

#### `org.oxycblt.auxio`
This is the root package and contains the application instance and the landing UIs. This should be kept sparse with most other code being placed
into a package.


#### `.detail`
Contains all the detail UIs for some data types in Auxio. All detail user interfaces share the same base layout (A Single RecyclerView) and
only change the adapter/data being used. The adapters display both the header with information and the child items of the item itself, usually
with a data list similar to this:

`Item being displayed | Header Item | Child Item | Child Item | Child Item...`

Each adapter instance also handles the highlighting of the currently playing item in the detail menu.

`DetailViewModel` acts as the holder for the currently displaying items, along with having the `navToItem` LiveData that coordinates menu/playback
navigation [Such as when a user presses "Go to artist"]

#### `.home`
This package contains the components for the "home" UI in Auxio, or the UI that the user first sees when they open the app.

- The base package contains the top-level components that manage the FloatingActionButton, AppBar, and ViewPager instances.
- The `fastscroll` package contains the fast scroll component used in each list of music
- The `list` package contains the individual fragments for each list of music. These are all placed in the top-level ViewPager instance.
- The `tabs` package contains the data representation of an individual library tab and the UIs for editing them.

#### `.image`
[Coil](https://github.com/coil-kt/coil) is the image loader used by Auxio. This package contains the components Auxio leverages to load images
in a stable manner. Usually, you do not need to import this package elsewhere, but there are some important components:

- `BitmapProvider`, which allows external components (Such as in PlaybackService) to load a `Bitmap` in a way not prone to race conditions.
This should not be used for UIs.
- `BaseFetcher`, which is effectively Auxio's image loading routine. Most changes to image loading should be done there, and not it's 
sub-classes like `AlbumArtFetcher`.

#### `.music`
This package contains all `Music` implementations, the music loading implementation, and the excluded directory system. 

Key classes in this package include:
- `MusicStore`, which is the primary access point for music data.
- `Indexer`, which implements all of the `MediaStore` hacks to create a good metadata indexer for Auxio.

#### `.playback`
This module not only contains the playback system described above, but also multiple other components:

- `queue` contains the Queue UI and it's fancy item UIs.
- `state` contains the core playback state and persistence system.
- `replaygain` contains the ReplayGain implementation and the UIs related to it. Auxio's ReplayGain implementation is
somewhat different compared to other apps, as it leverages ExoPlayer's metadata and audio processing systems to not only
parse ReplayGain
- `system` contains the system-facing playback system, i.e `PlaybackService`

The base package contains the user-facing UIs representing the playback state, specifically the playback bar and the
playback panel that it expands into. Note that while the playback UI does rely on `BottomSheetLayout`, the layout is
designed to be at least somewhat re-usable, so it is in the generic `.ui` class.

#### `.search`
Package for Auxio's search functionality, `SearchViewHolder` handles the data results and filtering while `SearchFragment`/`SearchAdapter` handles the
display of the results and user input.

#### `.settings`
The settings system is primarily based off of `SettingsManager`, a wrapper around `SharedPreferences`. This allows settings to be read/written in a
much simpler/safer manner and without a context being needed. The Settings UI is largely contained in `SettingsListFragment`, which is a standard
`PreferenceFragment` implementation wrapped by the more general `SettingsFragment`. 

Internally, the settings package also leverages a couple custom preference implementations, notably `IntListPreference`, which enables
a normal choice preference to be backed by the integer representations that Auxio uses.

#### `.ui`
Shared views and view configuration models. This contains:

- Customized views such as `EdgeAppBarLayout`, `StyledImageView`, and others, which provide extra styling and behavior
not provided by default.
- Configuration models like `DisplayMode` and `Sort`, which are used in many places but aren't tied to a specific feature.
- `newMenu` and `ActionMenu`, which automates menu creation for most data types.
- The `RecyclerView` adapter framework described previously.
- The view-binding super classes described previously.
- `BottomSheetLayout`, a highly important layout that underpins Auxio's UI flow.
- Standard `ViewHolder` implementations that can be used for common datatypes.

#### `.util`
Shared utilities. This is primarily for QoL when developing Auxio. Documentation is provided on each method.

Utilities are separated into a few groups:
- Context utilties are extensions of `Context` and generally act as shortcuts for that class.
- Framework utilities extend a variety of view implementations to add new behavior or shortcuts.
- Primitive utilities operate on basic datatypes and are mostly shortcuts.
- Log utilities are a more light-weight logging framework that Auxio leverages instead of
bloated and over-engineered libraries like Timber.

#### `.widgets`
This package contains Auxio's AppWidget implementation, which deviates from other AppWidget implementations by packing multiple
different layouts into a single widget and then switching between them depending on the widget size. Note that since `RemoteViews`
and the AppWidget API in general is incredibly outdated and limited, this package deviates from much of Auxio's normal UI
conventions.

The playback service owns `WidgetComponent`, which listens to `PlaybackStateManager` for updates. During an update, it reloads
all song metadata and playback state into a `WidgetState`, which is an immutable version of the playback state that negates some
of the problems with using a volatile shared object.

`WidgetProvider` is the widget "implementation" exposed in the manifest. When `WidgetComponent` updates it, the class will create
a series of layouts [e.g "Forms"] for a variety of "size buckets" that would adequately contain the widget. This is then used as
the widget views, either with the native responsive behavior on Android 12 and above, or with the responsive behavior backported
to older devices.
