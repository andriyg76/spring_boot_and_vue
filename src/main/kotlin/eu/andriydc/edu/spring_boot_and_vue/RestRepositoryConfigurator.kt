package eu.andriydc.edu.spring_boot_and_vue

import eu.andriydc.edu.spring_boot_and_vue.todo.Todo
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.stereotype.Component

/**
 * IDs are not returned by RestRepository by default. I like them exposed so that the client can easily find
 * the ID of created and listed resources.
 */
@Component
class RestRepositoryConfigurator : RepositoryRestConfigurer {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(Todo::class.java)
    }
}