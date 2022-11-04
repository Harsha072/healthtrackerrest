package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Workouts : Table("workout") {

    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 100)
    val description = varchar("description", 100)
    val duration = double("duration")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val mincalories = integer("mincalories")
}