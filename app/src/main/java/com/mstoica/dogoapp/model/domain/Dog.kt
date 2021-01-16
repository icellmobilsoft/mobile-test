package com.mstoica.dogoapp.model.domain

data class Dog (
    val imageId: String,
    val imageUrl: String,
    val breedInfo: String?,
    val temperament: String?,
    var favId: String? = null
)