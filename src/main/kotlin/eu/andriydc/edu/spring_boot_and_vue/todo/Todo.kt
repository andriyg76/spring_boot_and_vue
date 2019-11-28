package eu.andriydc.edu.spring_boot_and_vue.todo

import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
@Data
@NoArgsConstructor
data class Todo constructor(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var title: String? = null,
    var completed: Boolean? = false
)
