package com.meticulous.mimomobilechallenge.tools

import android.util.Log
import com.meticulous.mimomobilechallenge.models.Content
import com.meticulous.mimomobilechallenge.models.Lesson

class LessonEngine {
    private val TAG = "LessonEngine"

    fun processLesson(
        lesson: Lesson,
        callback: (viewTypes: List<ViewType>, instructionText: String, expectedAnswer: String) -> Unit
    ) {
        Log.d(TAG, "processLesson called")

        val outputViews = arrayListOf<ViewType>()
        // Extract all the text in the lesson
        val textBuilder = StringBuilder()
        lesson.contents.forEach { content ->
            textBuilder.append(content.text)
        }
        val hasInput = lesson.hasInput()
        val instructionText = textBuilder.toString()

        var expectedAnswer = ""

        // Get the start and end indexes. Only needed if we have INPUT in the lesson
        val startIndex = lesson.input?.startIndex ?: 0
        val endIndex = lesson.input?.endIndex ?: 0

        Log.v(
            TAG,
            "processLesson hasInput: $hasInput instructionText: $instructionText startIndex: $startIndex endIndex: $endIndex"
        )

        if (hasInput) {
            expectedAnswer = instructionText.substring(startIndex, endIndex)
            Log.i(TAG, "processLesson expectedAnswer: $expectedAnswer")

            var appendedLength = 0
            lesson.contents.forEach { content ->
                if (appendedLength + content.text.length <= startIndex) {
                    outputViews.add(ViewType.TextDisplay(content))
                    appendedLength += content.text.length
                } else if (appendedLength < endIndex && appendedLength + content.text.length >= endIndex) {
                    outputViews.add(ViewType.TextInput(content))
                    outputViews.add(
                        ViewType.TextDisplay(
                            Content(
                                instructionText.substring(
                                    endIndex,
                                    appendedLength + content.text.length
                                ), content.color
                            )
                        )
                    )
                    appendedLength += content.text.length
                } else {
                    outputViews.add(ViewType.TextDisplay(content))
                    appendedLength += content.text.length
                }
            }
        } else {
            lesson.contents.forEach { content ->
                outputViews.add(ViewType.TextDisplay(content))
            }
        }

        callback(outputViews, instructionText, expectedAnswer)
    }

    sealed class ViewType {
        class TextDisplay(val content: Content) : ViewType()
        class TextInput(val content: Content) : ViewType()
    }

}