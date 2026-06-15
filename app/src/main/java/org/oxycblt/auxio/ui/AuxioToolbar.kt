/*
 * Copyright (c) 2026 Auxio Project
 * AuxioToolbar.kt is part of Auxio.
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
 
package org.oxycblt.auxio.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.MenuRes
import androidx.appcompat.R as AR
import androidx.appcompat.view.SupportMenuInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.view.size
import com.google.android.material.R as MR
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ViewToolbarBinding
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * Minimal MaterialToolbar replacement that implements everything with MaterialButtons compared to
 * the main androidx/MDC toolbar which is more or less unusable.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AuxioToolbar
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = AR.attr.toolbarStyle,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var inflatingLayout: Boolean
    private val binding: ViewToolbarBinding
    private var menuItemClickListener: Toolbar.OnMenuItemClickListener? = null
    private var overflowClickListener: OnClickListener? = null
    private val actionButtons = mutableMapOf<Int, RippleFixMaterialButton>()
    @SuppressLint("RestrictedApi") private var menuBuilder = MenuBuilder(context)

    init {
        inflatingLayout = true
        binding = ViewToolbarBinding.inflate(LayoutInflater.from(context), this, true)
        inflatingLayout = false

        clipChildren = false
        clipToPadding = false

        binding.toolbarRoot.apply {
            clipChildren = false
            clipToPadding = false
        }
        binding.toolbarContentFrame.apply {
            clipChildren = false
            clipToPadding = false
        }
        binding.toolbarActionGroup.apply {
            clipChildren = false
            clipToPadding = false
            spacing = 0
        }

        configureIconButton(binding.toolbarNavigationButton)

        val toolbarAttrs =
            context.obtainStyledAttributes(attrs, AR.styleable.Toolbar, defStyleAttr, 0)
        val materialToolbarAttrs =
            context.obtainStyledAttributes(attrs, MR.styleable.MaterialToolbar, defStyleAttr, 0)

        val titleCentered =
            materialToolbarAttrs.getBoolean(MR.styleable.MaterialToolbar_titleCentered, false)
        binding.toolbarTitle.apply {
            text = toolbarAttrs.getText(AR.styleable.Toolbar_title)
            isVisible = !text.isNullOrEmpty()
            gravity =
                if (titleCentered) android.view.Gravity.CENTER_HORIZONTAL
                else android.view.Gravity.START
            textAlignment =
                if (titleCentered) {
                    TEXT_ALIGNMENT_CENTER
                } else {
                    TEXT_ALIGNMENT_VIEW_START
                }
            setTextAppearance(
                toolbarAttrs.getResourceId(
                    AR.styleable.Toolbar_titleTextAppearance,
                    MR.attr.textAppearanceDisplaySmallEmphasized,
                )
            )
        }

        val subtitleCentered =
            materialToolbarAttrs.getBoolean(MR.styleable.MaterialToolbar_subtitleCentered, false)
        binding.toolbarSubtitle.apply {
            text = toolbarAttrs.getText(AR.styleable.Toolbar_subtitle)
            isVisible = !text.isNullOrEmpty()
            gravity =
                if (subtitleCentered) android.view.Gravity.CENTER_HORIZONTAL
                else android.view.Gravity.START
            textAlignment =
                if (subtitleCentered) {
                    TEXT_ALIGNMENT_CENTER
                } else {
                    TEXT_ALIGNMENT_VIEW_START
                }
            setTextAppearance(
                toolbarAttrs.getResourceId(
                    AR.styleable.Toolbar_subtitleTextAppearance,
                    MR.attr.textAppearanceTitleLarge,
                )
            )
        }
        binding.toolbarNavigationButton.apply {
            icon = toolbarAttrs.getDrawable(AR.styleable.Toolbar_navigationIcon)
            contentDescription =
                toolbarAttrs.getText(AR.styleable.Toolbar_navigationContentDescription)
            isVisible = icon != null
            TooltipCompat.setTooltipText(this, contentDescription)
        }

        val menuResId = toolbarAttrs.getResourceId(AR.styleable.Toolbar_menu, 0)

        toolbarAttrs.recycle()
        materialToolbarAttrs.recycle()

        if (menuResId != 0) {
            inflateMenu(menuResId)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (inflatingLayout) {
            super.addView(child, index, params)
        } else {
            // hardcode layoutparams because injectable children logic is insane and never
            binding.toolbarTitleContainer.addView(
                child,
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
            )
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    var title: CharSequence?
        get() = binding.toolbarTitle.text
        set(value) {
            binding.toolbarTitle.apply {
                text = value
                isVisible = !text.isNullOrEmpty()
            }
        }

    var subtitle: CharSequence?
        get() = binding.toolbarSubtitle.text
        set(value) {
            binding.toolbarSubtitle.apply {
                text = value
                isVisible = !text.isNullOrEmpty()
            }
        }

    val menu: Menu
        get() = menuBuilder

    @SuppressLint("RestrictedApi")
    fun inflateMenu(@MenuRes resId: Int) {
        val builder = MenuBuilder(context)
        SupportMenuInflater(context).inflate(resId, builder)
        menuBuilder = builder
        rebuildActionButtons()
    }

    fun setOnMenuItemClickListener(listener: Toolbar.OnMenuItemClickListener?) {
        menuItemClickListener = listener
    }

    fun getMenuButton(itemId: Int): RippleFixMaterialButton? = actionButtons[itemId]

    @SuppressLint("RestrictedApi")
    fun setMenuItemEnabled(itemId: Int, enabled: Boolean) {
        menuBuilder.findItem(itemId)?.isEnabled = enabled
        getMenuButton(itemId)?.isEnabled = enabled
    }

    val titleContainer: View
        get() = binding.toolbarTitleContainer

    fun setNavigationOnClickListener(listener: OnClickListener) {
        binding.toolbarNavigationButton.setOnClickListener(listener)
    }

    /**
     * Override the overflow button's click behavior. When set, the overflow button will call
     * [block] instead of showing the default popup menu.
     */
    fun setOnOverflowMenuClick(block: OnClickListener) {
        overflowClickListener = block
    }

    private fun configureIconButton(button: RippleFixMaterialButton) {
        button.minimumWidth = resources.getDimensionPixelSize(R.dimen.size_touchable_small)
        button.minimumHeight = resources.getDimensionPixelSize(R.dimen.size_touchable_small)
        button.iconSize = resources.getDimensionPixelSize(R.dimen.size_icon_small)
    }

    @SuppressLint("RestrictedApi")
    private fun rebuildActionButtons() {
        actionButtons.clear()
        binding.toolbarActionGroup.removeAllViews()

        val actionItems = mutableListOf<MenuItemImpl>()
        val overflowItems = mutableListOf<MenuItemImpl>()
        for (i in 0 until menuBuilder.size()) {
            val item = menuBuilder.getItem(i) as MenuItemImpl
            if (!item.isVisible) continue
            if (item.requiresActionButton() || item.requestsActionButton()) {
                actionItems.add(item)
            } else {
                overflowItems.add(item)
            }
        }

        for (item in actionItems) {
            val btn = createActionButton(item)
            binding.toolbarActionGroup.addView(btn)
            actionButtons[item.itemId] = btn
        }

        if (overflowItems.isNotEmpty()) {
            binding.toolbarActionGroup.addView(createOverflowButton(overflowItems))
        }

        binding.toolbarActionGroup.isVisible = binding.toolbarActionGroup.isNotEmpty()
    }

    @SuppressLint("RestrictedApi")
    private fun createActionButton(item: MenuItemImpl) =
        RippleFixMaterialButton(
                createActionButtonContext(item.itemId),
                null,
                MR.attr.materialIconButtonStyle,
            )
            .apply {
                configureIconButton(this)
                id = item.itemId
                icon = item.icon
                contentDescription = item.title
                isEnabled = item.isEnabled
                TooltipCompat.setTooltipText(this, item.title)
                setOnClickListener { view ->
                    if (item.hasSubMenu()) {
                        val subMenu = unlikelyToBeNull(item.subMenu)
                        showPopupMenu(view, subMenu.children.toList())
                    } else {
                        menuItemClickListener?.onMenuItemClick(item)
                    }
                }
            }

    @SuppressLint("RestrictedApi")
    private fun createOverflowButton(overflowItems: List<MenuItemImpl>) =
        RippleFixMaterialButton(context, null, MR.attr.materialIconButtonStyle).apply {
            configureIconButton(this)
            setIconResource(R.drawable.ic_more_vert_24)
            contentDescription = context.getString(R.string.lbl_more)
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener { view ->
                val customListener = overflowClickListener
                if (customListener != null) {
                    customListener.onClick(view)
                } else {
                    showPopupMenu(view, overflowItems)
                }
            }
        }

    @SuppressLint("RestrictedApi")
    private fun showPopupMenu(anchor: View, items: List<MenuItem>) {
        val popup = PopupMenu(context, anchor)
        val originalItems = mutableMapOf<Int, MenuItem>()
        for (item in items) {
            popup.menu.add(item.groupId, item.itemId, item.order, item.title).apply {
                icon = item.icon
                isEnabled = item.isEnabled
                isCheckable = item.isCheckable
                isChecked = item.isChecked
            }
            originalItems[item.itemId] = item
        }
        popup.setOnMenuItemClickListener { clickedItem ->
            val originalItem =
                originalItems[clickedItem.itemId] ?: return@setOnMenuItemClickListener false
            menuItemClickListener?.onMenuItemClick(originalItem) ?: false
        }
        popup.show()
    }

    private fun createActionButtonContext(itemId: Int): Context {
        val themeOverlayRes =
            when (itemId) {
                R.id.action_play -> R.style.ThemeOverlay_Auxio_IconButton_Style_Small_Secondary
                R.id.action_shuffle -> R.style.ThemeOverlay_Auxio_IconButton_Style_Small_Primary
                else -> 0
            }

        return if (themeOverlayRes != 0) ContextThemeWrapper(context, themeOverlayRes) else context
    }
}
