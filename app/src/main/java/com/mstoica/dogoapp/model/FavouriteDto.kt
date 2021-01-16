package com.mstoica.dogoapp.model

import com.google.gson.annotations.SerializedName

data class FavouriteDto(

    @SerializedName("id")
    override val id: String,

    @SerializedName("image_id")
    val imageId: String,

    @SerializedName("sub_id")
    val subId: String,

    @SerializedName("created_at")
    val createdAt: String,
): BaseDto