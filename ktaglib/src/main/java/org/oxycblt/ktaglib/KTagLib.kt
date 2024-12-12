package com.example.ktaglib

object KTagLib {
    // Used to load the 'ktaglib' library on application startup.
    init {
        System.loadLibrary("ktaglib")
    }

    /**
     * A native method that is implemented by the 'ktaglib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
}