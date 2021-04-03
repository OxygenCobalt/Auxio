package org.oxycblt.auxio.settings.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import org.oxycblt.auxio.R
import androidx.preference.R as prefR

class IntListPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = prefR.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    val entries: Array<CharSequence>
    val values: IntArray
    var currentValue: Int? = null
        private set

    private val default: Int

    init {
        val prefAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.IntListPreference, defStyleAttr, defStyleRes
        )

        entries = prefAttrs.getTextArray(R.styleable.IntListPreference_entries)

        values = context.resources.getIntArray(
            prefAttrs.getResourceId(R.styleable.IntListPreference_entryValues, -1)
        )

        default = prefAttrs.getInt(prefR.styleable.Preference_defaultValue, Int.MIN_VALUE)

        prefAttrs.recycle()

        summaryProvider = IntListSummaryProvider()
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int) = a.getInt(index, -1)

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)

        if (defaultValue != null) {
            // If were given a default value, we need to assign it.
            setValue(defaultValue as Int)
        } else {
            currentValue = getPersistedInt(default)
        }
    }

    fun getValueIndex(): Int {
        val curValue = currentValue

        if (curValue != null) {
            return values.indexOf(curValue)
        }

        return -1
    }

    fun setValue(value: Int) {
        if (value != currentValue) {
            currentValue = value

            callChangeListener(value)
            persistInt(value)
            notifyChanged()
        }
    }

    private inner class IntListSummaryProvider : SummaryProvider<IntListPreference> {
        override fun provideSummary(preference: IntListPreference?): CharSequence {
            val index = getValueIndex()

            if (index != -1) {
                return entries[index]
            }

            return context.getString(prefR.string.not_set)
        }
    }
}
