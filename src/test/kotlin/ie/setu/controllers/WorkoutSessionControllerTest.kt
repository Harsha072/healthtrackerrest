package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.Workout
import ie.setu.domain.WorkoutSession
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import kotlinx.coroutines.flow.SharingStarted
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WorkoutSessionControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    private fun addUser(name: String, email: String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\"}")
            .asJson()
    }
    private fun retrieveUserById(id: Int): HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${id}").asString()
    }
    private fun deleteUser(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id").asString()
    }
    private fun addWorkout(name:String,description: String, duration: Double, userId: Int,mincalories:Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/workout")
            .body("""
                { "name":"$name",
                   "description":"$description",
                   "duration":$duration,
                   "userId":$userId,
                   "mincalories":$mincalories
                }
            """.trimIndent())
            .asJson()
    }
    private fun addWorkoutSession(started: DateTime,ended: DateTime, totalCalories: Int, status:String,workoutId:Int,userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/workoutSession")
            .body("""
                { "started":"$started",
                   "ended":"$ended",
                   "totalCalories":$totalCalories,
                   "status":"$status",
                   "workoutId":$workoutId,
                   "userId":$userId
               
                }
            """.trimIndent())
            .asJson()
    }



    private fun retrieveAllWorkoutSession(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/workotSession").asJson()
    }

    private fun retrieveWorkoutSessionByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/workoutSession").asJson()
    }
//
    private fun retrieveSessionBySessionId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/workoutSession/${id}").asJson()
    }
//
    //helper function to delete an activity by activity id
    private fun deleteSessionBySessionId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/workoutSession/$id").asString()
    }
//
    private fun deleteSessionByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/workoutSession").asString()
    }
//
//
    private fun updateSession(id:Int,
    started: DateTime,ended: DateTime, totalCalories: Int, status:String,workoutId:Int,userId: Int
    ): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/workoutSession/$id")
            .body(
                """
                 { "started":"$started",
                   "ended":"$ended",
                   "totalCalories":$totalCalories,
                   "status":"$status",
                   "workoutId":$workoutId,
                   "userId":$userId
               
                }
            """.trimIndent()
            ).asJson()
    }
    @Nested
    inner class CreateSession {

        @Test
        fun `add an session when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated activity that we plan to do a deleted on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addedWokout: Workout = jsonToObject(addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            ).body.toString())

            val addsessionResponse = addWorkoutSession(
                workoutSession[0].started, workoutSession[0].ended,
                workoutSession[0].totalCalories, workoutSession[0].status,addedWokout.id, addedUser.id
            )
            Assertions.assertEquals(201, addsessionResponse.status)

            //After - delete the user (Activity will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an session when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1

            Assertions.assertEquals(404, retrieveUserById(userId).status)

            val addSessionResponse = addWorkoutSession(
                workoutSession[0].started, workoutSession[0].ended,
                workoutSession[0].totalCalories, workoutSession[0].status,1, userId
            )
            Assertions.assertEquals(404, addSessionResponse.status)
        }
    }

    @Nested
    inner class ReadWorkoutSession {

        @Test
        fun `get all session from the database returns 200 or 404 response`() {
            val response = retrieveAllWorkoutSession()
            if (response.status == 200){
                val retrievedActivities = jsonNodeToObject<Array<Activity>>(response)
                Assertions.assertNotEquals(0, retrievedActivities.size)
            }
            else{
                Assertions.assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all session by user id when user and session exists returns 200 response`() {
            //Arrange - add a user and 3 associated activities that we plan to retrieve

            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addedWokout1: Workout = jsonToObject(addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            ).body.toString())
            val addedWokout2: Workout = jsonToObject(addWorkout(
                workout[1].name,workout[1].description, workout[1].duration,addedUser.id,workout[1].mincalories
            ).body.toString())
            val addedWokout3: Workout = jsonToObject(addWorkout(
                workout[2].name,workout[2].description, workout[2].duration,addedUser.id,workout[2].mincalories
            ).body.toString())
            addWorkoutSession(
                workoutSession[0].started, workoutSession[0].ended,
                workoutSession[0].totalCalories, workoutSession[0].status,addedWokout1.id, addedUser.id
            )
            addWorkoutSession(
                workoutSession[1].started, workoutSession[1].ended,
                workoutSession[1].totalCalories, workoutSession[1].status,addedWokout2.id, addedUser.id
            )
            addWorkoutSession(
                workoutSession[2].started, workoutSession[2].ended,
                workoutSession[2].totalCalories, workoutSession[2].status,addedWokout3.id, addedUser.id
            )

            //Assert and Act - retrieve the three added activities by user id
            val response = retrieveWorkoutSessionByUserId(addedUser.id)
            Assertions.assertEquals(200, response.status)
            val retrievedActivities = jsonNodeToObject<Array<WorkoutSession>>(response)
            Assertions.assertEquals(3, retrievedActivities.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }
//
        @Test
        fun `get all session by user id when no session exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the activities by user id
            val response = retrieveWorkoutSessionByUserId(addedUser.id)
            Assertions.assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }
//
       @Test
        fun `get all session by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve activities by user id
            val response = retrieveWorkoutSessionByUserId(userId)
            Assertions.assertEquals(404, response.status)
        }
//
        @Test
        fun `get session by session id when no session exists returns 404 response`() {
            //Arrange
            val sessionId = -1
            val response = retrieveSessionBySessionId(sessionId)
            Assertions.assertEquals(404, response.status)
        }


        @Test
        fun `get session by session id when session exists returns 200 response`() {
            //Arrange - add a user and associated activity
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addedWokout1: Workout = jsonToObject(addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            ).body.toString())

            val addSessionResponse=addWorkoutSession(
                workoutSession[1].started, workoutSession[1].ended,
                workoutSession[1].totalCalories, workoutSession[1].status,addedWokout1.id, addedUser.id
            )
            Assertions.assertEquals(201, addSessionResponse.status)
            val addedSession = jsonNodeToObject<WorkoutSession>(addSessionResponse)

            //Act & Assert - retrieve the activity by activity id
            val response = retrieveSessionBySessionId(addedSession.id)
            Assertions.assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

    }
//
    @Nested
    inner class UpdateSession {

        @Test
        fun `updating an session by session id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val workoutid=1
            val workoutSession = -1

            //Arrange - check there is no user for -1 id
            Assertions.assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an activity/user that doesn't exist
            Assertions.assertEquals(
                404, updateSession(
                    workoutSession, updatedWorkoutSessionStarted, updatedWorkoutSessionEnded,
                    updatedtotalCalories, updatedStaus,workoutid, userId
                ).status
            )
        }

        @Test
        fun `updating an session by session id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addedWokout1: Workout = jsonToObject(addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            ).body.toString())

            val addSessionResponse=addWorkoutSession(
                workoutSession[1].started, workoutSession[1].ended,
                workoutSession[1].totalCalories, workoutSession[1].status,addedWokout1.id, addedUser.id
            )
            Assertions.assertEquals(201, addSessionResponse.status)
            val addedSession = jsonNodeToObject<WorkoutSession>(addSessionResponse)

            //Act & Assert - update the added activity and assert a 204 is returned
            val updatedActivityResponse =  updateSession( addedSession.id,updatedWorkoutSessionStarted, updatedWorkoutSessionEnded,
                updatedtotalCalories, updatedStaus,addedWokout1.id, addedUser.id)
            Assertions.assertEquals(204, updatedActivityResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedSessionResponse = retrieveSessionBySessionId(addedSession.id)
            val updatedSession = jsonNodeToObject<WorkoutSession>(retrievedSessionResponse)
            Assertions.assertEquals(updatedWorkoutSessionStarted, updatedSession.started)
            Assertions.assertEquals(updatedWorkoutSessionEnded, updatedSession.ended)
            Assertions.assertEquals(updatedtotalCalories, updatedSession.totalCalories)
            Assertions.assertEquals(updatedStaus, updatedSession.status)

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }
//
    @Nested
    inner class DeleteSession {

        @Test
        fun `deleting an session by session id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assertions.assertEquals(404, deleteSessionBySessionId(-1).status)
        }

        @Test
        fun `deleting session by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assertions.assertEquals(404, deleteSessionByUserId(-1).status)
        }

        @Test
        fun `deleting an session by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addedWokout1: Workout = jsonToObject(addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            ).body.toString())

            val addSessionResponse=addWorkoutSession(
                workoutSession[1].started, workoutSession[1].ended,
                workoutSession[1].totalCalories, workoutSession[1].status,addedWokout1.id, addedUser.id
            )
            Assertions.assertEquals(201, addSessionResponse.status)
//
//
            val addedSession = jsonNodeToObject<WorkoutSession>(addSessionResponse)
            Assertions.assertEquals(204, deleteSessionBySessionId(addedSession.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }
//
        @Test
        fun `deleting all session by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated activities that we plan to do a cascade delete
    val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
    val addedWokout1: Workout = jsonToObject(addWorkout(
        workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
    ).body.toString())

    val addSessionResponse1=addWorkoutSession(
        workoutSession[0].started, workoutSession[0].ended,
        workoutSession[0].totalCalories, workoutSession[0].status,addedWokout1.id, addedUser.id
    )
    val addSessionResponse2=addWorkoutSession(
        workoutSession[1].started, workoutSession[1].ended,
        workoutSession[1].totalCalories, workoutSession[1].status,addedWokout1.id, addedUser.id
    )
    val addSessionResponse3=addWorkoutSession(
        workoutSession[2].started, workoutSession[2].ended,
        workoutSession[2].totalCalories, workoutSession[1].status,addedWokout1.id, addedUser.id
    )
            Assertions.assertEquals(201, addSessionResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted activities
            val addedSession1 = jsonNodeToObject<WorkoutSession>(addSessionResponse1)
            val addedSession2 = jsonNodeToObject<WorkoutSession>(addSessionResponse2)
            val addedSession3 = jsonNodeToObject<WorkoutSession>(addSessionResponse3)
            Assertions.assertEquals(404, retrieveSessionBySessionId(addedSession1.id).status)
            Assertions.assertEquals(404, retrieveSessionBySessionId(addedSession2.id).status)
            Assertions.assertEquals(404, retrieveSessionBySessionId(addedSession3.id).status)
        }
    }
}