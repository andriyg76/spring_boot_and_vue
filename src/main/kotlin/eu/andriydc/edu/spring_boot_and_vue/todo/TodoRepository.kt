package eu.andriydc.edu.spring_boot_and_vue.todo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface TodoRepository : JpaRepository<Todo, Long>