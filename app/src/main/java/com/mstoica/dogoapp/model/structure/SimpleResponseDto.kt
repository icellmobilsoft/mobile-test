package com.mstoica.dogoapp.model.structure

import com.google.gson.annotations.SerializedName

data class SimpleResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("message")
    val message: String?
)