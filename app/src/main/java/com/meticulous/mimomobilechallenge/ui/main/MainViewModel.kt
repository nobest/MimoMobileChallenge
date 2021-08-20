package com.meticulous.mimomobilechallenge.ui.main

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.meticulous.mimomobilechallenge.models.Content
import com.meticulous.mimomobilechallenge.models.Lesson
import com.meticulous.mimomobilechallenge.tools.LessonComplete
import com.meticulous.mimomobilechallenge.tools.LessonFetchedListener
import com.meticulous.mimomobilechallenge.tools.LessonRepository
import com.meticulous.mimomobilechallenge.tools.MimoChallengeDb

class MainViewModel(app: Application) : AndroidViewModel(app), LessonFetchedListener {
    private val TAG = "MIMO_VM"

    val loaderVisibility: ObservableField<Boolean> = ObservableField(true)
    val runButtonEnabled: ObservableField<Boolean> = ObservableField(false)
    val instructionCode: ObservableField<String> = ObservableField("")
    private val repository: LessonRepository by lazy {
        LessonRepository(MimoChallengeDb.getDatabase(app))
    }

    private var lessonStartTime: Long = System.currentTimeMillis()
    private var lessonCompletedTime: Long = System.currentTimeMillis()

    private var expectedAnswer: String = ""
    private var hasInput = false

    private lateinit var currentLesson: Lesson
    val uiStateAction: MutableLiveData<UiState> = MutableLiveData()

    init {
        repository.registerForLessonFetched(this)
    }

    fun onRunButtonClicked() {
        Log.d(TAG, "onRunButtonClicked called")
        lessonCompletedTime = System.currentTimeMillis()
        repository.saveLesson(LessonComplete(currentLesson.id, lessonStartTime, lessonCompletedTime))
        uiStateAction.value = UiState.Answered
        processNextLesson()
    }

    override fun onLessonFetched() {
        Log.d(TAG, "onLessonFetched called")
        processNextLesson()
    }

    private fun processNextLesson() {
        Log.d(TAG, "getNextLesson called")
        val lesson = repository.getNextLesson()
        if (lesson == null) {
            // This means we've come to the end of the lesson
            uiStateAction.value = UiState.Done
            Log.w(TAG, "getNextLesson got null. Returning")
            return
        }
        lessonStartTime = System.currentTimeMillis()
        currentLesson = lesson
        hasInput = lesson.hasInput()

        // Extract all the text in the lesson
        val textBuilder = StringBuilder()
        lesson.contents.forEach { content ->
            textBuilder.append(content.text)
        }
        val text = textBuilder.toString()

        // Display the text in the instruction view
        instructionCode.set(text)

        // Get the start and end indexes. Only needed if we have INPUT in the lesson
        val startIndex = lesson.input?.startIndex ?: 0
        val endIndex = lesson.input?.endIndex ?: 0

        // We have a lesson at hand, hide loader
        loaderVisibility.set(false)
        Log.v(
            TAG,
            "getNextLesson hasInput: $hasInput text: $text startIndex: $startIndex endIndex: $endIndex"
        )

        if (hasInput) {
            //Disable the run button if we have input in the lesson until the user enter the correct answer
            runButtonEnabled.set(false)
            // Extract the expected answer which is the correct answer
            expectedAnswer = text.substring(startIndex, endIndex)
            Log.i(TAG, "getNextLesson expectedAnswer: $expectedAnswer")

            var appendedLength = 0
            lesson.contents.forEach { content ->
                if (appendedLength + content.text.length <= startIndex) {
                    uiStateAction.value = UiState.BindTextDisplay(content)
                    appendedLength += content.text.length
                } else if (appendedLength < endIndex && appendedLength + content.text.length >= endIndex) {
                    uiStateAction.value = UiState.BindTextInput(content)
                    uiStateAction.value = UiState.BindTextDisplay(
                        Content(
                            text.substring(
                                endIndex,
                                appendedLength + content.text.length
                            ), content.color
                        )
                    )
                    appendedLength += content.text.length
                } else {
                    uiStateAction.value = UiState.BindTextDisplay(content)
                    appendedLength += content.text.length
                }
            }
        } else {
            // If we have no input in the lesson, just add all the views to the display
            runButtonEnabled.set(true)
            lesson.contents.forEach { content ->
                uiStateAction.value = UiState.BindTextDisplay(content)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.deregisterFromLessonFetched(this)
        repository.resetIndex()
    }

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            val answer = p0?.toString().orEmpty()
            Log.d(TAG, "afterTextChanged called. answer: $answer")
            // Update the run button as necessary
            // Trim is not ideal in every case as the expected answer might have trailing spaces
            // and you want the user to enter the exact answer but, it is at least acceptable for this demo case
            runButtonEnabled.set(!hasInput || (answer.isNotBlank() && answer.trim() == expectedAnswer.trim()))
        }
    }

    sealed class UiState {
        data class BindTextDisplay(val content: Content) : UiState()
        data class BindTextInput(val content: Content) : UiState()
        object Answered : UiState()
        object Done : UiState()
    }
}