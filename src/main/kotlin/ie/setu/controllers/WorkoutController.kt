package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.setu.domain.Workout
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.WorkoutDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context

object WorkoutController {
    private val userDao = UserDAO()
    private val workoutDAO = WorkoutDAO()
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

    fun deleteWorkoutById(ctx: Context) {

        if(workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt()) !=null){
         workoutDAO.deleteWorkoutByWorkoutId(ctx.pathParam("workout-id").toInt())
            ctx.status(204)
        }
        else{
            ctx.status(404)
        }
    }

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