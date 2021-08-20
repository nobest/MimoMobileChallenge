package com.meticulous.mimomobilechallenge.ui.main.customviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.meticulous.mimomobilechallenge.models.Content

class TextInputView : AppCompatEditText {
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
            setTextColor(Color.parseColor(it.color))
        }
        textSize = 26F
        width = (100 * context.resources.displayMetrics.density).toInt() // width to 100dp
    }

    fun setContent(content: Content) {
        mContent = content
        setTextColor(Color.parseColor(content.color))
    }

}