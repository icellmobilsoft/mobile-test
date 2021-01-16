package com.mstoica.dogoapp.model

import com.google.gson.annotations.SerializedName

data class BreedDto (

    @SerializedName("id")
    override val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("temperament")
    val temperament: String,

    @SerializedName("life_span")
    val lifeSpan: String,

    @SerializedName("alt_names")
    val altNames: String?,

    @SerializedName("wikipedia_url")
    val wikipediaUrl: String?,

    @SerializedName("origin")
    val origin: String?,

    @SerializedName("weight")
    val weight: Any?,

    @SerializedName("country_code")
    val countryCode: String?,

    @SerializedName("height")
    val height: Any?

): BaseDto