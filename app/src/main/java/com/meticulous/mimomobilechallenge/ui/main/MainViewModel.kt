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
import com.meticulous.mimomobilechallenge.tools.LessonEngine
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
    private val lessonEngine: LessonEngine by lazy {
        LessonEngine()
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
        repository.saveLesson(
            LessonComplete(
                currentLesson.id,
                lessonStartTime,
                lessonCompletedTime
            )
        )
        uiStateAction.value = UiState.Answered
        processNextLesson()
    }

    override fun onLessonFetched() {
        Log.d(TAG, "onLessonFetched called")
        processNextLesson()
    }

    private fun processNextLesson() {
        Log.d(TAG, "processNextLesson called")
        val lesson = repository.getNextLesson()
        if (lesson == null) {
            // This means we've come to the end of the lesson
            uiStateAction.value = UiState.Done
            Log.w(TAG, "processNextLesson got null. Returning Done")
            return
        }
        loaderVisibility.set(false)
        lessonStartTime = System.currentTimeMillis()
        currentLesson = lesson
        hasInput = lesson.hasInput()
        runButtonEnabled.set(!hasInput)

        lessonEngine.processLesson(lesson) { viewTypes, instructionText, answer ->
            Log.d(
                TAG,
                "processNextLesson.lessonEngine.processLesson instructionText: $instructionText, answer: $answer and\nviewTypes: $viewTypes"
            )
            instructionCode.set(instructionText)
            expectedAnswer = answer
            viewTypes.forEach {
                uiStateAction.value = when (it) {
                    is LessonEngine.ViewType.TextDisplay -> UiState.BindTextDisplay(it.content)
                    is LessonEngine.ViewType.TextInput -> UiState.BindTextInput(it.content)
                }
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