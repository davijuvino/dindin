package br.com.dindin.domain.model

import org.gotson.komga.language.lowerNotBlank
import java.io.Serializable
import jakarta.persistence.*

@Embeddable
class ContentRestrictions(
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "age_restriction")
    val ageRestriction: AgeRestriction? = null,

    paramLabelsAllow: Set<String> = emptySet(),
    paramLabelsExclude: Set<String> = emptySet()
) : Serializable {

    @field:ElementCollection
    @field:CollectionTable(name = "content_restrictions_labels_allow", joinColumns = [JoinColumn(name = "owner_id")])
    @field:Column(name = "label")
    val labelsAllow: Set<String> =
        paramLabelsAllow
            .lowerNotBlank()
            .toSet()
            .minus(paramLabelsExclude.lowerNotBlank().toSet())

    @field:ElementCollection
    @field:CollectionTable(name = "content_restrictions_labels_exclude", joinColumns = [JoinColumn(name = "owner_id")])
    @field:Column(name = "label")
    val labelsExclude: Set<String> = paramLabelsExclude.lowerNotBlank().toSet()

    //@field:Transient
    //val isRestricted: Boolean by lazy { ageRestriction != null || labelsAllow.isNotEmpty() || labelsExclude.isNotEmpty() }

    override fun toString(): String =
        "ContentRestrictions(ageRestriction=$ageRestriction, labelsAllow=$labelsAllow, labelsExclude=$labelsExclude)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContentRestrictions) return false

        if (ageRestriction != other.ageRestriction) return false
        if (labelsAllow != other.labelsAllow) return false
        if (labelsExclude != other.labelsExclude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ageRestriction?.hashCode() ?: 0
        result = 31 * result + labelsAllow.hashCode()
        result = 31 * result + labelsExclude.hashCode()
        return result
    }
}
