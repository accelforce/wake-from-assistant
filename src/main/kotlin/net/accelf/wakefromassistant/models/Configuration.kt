package net.accelf.wakefromassistant.models

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
}

interface ConfigurationRepository: CrudRepository<Configuration, Long> {
    fun findFirstById(id: Long = 0): Configuration?
}
