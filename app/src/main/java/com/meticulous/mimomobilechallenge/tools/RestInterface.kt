package com.meticulous.mimomobilechallenge.tools

import com.meticulous.mimomobilechallenge.models.LessonsData
import retrofit2.Call
import retrofit2.http.GET

interface RestInterface {
    @get:GET("/api/lessons")
    val lessons: Call<LessonsData>
}