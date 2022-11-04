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

object WorkoutSessionController {
    private val userDao = UserDAO();
    private val workoutSession = WorkoutSessionDAO()

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

    fun deleteWorkoutSessionById(ctx: Context) {

        if(workoutSession.findByWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt()) !=null){
            workoutSession.deleteWorkoutSessionByWorkoutSessionId(ctx.pathParam("workoutSession-id").toInt())
            ctx.status(204)
        }
        else{
            ctx.status(404)
        }
    }

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