package com.github.kr328.clash.design.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.databinding.ComponentLargeActionLabelBinding
import com.github.kr328.clash.design.databinding.ComponentMainProfileBinding

import com.github.kr328.clash.design.ui.ObservableCurrentTime
import com.github.kr328.clash.design.util.*
import com.github.kr328.clash.service.model.Profile
import com.google.android.material.card.MaterialCardView

class ProfileActionCard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : MaterialCardView(context, attributeSet, defStyleAttr) {
    private val binding = ComponentMainProfileBinding
        .inflate(context.layoutInflater, this, true)

    var profile: Profile?
        get() = binding.profile
        set(value) {
            binding.profile = value
        }

    var onClick: OnClickListener?
        get() = binding.clicked
        set(value) {
            binding.clicked = value
        }
    var onUpdate: OnClickListener?
        get() = binding.update
        set(value) {
            binding.update = value
        }

    var icon: Drawable?
        get() = binding.iconView.background
        set(value) {
            binding.iconView.background = value
        }

    init {
        context.resolveClickableAttrs(attributeSet, defStyleAttr) {
            isFocusable = focusable(true)
            isClickable = clickable(true)
            foreground = foreground() ?: context.selectableItemBackground
        }
        binding.currentTime=ObservableCurrentTime()

        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.LargeActionCard,
            defStyleAttr,
            0
        ).apply {
            try {
                icon = getDrawable(R.styleable.LargeActionCard_icon)
            } finally {
                recycle()
            }
        }

        minimumHeight = context.getPixels(R.dimen.large_action_card_min_height)
        radius = context.getPixels(R.dimen.large_action_card_radius).toFloat()
        elevation = context.getPixels(R.dimen.large_action_card_elevation).toFloat()
        setCardBackgroundColor(context.resolveThemedColor(R.attr.colorSurface))
    }
}