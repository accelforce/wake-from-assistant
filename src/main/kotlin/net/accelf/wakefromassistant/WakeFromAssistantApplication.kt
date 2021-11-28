package net.accelf.wakefromassistant

import net.accelf.wakefromassistant.models.Configuration
import net.accelf.wakefromassistant.models.ConfigurationRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import java.security.SecureRandom

@SpringBootApplication
class WakeFromAssistantApplication {

    @Bean
    fun configuration(configurationRepository: ConfigurationRepository): Configuration {
        val logger = LoggerFactory.getLogger(WakeFromAssistantApplication::class.java)

        return configurationRepository.findFirstById()
            ?.also {
                logger.info("Configuration found.")
            }
            ?: run {
                logger.info("Configuration not found. Filling with default values")

                val pool = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')
                val source = SecureRandom.getInstance("NativePRNGNonBlocking")
                val seq = sequence { while (true) { yield(pool[source.nextInt(pool.size)]) } }

                val clientSecret = seq.take(64).joinToString("")
                val userId = seq.take(16).joinToString("")
                val userPassword = seq.take(64).joinToString("")

                val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

                val configuration = configurationRepository.save(Configuration(
                    id = 0,
                    clientId = "google",
                    clientSecret = encoder.encode(clientSecret),
                    baseUrl = "https://wake-from-assistant.example.com",
                    userId = userId,
                    userPassword = encoder.encode(userPassword),
                    projectId = "wake-from-assistant",
                ))

                logger.info("")
                logger.info("== OAuth Client Information ==")
                logger.info("Client ID         : google")
                logger.info("Client secret     : $clientSecret")
                logger.info("")
                logger.info("== User Login Information ==")
                logger.info("User ID           : $userId")
                logger.info("User password     : $userPassword")
                logger.info("")
                logger.info("Hint: Secret values are shown only once.")
                logger.info("Hint: Access web interface to update generated values.")

                configuration
            }
    }
}

fun main(args: Array<String>) {
    runApplication<WakeFromAssistantApplication>(*args)
}
