package ie.setu.domain.db

import ie.setu.domain.db.Activities.references
import ie.setu.domain.db.Users.autoIncrement
import ie.setu.domain.db.Users.primaryKey
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserNotes : Table("notes") {
    val id = integer("id").autoIncrement().primaryKey()
    val title = varchar("title", 100)
    val text = varchar("text", 255)
    val shared = varchar("shared", 255)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}