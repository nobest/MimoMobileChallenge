package com.meticulous.mimomobilechallenge.models

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("text") val text: String = "",
    @SerializedName("color") val color: String = ""
)

data class Input(
    @SerializedName("startIndex") val startIndex: Int = -1,
    @SerializedName("endIndex") val endIndex: Int = -1,
)

data class Lesson(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("content") val contents: List<Content>,
    @SerializedName("input") val input: Input? = null
)

data class LessonsData(
    @SerializedName("lessons") val lessons: List<Lesson>
)