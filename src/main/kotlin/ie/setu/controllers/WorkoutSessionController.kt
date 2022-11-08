package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.setu.domain.Workout
import ie.setu.domain.WorkoutSession
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.WorkoutSessionDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object WorkoutSessionController {
    private val userDao = UserDAO();
    private val workoutSession = WorkoutSessionDAO()

    @OpenApi(
        summary = "get all workouts Session",
        operationId = "getAllWorkoutsSession",
        tags = ["WorkoutsSession"],
        path = "/api/workoutSession",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<WorkoutSession>::class)])]
    )
    fun getAllWorkoutsSession(ctx: Context) {
val workoutSessionList = workoutSession.getAllWorkoutsSesiion();
        if (workoutSession.getAllWorkoutsSesiion()!=null) {
            val mapper = jacksonObjectMapper()
                .registerModule(JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

            ctx.json(mapper.writeValueAsString( workoutSession.getAllWorkoutsSesiion() ))
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(workoutSessionList)
    }
    @OpenApi(
        summary = "get all workouts session by user id",
        operationId = "getWorkoutSessionByUserId",
        tags = ["WorkoutsSession"],
        path = "/api/user/{user-id}/workoutSession",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<WorkoutSession>::class)])]
    )

    fun getWorkoutSessionByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val workoutSession = workoutSession.findWorkoutSessionByUserId(ctx.pathParam("user-id").toInt())
            if (workoutSession.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.

                ctx.json(workoutSession)
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
        summary = "get all workouts session by  id",
        operationId = "getWorkoutSessionById",
        tags = ["WorkoutsSession"],
        path = "/api/workoutSession/{workoutSession-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("workoutSession-id", Int::class, "The workout sessionID")],
        responses  = [OpenApiResponse("204")]

    )
    fun getWorkoutSessionById(ctx: Context) {

        val workoutSession = workoutSession.findByWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt())
        if (workoutSession!=null) {
            //mapper handles the deserialization of Joda date into a String.

            ctx.json(workoutSession)
        }
        else{
            ctx.status(404)
        }
    }


    @OpenApi(
        summary = "add  workouts session",
        operationId = "addWorkoutSession",
        tags = ["WorkoutsSession"],
        path = "/api/workoutSession",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<WorkoutSession>::class)])]
    )

    fun addWorkoutSession(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val newworkoutSession : WorkoutSession = jsonToObject(ctx.body())

        if(userDao.findById(newworkoutSession.userId)!=null){
            val workoutSessionId = workoutSession.save(newworkoutSession)
            if (workoutSessionId != null) {
                newworkoutSession.id = workoutSessionId
                ctx.json(newworkoutSession)
                ctx.status(201)
            }
            else ctx.status(404)
        }
        else
            ctx.status(404)

    }
    @OpenApi(
        summary = "Delete workout session by ID",
        operationId = "deleteWorkoutSessionById",
        tags = ["WorkoutsSession"],
        path = "/api/workout/{workoutSession-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("workoutSession-id", Int::class, "The workout session ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteWorkoutSessionById(ctx: Context) {

        if(workoutSession.findByWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt()) !=null){
            workoutSession.deleteWorkoutSessionByWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt())
            ctx.status(204)
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary="Update workout  sessionby ID",
        operationId="updateWorkoutSessionById",
        tags=["WorkoutsSession"],
        path="/api/workout/{workoutSession-id}",
        method= HttpMethod.PATCH,
        pathParams=[OpenApiParam("workoutSession", Int::class, "The workout sessionID")],
        responses=[OpenApiResponse("204")]
    )

    fun updateWorkoutSessionById(ctx: Context) {
        val workoutSessionUpdates : WorkoutSession = jsonToObject(ctx.body())
        print("hello updates "+workoutSessionUpdates)
        if(workoutSession.findByWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt())!=null){
            print("hello updates2 "+workoutSessionUpdates)
            workoutSession.updateWorkoutSessionBasedOnWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt(),workoutSessionUpdates)
            ctx.status(204)
        }
        else{

            ctx.status(404)
        }

    }
    @OpenApi(
        summary = "Delete workout sessionby userID",
        operationId = "deleteWorkoutSessionByUserId",
        tags = ["WorkoutsSession"],
        path = "/api/users/{user-id}/workoutSession",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteWorkoutSessionByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            if (workoutSession.findByWorkoutSessionId(ctx.pathParam("workout-id").toInt()) != null) {
               workoutSession.deleteWorkoutSessionByUserId(ctx.pathParam("user-id").toInt())
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