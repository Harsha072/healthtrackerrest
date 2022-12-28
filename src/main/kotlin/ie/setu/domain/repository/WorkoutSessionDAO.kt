package ie.setu.domain.repository

import ie.setu.domain.Workout
import ie.setu.domain.WorkoutSession
import ie.setu.domain.db.WorkoutSessions
import ie.setu.domain.db.Workouts
import ie.setu.utils.mapToWorkout
import ie.setu.utils.mapToWorkoutSession
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class WorkoutSessionDAO {

    fun getAllWorkoutsSesiion(): ArrayList<WorkoutSession> {
        print("getAllWorkoutsSesiion\n")
        val workoutSessionList: ArrayList<WorkoutSession> = arrayListOf()
        transaction {
            WorkoutSessions.selectAll().map {
                workoutSessionList.add(mapToWorkoutSession(it)) }
        }

        return workoutSessionList
    }
    fun findByWorkoutSessionId(id: Int): WorkoutSession?{
        print("findByWorkoutSessionId\n")
        return transaction {
            WorkoutSessions
                .select() { WorkoutSessions.id eq id}
                .map{ mapToWorkoutSession(it) }
                .firstOrNull()
        }
    }
    fun findWorkoutSessionByUserId(userId: Int): List<WorkoutSession>{
        print("findWorkoutSessionByUserId\n")
        return transaction {
            WorkoutSessions
                .select {
                    WorkoutSessions.userId eq userId}
                .map { mapToWorkoutSession(it) }


        }
    }
    fun save(workoutSession: WorkoutSession):Int{
        return transaction {
            WorkoutSessions.insert {
                it[started] = workoutSession.started
                it[ended] = workoutSession.ended
                it[totalCalories] = workoutSession.totalCalories
                it[status] =  workoutSession.status
                it[workoutId] = workoutSession.workoutId
                it[userId]= workoutSession.userId
            }
        } get WorkoutSessions.id
    }

    fun deleteWorkoutSessionByUserId(userId: Int){
        print("deleteWorkoutSessionByUserId\n")
        return transaction{ WorkoutSessions.deleteWhere{
            WorkoutSessions.userId eq userId
        }
        }
    }

    fun deleteWorkoutSessionByWorkoutSessionId(WorkoutId: Int){
        print("deleteWorkoutSessionByWorkoutSessionId\n")
        return transaction{ WorkoutSessions.deleteWhere{
            WorkoutSessions.id eq WorkoutId
        }
        }
    }
    fun updateWorkoutSessionBasedOnWorkoutSessionId(WorkoutSessionId: Int, workoutSessionUpdates: WorkoutSession):Int {
        print("updateWorkoutSessionBasedOnWorkoutSessionId\n")
       return transaction {
            WorkoutSessions.update ({
                WorkoutSessions.id eq WorkoutSessionId}) {
                it[started] = workoutSessionUpdates.started
                it[ended] = workoutSessionUpdates.ended
                it[totalCalories] = workoutSessionUpdates.totalCalories
                it[status]=workoutSessionUpdates.status
                it[workoutId]=workoutSessionUpdates.workoutId
                it[userId]=workoutSessionUpdates.userId

            }
        }
    }

}