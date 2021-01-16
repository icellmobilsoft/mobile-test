package com.mstoica.dogoapp.model.dto

import com.google.gson.annotations.SerializedName
import com.mstoica.dogoapp.model.structure.BaseDto

data class ImageSearchDto(
    @SerializedName("id")
    override val id: String,

    @SerializedName("breeds")
    val breeds: List<BreedDto>,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("url")
    val url: String
): BaseDto

enum class ImageSize(val queryParamText: String) {
    FULL("full"),
    MEDIUM("med"),
    SMALL("small"),
    THUMBNAIL("thumb");
}
