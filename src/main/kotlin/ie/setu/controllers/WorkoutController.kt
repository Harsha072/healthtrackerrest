package ie.setu.controllers

import ie.setu.domain.Workout
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.WorkoutDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context

object WorkoutController {
    private val userDao = UserDAO()
    private val workoutDAO = WorkoutDAO()
    fun getAllWorkouts(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val workouts = workoutDAO.getAllWorkouts()
        if (workouts.size != 0) {
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
                ctx.json(404)
            }
        }
        else{
            ctx.json(404)
        }
    }
    fun getWorkoutsById(ctx: Context) {

        val workouts = workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt())
        if (workouts!=null) {
            //mapper handles the deserialization of Joda date into a String.

            ctx.json(workouts)
        }
        else{
            ctx.json(404)
        }
    }




    fun addWorkout(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val workout : Workout = jsonToObject(ctx.body())
        val workoutId = workoutDAO.save(workout)
        if (workoutId != null) {
            workout.id = workoutId
            ctx.json(workout)
            ctx.status(201)
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

//        val activitiesList = activityDAO.findByActivityId(ctx.pathParam("activity-id").toInt())
        if(workoutDAO.findByWorkoutId(ctx.pathParam("workout-id").toInt())!=null){
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
              workoutDAO.deleteWorkoutByUserId(ctx.pathParam("workout-id").toInt())
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