package com.mstoica.dogoapp.model

import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.ImageSearchDto

object DomainModelConverter {
    fun convertToDog(searchResult: ImageSearchDto): Dog {
        val breedInfo = searchResult.breeds?.firstOrNull()

        return Dog(
            imageId = searchResult.id,
            imageUrl = searchResult.url,
            breedInfo = breedInfo?.name,
            temperament = breedInfo?.temperament,
        )
    }
}