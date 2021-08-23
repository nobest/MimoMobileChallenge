package com.meticulous.mimomobilechallenge.tools

import com.meticulous.mimomobilechallenge.models.Content
import com.meticulous.mimomobilechallenge.models.Input
import com.meticulous.mimomobilechallenge.models.Lesson
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class LessonEngineTest {
    private var lessonEngine: LessonEngine? = null

    @Before
    fun setUp() {
        lessonEngine = LessonEngine()
    }

    @After
    fun tearDown() {
        lessonEngine = null
    }

    @Test
    fun testProcessLessonWithoutInput() {
        val noInputLesson = Lesson(
            id = 1,
            contents = arrayListOf(
                Content("var ", "#FF0000"),
                Content("x = ", "#FFFFFF"),
                Content("1", "#0000FF")
            ),
            input = null
        )
        lessonEngine?.processLesson(noInputLesson) { viewTypes, instructionText, expectedAnswer ->
            // Confirm that the text contents are well concatenated
            assertTrue(instructionText == "var x = 1")
            // Confirm that the expected answer is an empty string since this has no input
            assertTrue(expectedAnswer == "")
            // Confirm that this generates three text display views. One for each content.
            assertTrue(viewTypes.size == 3)
            assertTrue(viewTypes.filterIsInstance<LessonEngine.ViewType.TextDisplay>().size == 3)
            // Confirm that this does not generate TextInput view
            assertTrue(viewTypes.filterIsInstance<LessonEngine.ViewType.TextInput>().isEmpty())
        }
    }

    @Test
    fun testProcessLessonWitInput() {
        val inputLesson = Lesson(
            id = 2,
            contents = arrayListOf(
                Content(text = "var ", color = "#FF0000"),
                Content(text = "number = ", color = "#FFFFFF"),
                Content(text = "1", color = "#0000FF")
            ),
            input = Input(startIndex = 4, endIndex = 10)
        )
        // NOTE: The second content "Content(text = "number = ", color = "#FFFFFF")" has white text color
        // The input starts at index 4 and ends at 10 giving us "number" and it is space for input
        // The remaining part of the second content " = " will be painted as white as text display
        lessonEngine?.processLesson(inputLesson) { viewTypes, instructionText, expectedAnswer ->
            // Confirm that the text contents are well concatenated
            assertTrue(instructionText == "var number = 1")
            // Confirm that the expected answer matches the substring with the given index
            assertTrue(expectedAnswer == instructionText.substring(inputLesson.input?.startIndex ?: 0, inputLesson.input?.endIndex ?: 0))
            // Confirm that we have the expected number of display views
            assertTrue(viewTypes.size == 4)
            // Confirm that we have 3 TextDisplay views
            assertTrue(viewTypes.filterIsInstance<LessonEngine.ViewType.TextDisplay>().size == 3)
            // Confirm that we have 1 TextInput view
            assertTrue(viewTypes.filterIsInstance<LessonEngine.ViewType.TextInput>().size == 1)
            // Confirm that the TextInput is at index 1
            assertTrue(viewTypes[1] is LessonEngine.ViewType.TextInput)
        }
    }
}