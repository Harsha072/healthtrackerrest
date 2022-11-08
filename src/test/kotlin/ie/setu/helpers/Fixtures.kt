package ie.setu.helpers

import ie.setu.domain.*
import ie.setu.domain.db.*
import ie.setu.domain.repository.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.chrono.ISOChronology


val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 672"
val validEmail = "testuser672@test672.com"
val updatedName = "Updated Name"
val updatedEmail = "Updated Email"

val updatedDescription = "Updated Description"
val updatedDuration = 30.0
val updatedCalories = 945
val updatedStarted = DateTime.parse("2020-06-11T05:59:27.258Z")

val updatedWorkoutName = "updatedName"
val updatedWorkoutDescription = "Updated Description"
val updatedWorkoutDuration = 30.0
val updatedWorkoutCalories = 945

val updatedWorkoutSessionStarted = DateTime.parse("2020-06-11T05:59:27.258Z")
val updatedWorkoutSessionEnded =  DateTime.parse("2020-06-11T05:59:27.258Z")
val updatedtotalCalories = 30
val updatedStaus ="complted"

val updatedtitle = "my life"
val updatedtext =  "my rules"
val updatedshared = "true"


val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4)
)

val activities = arrayListOf<Activity>(
    Activity(id = 1, description = "Running", duration = 22.0, calories = 230, started = DateTime.now(), userId = 1),
    Activity(id = 2, description = "Hopping", duration = 10.5, calories = 80, started = DateTime.now(), userId = 1),
    Activity(id = 3, description = "Walking", duration = 12.0, calories = 120, started = DateTime.now(), userId = 2)
)

val workout = arrayListOf<Workout>(
    Workout(id=1,name = "HOME", description = "HOME training", duration = 7.5, userId = 1,mincalories=200),
    Workout(id=2,name = "strength", description = "heavy weight training", duration = 8.9, userId = 2,mincalories=400),
    Workout(id=3,name = "aerobic", description = "simple weight training", duration = 5.7, userId = 1,mincalories=300),
)
val workoutSession = arrayListOf<WorkoutSession>(
    WorkoutSession(id=1,started=DateTime.now(), ended=DateTime.now(), totalCalories = 300,status="completed", workoutId = 2, userId = 1),
    WorkoutSession(id=2,started=DateTime.now(), ended=DateTime.now(), totalCalories = 400,status="not completed", workoutId = 2, userId = 1),
    WorkoutSession(id=3,started=DateTime.now(), ended=DateTime.now(), totalCalories = 500,status="pending", workoutId = 1, userId = 3),
)
val userNote = arrayListOf<UserNote>(
    UserNote(id=1,title="My day", text="its going good", shared = "true", userId = 1),
    UserNote(id=2,title="My routine", text="usual", shared = "false", userId = 1),
    UserNote(id=3,title="My dog", text="is good boy", shared = "true", userId = 2),
)
fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(users[0])
    userDAO.save(users[1])
    userDAO.save(users[2])
    return userDAO
}

fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities[0])
    activityDAO.save(activities[1])
    activityDAO.save(activities[2])
    return activityDAO
}
fun populateWorkoutTable(): WorkoutDAO {
    SchemaUtils.create(Workouts)
    val workoutDAO = WorkoutDAO()
    workoutDAO.save(workout[0])
    workoutDAO.save(workout[1])
    workoutDAO.save(workout[2])
    return workoutDAO
}
fun populateWorkoutSessionTable(): WorkoutSessionDAO {
    SchemaUtils.create(WorkoutSessions)
    val workoutSessionDAO = WorkoutSessionDAO()
    workoutSessionDAO.save(workoutSession[0])
    workoutSessionDAO.save(workoutSession[1])
    workoutSessionDAO.save(workoutSession[2])
    return workoutSessionDAO
}
fun populateUserNotes(): UserNotesDAO {
    SchemaUtils.create(UserNotes)
    val uerNotesDAO = UserNotesDAO()
    uerNotesDAO.saveNote(userNote[0])
    uerNotesDAO.saveNote(userNote[1])
    uerNotesDAO.saveNote(userNote[2])
    return uerNotesDAO
}
