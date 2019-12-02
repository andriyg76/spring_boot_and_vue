package eu.andriydc.edu.spring_boot_and_vue

import eu.andriydc.edu.spring_boot_and_vue.todo.Todo
import eu.andriydc.edu.spring_boot_and_vue.todo.TodoRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.stream.Stream
import javax.servlet.Filter


@SpringBootApplication
open class SpringBootAndVueApplication {
    @Bean
     fun init(repository: TodoRepository) : ApplicationRunner {
        return ApplicationRunner {
            Stream.of("Buy milk", "Eat pizza", "Write tutorial", "Study Vue.js", "Go kayaking").forEach { name ->
                val todo = Todo(title = name)
                repository.save(todo);
                repository.findAll().forEach(System.out::println);
            }
        }
    }

    @Bean
    fun simpleCorsFilter(): FilterRegistrationBean<*>? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        // *** URL below needs to match the Vue client URL and port ***
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        source.registerCorsConfiguration("/**", config)
        val bean: FilterRegistrationBean<*> = FilterRegistrationBean<Filter>(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<SpringBootAndVueApplication>(*args)
        }
    }
}