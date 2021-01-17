package com.mstoica.dogoapp.model.domain

import java.io.Serializable

data class Dog (
    val imageId: String,
    val imageUrl: String,
    val breedInfo: String?,
    val temperament: String?,
    var favId: String? = null
): Serializable {
    var tag: String? = null

    val liked: Boolean
        get() = favId != null
}