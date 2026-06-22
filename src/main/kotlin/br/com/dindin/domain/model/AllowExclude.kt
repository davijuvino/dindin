package br.com.dindin.domain.model

enum class AllowExcludeDto {
    ALLOW_ONLY,
    EXCLUDE,
    ALLOW,
    NONE,
    ;

    fun toDomain() =
        when (this) {
            ALLOW_ONLY -> AllowExclude.ALLOW_ONLY
            EXCLUDE -> AllowExclude.EXCLUDE
            ALLOW -> AllowExclude.ALLOW_ONLY
            NONE -> throw IllegalArgumentException()
        }
}