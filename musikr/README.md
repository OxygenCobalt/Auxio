# musikr

Musikr is a highly opinionated multithreaded music library that enables Auxio's advanced music functionality.
It completely bypasses Android's MediaStore and uses the [storage access framework (SAF)](), [taglib]() to
replicate it's functionality with less bugs and more flexibility, further expanding it with an advanced music
model that is both fast and leverages some of the nice features within the MusicBrainz spec.

There's not really a stable API surface for musikr right now, given that the music loader is still being
optimized and the rest of Auxio's modularization efforts are still in progress. Lots of useful stuff is
hidden, and lots of useless stuff is exposed. Eventually the API will start settling down into something
usable, but it will still require patches for your own project.

Feel free to use this library as long as you follow Auxio's GPLv3 license and open-source all modifications.

If you want to generate some docs for the unstable API, you can run

```bash
./gradlew musikr:dokkaGeneratePublicationHtml
```

In the project root and it should produce a webpage in `musikr/build/dokka/html`
