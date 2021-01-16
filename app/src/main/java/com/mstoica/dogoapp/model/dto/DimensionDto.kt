package com.mstoica.dogoapp.model.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DimensionDto(
    @SerializedName("imperial")
    val imperial: String,

    @SerializedName("metric")
    val metric: String
): Serializable