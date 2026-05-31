/*
 * Topway/DoFun stock-component compatibility shim.
 *
 * DoFun/Topway launchers are known to recognise com.tw.music / com.tw.music.MusicActivity,
 * and stock twmusic also exposes com.tw.music.MusicService. Android has no service-alias
 * mechanism, so the Topway flavour provides this narrow class-name shim and inherits the
 * real Auxio media/browser/playback service implementation.
 */

package com.tw.music

class MusicService : org.oxycblt.auxio.AuxioService()
