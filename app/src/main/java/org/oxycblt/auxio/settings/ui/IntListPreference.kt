/*
 * Copyright (c) 2021 Auxio Project
 * IntListPreference.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.settings.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getTextArrayOrThrow
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import java.lang.reflect.Field
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getInteger
import org.oxycblt.auxio.util.lazyReflectedField

/**
 * An implementation of a list-based preference backed with integers.
 *
 * The dialog this preference corresponds to is not handled automatically, so a preference screen
 * must override onDisplayPreferenceDialog in order to handle it.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class IntListPreference
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.preference.R.attr.dialogPreferenceStyle,
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    /** The names of each entry. */
    val entries: Array<CharSequence>
    /** The corresponding integer values for each entry. */
    val values: IntArray

    private var entryIcons: TypedArray? = null
    private var offValue: Int? = -1
    private var currentValue: Int? = null

    init {
        val prefAttrs =
            context.obtainStyledAttributes(
                attrs, R.styleable.IntListPreference, defStyleAttr, defStyleRes)

        // Can't depend on ListPreference due to it working with strings we have to instead
        // define our own attributes for entries/values.
        entries = prefAttrs.getTextArrayOrThrow(R.styleable.IntListPreference_entries)
        values =
            context.resources.getIntArray(
                prefAttrs.getResourceIdOrThrow(R.styleable.IntListPreference_entryValues))

        // entryIcons defines an additional set of icons to use for each entry.
        val iconsId = prefAttrs.getResourceId(R.styleable.IntListPreference_entryIcons, -1)
        if (iconsId > -1) {
            entryIcons = context.resources.obtainTypedArray(iconsId)
        }

        // offValue defines an value in which the preference should be disabled.
        val offValueId = prefAttrs.getResourceId(R.styleable.IntListPreference_offValue, -1)
        if (offValueId > -1) {
            offValue = context.getInteger(offValueId)
        }

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
            // Reflect into Preference to get the (normally inaccessible) default value.
            currentValue = getPersistedInt(PREFERENCE_DEFAULT_VALUE_FIELD.get(this) as Int)
        }
    }

    override fun shouldDisableDependents() = currentValue == offValue

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val index = getValueIndex()
        if (index > -1) {
            // If we have a specific icon to use, make sure it is set in the view.
            val resourceId = entryIcons?.getResourceId(index, -1) ?: -1
            if (resourceId > -1) {
                (holder.findViewById(android.R.id.icon) as ImageView).setImageResource(resourceId)
            }
        }
    }

    /**
     * Get the index of the current value.
     *
     * @return The index of the current value within [values], or -1 if the [IntListPreference] is
     *   not set.
     */
    fun getValueIndex(): Int {
        val curValue = currentValue
        if (curValue != null) {
            return values.indexOf(curValue)
        }
        return -1
    }

    /**
     * Set the current value of this preference using it's index.
     *
     * @param index The index of the new value within [values]. Must be valid.
     */
    fun setValueIndex(index: Int) {
        setValue(values[index])
    }

    private fun setValue(value: Int) {
        if (value != currentValue) {
            if (!callChangeListener(value)) {
                // Listener rejected the value
                return
            }

            // Update internal value.
            currentValue = value
            notifyDependencyChange(shouldDisableDependents())
            persistInt(value)
            notifyChanged()
        }
    }

    /** Copy of ListPreference's [Preference.SummaryProvider] for this [IntListPreference]. */
    private inner class IntListSummaryProvider : SummaryProvider<IntListPreference> {
        override fun provideSummary(preference: IntListPreference): CharSequence {
            val index = getValueIndex()
            if (index != -1) {
                // Get the entry corresponding to the currently shown value.
                return entries[index]
            }

            // Usually an invalid state, don't bother translating
            return "<not set>"
        }
    }

    private companion object {
        val PREFERENCE_DEFAULT_VALUE_FIELD: Field by
            lazyReflectedField(Preference::class, "mDefaultValue")
    }
}
