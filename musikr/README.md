# musikr

Musikr is a highly opinionated multithreaded music loader that enables Auxio's advanced music functionality.
It completely bypasses Android's MediaStore and uses the [storage access framework (SAF)](https://developer.android.com/guide/topics/providers/document-provider)
and [taglib](https://taglib.org/) to replicate it's functionality with less bugs and more flexibility, further
expanding it with an advanced music model that leverages the wide variety of tags available in modern extended
specs.

Warning that the API surface is:
- Extremely unstable, as it's a very thin shim on top of a constantly optimzied and updated music loader
- Minimized to only what the rest of the app uses or builds on, so you will need to patch it to extend 
certain components

Feel free to use this library as long as you follow Auxio's GPLv3 license and open-source all modifications.

If you want to generate some docs for the unstable API, you can run

```bash
./gradlew musikr:dokkaGeneratePublicationHtml
```

In the project root and it should produce a webpage in `musikr/build/dokka/html`
