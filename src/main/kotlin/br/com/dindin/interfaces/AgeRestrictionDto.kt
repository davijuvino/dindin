package br.com.dindin.interfaces

import br.com.dindin.domain.model.AgeRestriction
import br.com.dindin.domain.model.AllowExclude
import br.com.dindin.interfaces.AllowExcludeDto

data class AgeRestrictionDto(
    val restriction: AllowExcludeDto,
    val age: Int?
) {
    fun toDomain(): AgeRestriction {
        val domainRestriction = when (restriction) {
            AllowExcludeDto.NONE -> AllowExclude.NONE
            AllowExcludeDto.ALLOW -> AllowExclude.ALLOW
            AllowExcludeDto.EXCLUDE -> AllowExclude.EXCLUDE
        }

        require(domainRestriction != AllowExclude.NONE) { "Cannot convert NONE to domain AgeRestriction" }
        val nonNullAge = age ?: throw IllegalArgumentException("age is required when restriction is not NONE")

        return AgeRestriction(domainRestriction, nonNullAge)
    }
}