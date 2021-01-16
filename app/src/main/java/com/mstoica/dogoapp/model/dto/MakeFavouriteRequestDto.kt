package com.mstoica.dogoapp.model.dto

import com.google.gson.annotations.SerializedName

data class MakeFavouriteRequestDto(
    @SerializedName("image_id")
    val imageId: String,
    @SerializedName("sub_id")
    val userName: String
)