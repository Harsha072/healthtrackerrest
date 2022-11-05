package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.setu.domain.Activity
import ie.setu.domain.Workout
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.WorkoutDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object WorkoutController {
    private val userDao = UserDAO()
    private val workoutDAO = WorkoutDAO()
    @OpenApi(
        summary = "get all workouts",
        operationId = "getAllWorkouts",
        tags = ["Workouts"],
        path = "/api/workout",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Workout>::class)])]
    )
    fun getAllWorkouts(ctx: Context) {
val workouts = workoutDAO.getAllWorkouts()
     if (workoutDAO.getAllWorkouts()!=null) {
         val mapper = jacksonObjectMapper()
             .registerModule(JodaModule())
             .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

         ctx.json(mapper.writeValueAsString( workoutDAO.getAllWorkouts() ))
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
       ctx.json(workouts)
    }

    @OpenApi(
        summary = "get all workouts by user id",
        operationId = "getWorkoutsByUserId",
        tags = ["Workouts"],
        path = "/api/user/{user-id}/workout",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Workout>::class)])]
    )
    fun getWorkoutsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val workouts = workoutDAO.findWorkoutByUserId(ctx.pathParam("user-id").toInt())
            if (workouts.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.

                ctx.json(workouts)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary = "get all workouts by  id",
        operationId = "getWorkoutsById",
        tags = ["Workouts"],
        path = "/api/user/workout/{workout-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("workout-id", Int::class, "The workout ID")],
        responses  = [OpenApiResponse("204")]

    )
    fun getWorkoutsById(ctx: Context) {
        val workouts = workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt())
        if (workouts!=null) {
            //mapper handles the deserialization of Joda date into a String.

            ctx.json(workouts)
        }
        else{
            ctx.status(404)
        }
    }



    @OpenApi(
        summary = "add  workouts",
        operationId = "addWorkout",
        tags = ["Workouts"],
        path = "/api/workout",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Workout>::class)])]
    )
    fun addWorkout(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val workout : Workout = jsonToObject(ctx.body())
        val userId = userDao.findById(workout.userId)

        if(userId!=null){
            val workoutId = workoutDAO.save(workout)
            if (workoutId != null) {
                workout.id = workoutId
                ctx.json(workout)
                ctx.status(201)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }

    }
    @OpenApi(
        summary = "Delete workout by ID",
        operationId = "deleteWorkoutById",
        tags = ["Workout"],
        path = "/api/workout/{workout-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("workout-id", Int::class, "The workout ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteWorkoutById(ctx: Context) {

        if(workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt()) !=null){
         workoutDAO.deleteWorkoutByWorkoutId(ctx.pathParam("workout-id").toInt())
            ctx.status(204)
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary="Update workout by ID",
        operationId="updateWorkoutById",
        tags=["Workout"],
        path="/api/workout/{workout-id}",
        method= HttpMethod.PATCH,
        pathParams=[OpenApiParam("workout-id", Int::class, "The workout ID")],
        responses=[OpenApiResponse("204")]
    )

    fun updateWorkoutById(ctx: Context) {
        val workoutUpdates : Workout = jsonToObject(ctx.body())
        print("hello updates "+workoutUpdates)
        if(workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt())!=null){
            print("hello updates2 "+workoutUpdates)
       workoutDAO.updateWorkoutBasedOnWorkoutId(ctx.pathParam("workout-id").toInt(),workoutUpdates)
            ctx.status(204)
        }
        else{

            ctx.status(404)
        }

    }

    @OpenApi(
        summary = "Delete workout by userID",
        operationId = "deleteWorkoutById",
        tags = ["Workout"],
        path = "/api/users/{user-id}/workout",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteWorkoutByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            if (workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt()) != null) {
              workoutDAO.deleteWorkoutByUserId(ctx.pathParam("user-id").toInt())
                ctx.status(204)
            } else {
                ctx.status(404)
            }
        }
        else {
            ctx.status(404)
        }

    }

}