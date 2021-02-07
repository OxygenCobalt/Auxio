# Architecture

This document is designed to provide a simple overview of Auxio's architecture and some guides on how to add to the codebase in an elegant manner. It will be updated as aspects about Auxio change.

#### Package structure overview

```
org.oxycblt.auxio  # Main UI's and logging utilities 
├──.coil           # Fetchers and utilities for Coil, contains binding adapters than be used in the user interface.
├──.database       # Databases and their items for Auxio
├──.detail         # UIs for more album/artist/genre details
│  └──.adapters    # RecyclerView adapters for the detail UIs, which display the header information and items
├──.library        # Library UI
├──.loading        # Loading UI
├──.music          # Music storage and loading
│  └──.processing  # Systems for music loading and organization
├──.playback       # Playback UI and systems
│  ├──.queue       # Queue user interface
│  └──.state       # Backend/Modes for the playback state
├──.recycler       # Shared RecyclerView utilities and modes
│  └──.viewholders # Shared ViewHolders and ViewHolder utilities
├──.search         # Search UI
├──.settings       # Settings UI and systems
│  └──.ui          # Contains UI's related to the settings view, such as the about screen
├──.songs          # Songs UI
├──.ui             # Shared user interface utilities
```

#### Primary code structure

Auxio's codebase is mostly centered around 4 different types of code.

- UIs: Fragments, RecyclerView items, and Activities are part of this class. All of them should have little data logic in them and should primarily focus on displaying information in their UIs.
- ViewModels: These usually contain data and values that a UI can display, along with doing data processing. The data often takes the form of `MutableLiveData` or `LiveData`, which can be observed.
- Shared Objects: These are the fundamental building blocks of Auxio, and exist at the process level. These are usually retrieved using `getInstance` or a similar function. Shared Objects should be avoided in UIs, as their volatility can cause problems. Its better to use a ViewModel and their exposed data instead.
- Utilities: These are largely found in the `.ui`, `.music`, and `.coil` packages, taking the form of standalone or extension functions that can be used anywhere.

Ideally, UIs should only be talking to ViewModels, ViewModels should only be talking to the Shared Objects, and Shared Objects should only be talking to other shared objects. All objects can use the utility functions.

#### UI Structure

Auxio only has one activity, that being `MainActivity`. When adding a new UI, it should be added as a `Fragment` or a `RecyclerView` item depending on the situation. 

Databinding should *always* be used instead of `findViewById`.  `by memberBinding` is used if the binding needs to be a member variable in order to avoid memory leaks.

Usually, fragment creation is done in `onCreateView`, and organized into three parts:

- Create variables [Bindings, Adapters, etc]
- Set up the UI
- Set up LiveData observers

When creating a ViewHolder for a `RecyclerView`, one should use `BaseHolder` to standardize the binding process and automate some code shared across all ViewHolders.

#### Binding Adapters

Data is often bound using Binding Adapters, which are XML attributes assigned in layout files that can automatically display data, usually written as `app:bindingAdapterName="@{data}"`. Its recommended to use these instead of duplicating code manually. These can be found in `.coil` and `.music`.

#### Playback system

Auxio's playback system is somewhat unorthodox, as it avoids a lot of the built-in android code in favor of a more understandable and controllable system. Its structured around a couple of objects, the connections being highlighted in this diagram.

```
    Playback UI    Queue UIs    PlaybackService
         │             │               │
         │             │               │
 PlaybackViewModel─────┘               │
         │                             │
         │                             │
PlaybackStateManager───────────────────┘
```

`PlaybackStateManager` is the shared object that contains the master copy of the playback state, doing all operations on it. If you want to add something to the playback system, this is likely where you should add it.

This object should ***NEVER*** be used in a UI, as it does not sanitize input and can cause major problems if a Volatile UI interacts with it. It's callback system is also prone to memory leaks if not cleared when done.  `PlaybackViewModel` can be used instead, as it exposes stable data and abstracted functions that UI's can use to interact with the playback state.

`PlaybackService`'s job is to use the playback state to manage the ExoPlayer instance and also modify the state depending on system external events, such as when a button is pressed on a headset.  It should **never** be bound to, mostly because there is no need given that `PlaybackViewModel` exposes the same data in a much safer fashion.

#### Using Music Data

All music objects inherit `BaseModel`, which guarantees that all music has both an ID and a name.

- Songs are the most basic element, with them having a reference to their album and genre. 
- Albums contain a list of their songs and their parent artist.
- Artists contain a list of songs, a list of albums, and their most prominent genre.
- Genres contain a list of songs, its preferred to use `displayName` with genres as that will convert the any numbered names into non-numbered names.

`BaseModel` can be used as an argument type to specify that any music type, while `Parent` can be used as an argument type to only specify music objects that have child items, such as albums or artists.

#### Using Settings

Access to settings should preferably be done with `SettingsManager` as it can be accessed everywhere without a context.

#### Using Coil

[Coil](https://github.com/coil-kt/coil) is the image loader used by Auxio. All image loading is done through these four functions/binding adapters:

- `app:coverArt`: Binding Adapter that will load the cover art for a song or album
- `app:artistImage`: Binding Adapter that will load the artist image
- `app:genreImage`: Binding Adapter that will load the genre image
- `getBitmap`: Function that will take a song and return a bitmap, this should not be used in anything UI related, that is what the binding adapters above are for.

This should be enough to cover most use cases in Auxio.

#### Logging

Its recommended to use `logD` and `logE` for logging debug messages and errors. Both will automatically use the names of the objects that call it, and logging messages done with `logD` wont show in release builds.