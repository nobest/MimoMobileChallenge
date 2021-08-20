package com.meticulous.mimomobilechallenge.ui.main.customviews

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.meticulous.mimomobilechallenge.models.Content

class TextDisplayView : AppCompatTextView {
    private lateinit var mContent: Content

    constructor(context: Context, content: Content? = null) : super(context) {
        initialise(content)
    }

    constructor(context: Context, attrs: AttributeSet, content: Content? = null) : super(
        context,
        attrs
    ) {
        initialise(content)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyle: Int,
        content: Content? = null
    ) : super(
        context,
        attrs,
        defStyle
    ) {
        initialise(content)
    }

    private fun initialise(content: Content?) {
        content?.let {
            mContent = it
            text = it.text
            setTextColor(Color.parseColor(it.color))
        }
        textSize = 20F

    }

    fun setContent(content: Content) {
        mContent = content
        text = content.text
        setTextColor(Color.parseColor(content.color))
    }

}