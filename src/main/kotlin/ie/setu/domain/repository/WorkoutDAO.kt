package ie.setu.domain.repository

import ie.setu.domain.Workout
import ie.setu.domain.db.Workouts
import ie.setu.utils.mapToWorkout
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class WorkoutDAO {
    fun getAllWorkouts(): ArrayList<Workout> {
        val workoutList: ArrayList<Workout> = arrayListOf()
        transaction {
            Workouts.selectAll().map {
                workoutList.add(mapToWorkout(it)) }
        }
print("HIIIIIIIIIIIIII"+workoutList)
        return workoutList
    }


    fun findByWorkoutId(id: Int): Workout?{
        return transaction {
            Workouts
                .select() { Workouts.id eq id}
                .map{mapToWorkout(it)}
                .firstOrNull()
        }
    }


    fun findWorkoutByUserId(userId: Int): List<Workout>{
        return transaction {
            Workouts
                .select {Workouts.userId eq userId}
                .map {mapToWorkout(it)}
        }
    }

    //Save an workout to the database
    fun save(workout: Workout):Int{
        return transaction {
            Workouts.insert {
                it[name] = workout.name
                it[description] = workout.description
                it[duration] = workout.duration
                it[userId] = workout.userId
                it[mincalories] =workout.mincalories
            }
        } get Workouts.id
    }
    fun deleteWorkoutByUserId(userId: Int){
        return transaction{ Workouts.deleteWhere{
            Workouts.userId eq userId
        }
        }
    }

    fun deleteWorkoutByWorkoutId(WorkoutId: Int){
        return transaction{ Workouts.deleteWhere{
            Workouts.id eq WorkoutId
        }
        }
    }

    fun updateWorkoutBasedOnWorkoutId(WorkoutId: Int, workoutUpdates: Workout) {
        transaction {
            Workouts.update ({
                Workouts.id eq WorkoutId}) {
                it[name] = workoutUpdates.name
                it[description] = workoutUpdates.description
                it[duration] = workoutUpdates.duration
                it[userId]=workoutUpdates.userId
                it[mincalories]=workoutUpdates.mincalories
            }
        }
    }
}