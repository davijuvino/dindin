package br.com.dindin.domain.model

enum class AgeRestriction(val minAge: Int?) {
    NONE(null),
    TEEN(13),
    YOUNG_ADULT(16),
    ADULT(18);

    companion object {
        fun fromMinAge(age: Int?): AgeRestriction =
            when {
                age == null -> NONE
                age >= 18 -> ADULT
                age >= 16 -> YOUNG_ADULT
                age >= 13 -> TEEN
                else -> NONE
            }
    }
}

enum class AllowExclude {
    ALLOW_ONLY,
    EXCLUDE,
}
