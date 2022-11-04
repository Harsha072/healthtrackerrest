package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object WorkoutSessions : Table("workoutsession") {
    val id = integer("id").autoIncrement().primaryKey()
   val started = datetime("started")
    val ended = datetime("ended")
    val totalCalories = integer("totalcalories")
    val status = varchar("status", 100)
    val workoutId = integer("workout_id").references(Workouts.id, onDelete = ReferenceOption.CASCADE)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}