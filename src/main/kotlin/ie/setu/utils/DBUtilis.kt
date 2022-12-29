package ie.setu.utils

import ie.setu.domain.*
import ie.setu.domain.db.*
import org.jetbrains.exposed.sql.ResultRow

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email]
)

fun mapToUserNote(it: ResultRow) = UserNote(
    id = it[UserNotes.id],
    title = it[UserNotes.title],
    noteText = it[UserNotes.noteText],
    shared = it[UserNotes.shared],
    userId = it[UserNotes.userId]
)


fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)

fun mapToWorkout(it: ResultRow) = Workout(
    id = it[Workouts.id],
    name = it[Workouts.name],
    description = it[Workouts.description],
    duration = it[Workouts.duration],
    userId = it[Workouts.userId],
    mincalories = it[Workouts.mincalories]
)
fun mapToWorkoutSession(it: ResultRow) = WorkoutSession(
    id = it[WorkoutSessions.id],
   started = it[WorkoutSessions.started],
    ended = it[WorkoutSessions.ended],
    totalCalories = it[WorkoutSessions.totalCalories],
    status = it[WorkoutSessions.status],
    workoutId = it[WorkoutSessions.workoutId],
    userId = it[WorkoutSessions.userId]

)