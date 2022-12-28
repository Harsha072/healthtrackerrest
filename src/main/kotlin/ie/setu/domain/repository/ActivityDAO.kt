package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.utils.mapToActivity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.asLiteral
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ActivityDAO {

    //Get all the activities in the database regardless of user id
    fun getAll(): ArrayList<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it)) }
        }
        return activitiesList
    }

    //Find a specific activity by activity id
    fun findByActivityId(id: Int): Activity?{
        return transaction {
            Activities
                .select() { Activities.id eq id}
                .map{mapToActivity(it)}
                .firstOrNull()
        }
    }

    //Find all activities for a specific user id
    fun findByUserId(userId: Int): List<Activity>{
        return transaction {
            Activities
                .select {Activities.userId eq userId}
                .map {mapToActivity(it)}
        }
    }

    //Save an activity to the database
    fun save(activity: Activity):Int{
        return transaction {
            Activities.insert {
                it[description] = activity.description
                it[duration] = activity.duration
                it[calories] = activity.calories
                it[started] = activity.started
                it[userId] = activity.userId
            }
        } get Activities.id
    }
    //delete activity based on user id
    fun deleteActivityByUserId(userId: Int){
        return transaction{ Activities.deleteWhere{
            Activities.userId eq userId
        }
        }
    }

    fun deleteActivityByActivityId(ActivityId: Int){
        return transaction{ Activities.deleteWhere{
            Activities.id eq ActivityId
        }
        }
    }

    fun updateActivityBasedOnActivityId(ActivityId: Int, activityUpdates: Activity):Int {
      return  transaction {
            Activities.update ({
                Activities.id eq ActivityId}) {
                it[description] = activityUpdates.description
                it[duration] = activityUpdates.duration
                it[calories]=activityUpdates.calories
                it[started] =activityUpdates.started
                it[userId]=activityUpdates.userId
            }
       } and ActivityId
    }





}