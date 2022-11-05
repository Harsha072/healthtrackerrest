package ie.setu.config

import ie.setu.controllers.*
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.swagger.v3.oas.models.info.Info
class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create {
            it.registerPlugin(getConfiguredOpenApiPlugin())
            it.defaultContentType = "application/json"
            it.jsonMapper(JavalinJackson(jsonObjectMapper()))
            it.enableWebjars()
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getHerokuAssignedPort())

        registerRoutes(app)
        return app
    }

    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/api/users") {
                get(UserController::getAllUsers)
                post(UserController::addUser)
                path("{user-id}"){
                    get(UserController::getUserByUserId)
                    delete(UserController::deleteUser)
                    patch(UserController::updateUser)
                    path("activities"){
                        get(ActivityController::getActivitiesByUserId)
                        delete(ActivityController::deleteActivityByUserId)

                    }
                    path("workout"){
                        get(WorkoutController::getWorkoutsByUserId)
                        delete(WorkoutController::deleteWorkoutByUserId)

                    }
                    path("workoutSession"){
                        get(WorkoutSessionController::getWorkoutSessionByUserId)
                        delete(WorkoutSessionController::deleteWorkoutSessionByUserId)

                    }
                    path("notes"){
                        get(UserNotesController::getNotesByUserId)
                        delete(UserNotesController::deleteNoteByUserId)
                    }
                }

                path("/email/{email}"){
                    get(UserController::getUserByEmail)
                }
            }
            path("/api/activities") {
                get(ActivityController::getAllActivities)
                post(ActivityController::addActivity)
                path("{activity-id}"){
                    get(ActivityController::getActivitiesByActivityId)
                    delete(ActivityController::deleteActivity)
                    patch(ActivityController::updateActivityById)
                }
            }
            path("/api/workout"){
                get(WorkoutController::getAllWorkouts)
                post(WorkoutController::addWorkout)
                path("{workout-id}"){
                    get(WorkoutController::getWorkoutsById)
                    delete(WorkoutController::deleteWorkoutById)
                    patch(WorkoutController::updateWorkoutById)
                }

            }
            path("/api/workoutSession"){
                get(WorkoutSessionController::getAllWorkoutsSession)
                post(WorkoutSessionController::addWorkoutSession)
                path("{workoutSession-id}"){
                    get(WorkoutSessionController::getWorkoutSessionById)
                    delete(WorkoutSessionController::deleteWorkoutSessionById)
                    patch(WorkoutSessionController::updateWorkoutSessionById)
                }
            }
            path("/api/notes"){
                get(UserNotesController::getAllNotes)
               post(UserNotesController::addNote)
                path("{notes-id}"){
                    get(UserNotesController::getUserNoteByNoteId)
                    patch(UserNotesController::updateUserNote)
                    delete(UserNotesController::deleteNoteById)
                }
                path("title/{title}"){
                    get(UserNotesController::getNotesByTitle)
                }


            }
        }
    }

    fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
        OpenApiOptions(
            Info().apply {
                title("Health Tracker App")
                version("1.0")
                description("Health Tracker API")
            }
        ).apply {
            path("/swagger-docs") // endpoint for OpenAPI json
            swagger(SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
            reDoc(ReDocOptions("/redoc")) // endpoint for redoc
        }
    )
}