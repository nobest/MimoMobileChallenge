package com.meticulous.mimomobilechallenge.tools

import android.util.Log
import com.meticulous.mimomobilechallenge.models.Lesson
import com.meticulous.mimomobilechallenge.models.LessonsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonRepository constructor(val database: MimoChallengeDb){
    private val TAG = "MIMO_Repository"
    val lessons: MutableList<Lesson> = mutableListOf()
    val lessonFetchedListeners = mutableSetOf<LessonFetchedListener>()
    var lastIndex = 0
    private val coroutineDbScope = CoroutineScope(Dispatchers.IO)

    init {
        fetchLessons()
    }

    private fun fetchLessons() {
        Log.d(TAG, "fetchLessons called")
        RestService.getRetrofit().lessons.enqueue(
            object : Callback<LessonsData> {
                override fun onResponse(call: Call<LessonsData>, response: Response<LessonsData>) {
                    Log.d(
                        TAG,
                        "getLessons.onResponse got response: ${response.body()}"
                    )
                    response.body()?.let { lessonData ->
                        lessons.clear()
                        lessons.addAll(lessonData.lessons)
                        lessonFetchedListeners.forEach { it.onLessonFetched() }
                    }
                }

                override fun onFailure(call: Call<LessonsData>, t: Throwable) {
                    Log.d(TAG, "getLessons.onFailure got Throwable: $t")
                }
            })
    }

    fun getNextLesson(): Lesson? {
        Log.d(TAG, "getNextLesson called lastIndex: $lastIndex and size: ${lessons.size}")
        if (lastIndex >= lessons.size) {
            Log.w(TAG, "getNextLesson  returning null")
            return null
        }
        return lessons[lastIndex++]
    }

    fun resetIndex() {
        Log.d(TAG, "resetIndex called")
        lastIndex = 0
    }

    fun registerForLessonFetched(listener: LessonFetchedListener) {
        Log.d(TAG, "registerForLessonFetched called")
        lessonFetchedListeners.add(listener)
        if (lessons.isNotEmpty()) listener.onLessonFetched()
    }

    fun deregisterFromLessonFetched(listener: LessonFetchedListener) {
        Log.d(TAG, "deregisterFromLessonFetched called")
        lessonFetchedListeners.remove(listener)
    }

    fun saveLesson(completedLesson: LessonComplete) {
        coroutineDbScope.launch {
            database.lessonCompleteDao().insertAll(completedLesson)
        }
    }

}