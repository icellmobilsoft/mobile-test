package com.mstoica.dogoapp.model.dto

import com.google.gson.annotations.SerializedName
import com.mstoica.dogoapp.model.structure.BaseDto

data class ImageMetaDto(
    @SerializedName("id")
    override val id: String,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("url")
    val url: String
): BaseDto