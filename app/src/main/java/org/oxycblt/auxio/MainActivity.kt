package org.oxycblt.auxio

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.oxycblt.auxio.theme.accent

class MainActivity : AppCompatActivity() {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        // Apply the theme
        setTheme(accent.second)

        return super.onCreateView(name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.postponeEnterTransition(this)
        setContentView(R.layout.activity_main)
    }
}

// I wish I knew somewhere else to put this
class ClickListener<T>(val onClick: (T) -> Unit)
