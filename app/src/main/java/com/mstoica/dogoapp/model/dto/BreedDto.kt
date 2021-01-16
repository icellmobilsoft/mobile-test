package com.mstoica.dogoapp.model.dto

import com.google.gson.annotations.SerializedName
import com.mstoica.dogoapp.model.structure.BaseDto

data class BreedDto (

    @SerializedName("id")
    override val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("temperament")
    val temperament: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("life_span")
    val lifeSpan: String,

    @SerializedName("alt_names")
    val altNames: String?,

    @SerializedName("wikipedia_url")
    val wikipediaUrl: String?,

    @SerializedName("origin")
    val origin: String?,

    @SerializedName("weight")
    val weight: DimensionDto?,

    @SerializedName("country_code")
    val countryCode: String?,

    @SerializedName("height")
    val height: DimensionDto?,

    @SerializedName("reference_image_id")
    val referenceImageId: String?,

    @SerializedName("image")
    val imageMeta: ImageMetaDto

): BaseDto