package org.oxycblt.auxio.ui

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel

/**
 * Apply a text color to a [MenuItem]
 * @param color The text color that should be applied.
 */
fun MenuItem.applyColor(@ColorInt color: Int) {
    SpannableString(title).apply {
        setSpan(ForegroundColorSpan(color), 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        title = this
    }
}

/**
 * Disable an image button.
 */
fun ImageButton.disable() {
    if (isEnabled) {
        imageTintList = ColorStateList.valueOf(
            R.color.inactive_color.toColor(context)
        )

        isEnabled = false
    }
}

/**
 * Determine if the device is currently in landscape.
 * @param resources [Resources] required
 */
fun isLandscape(resources: Resources): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Get the span count for most RecyclerViews when in landscape mode.
 * @return 3 if landscape mode is tablet, 2 if landscape mode is phone
 */
fun getLandscapeSpans(resources: Resources): Int {
    return if (resources.configuration.screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) 3 else 2
}

/**
 * Create a [Toast] from a [String]
 * @param context [Context] required to create the toast
 */
fun String.createToast(context: Context) {
    Toast.makeText(context.applicationContext, this, Toast.LENGTH_SHORT).show()
}

/**
 * "Render" a [Spanned] using [HtmlCompat].
 * @return A [Spanned] that actually works.
 */
fun Spanned.render(): Spanned {
    return HtmlCompat.fromHtml(
        this.toString(), HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
    )
}

/**
 * Set a [TextView] text color, without having to resolve the resource.
 */
fun TextView.setTextColorResource(@ColorRes color: Int) {
    setTextColor(color.toColor(context))
}

/**
 * Apply accents to a [MaterialButton]
 * @param highlighted Whether the MaterialButton has an "Unimportant" style or not.
 * Required because you cant determine a style of a view before 29
 */
fun MaterialButton.applyAccents(highlighted: Boolean) {
    val accent = accent.first.toColor(context)

    if (highlighted) {
        backgroundTintList = ColorStateList.valueOf(accent)
    } else {
        setTextColor(accent)
    }
}

/**
 * Require an [AppCompatActivity]
 */
fun Fragment.requireCompatActivity(): AppCompatActivity {
    val activity = requireActivity()

    if (activity is AppCompatActivity) {
        return activity
    } else {
        error("Required activity to be AppCompatActivity, however it wasn't.")
    }
}
