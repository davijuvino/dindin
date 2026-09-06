package br.com.dindin.domain.model

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.net.URL
import java.nio.file.Path
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.nio.file.Paths

@Entity
@Table(name = "library")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache.library")
class Library(
    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    @NotNull
    @Column(name = "root", nullable = false)
    val root: URL
) : Auditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    constructor(name: String, root: String) : this(name, Paths.get(root).toUri().toURL())

    fun path(): Path = Paths.get(this.root.toURI())

    override fun toString() = "Library($id, $name, ${root.toURI().path})"
}
