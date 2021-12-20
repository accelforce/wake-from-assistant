package net.accelf.wakefromassistant.models

import org.hibernate.Hibernate
import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Configuration(
    @Id val id: Long,
    val clientId: String,
    val clientSecret: String,
    val baseUrl: String,
    val userId: String,
    val userPassword: String,
    val projectId: String,
) {
    constructor() : this(0, "", "", "", "", "", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Configuration

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , clientId = $clientId , baseUrl = $baseUrl , userId = $userId , projectId = $projectId )"
    }
}

interface ConfigurationRepository: CrudRepository<Configuration, Long> {
    fun findFirstById(id: Long = 0): Configuration?
}
