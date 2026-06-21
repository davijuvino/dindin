package br.com.dindin.interfaces

import br.com.dindin.domain.model.AllowExclude

enum class AllowExcludeDto {
    ALLOW_ONLY,
    EXCLUDE,
    NONE,
    ;

    fun toDomain() =
        when (this) {
            ALLOW_ONLY -> AllowExclude.ALLOW_ONLY
            EXCLUDE -> AllowExclude.EXCLUDE
            NONE -> throw IllegalArgumentException()
        }
}