package ie.setu.controllers

import ie.setu.config.DbConfig


import ie.setu.domain.User
import ie.setu.domain.Workout
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import junit.framework.TestCase.assertEquals
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WorkoutControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    private val workout1 = workout.get(0)

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


    private fun retrieveAllWorkout(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/workout").asJson()
    }

    private fun retrieveWorkoutByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/workout").asJson()
    }

    private fun retrieveWorkoutByWorkoutId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/workout/${id}").asJson()
    }


    private fun deleteWorkoutByWorkoutId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/workout/$id").asString()
    }


    private fun deleteWorkoutByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/workout").asString()
    }
    //helper function to add a test user to the database
    private fun updatWorkout(
        id: Int, name: String, description: String, duration: Double, userId: Int,mincalories: Int
    ): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/workout/$id")
            .body(
                """
                { "name":"$name",
                  "description":"$description",
                  "duration":$duration,
                  "userId":$userId,
                  "mincalories":$mincalories
                }
            """.trimIndent()
            ).asJson()
    }
    @Nested
    inner class CreateWorkout {

        @Test
        fun `add an workout when a user exists for it, returns a 201 response`() {


            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addWorkoutResponse = addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id, workout[0].mincalories)
            Assertions.assertEquals(201, addWorkoutResponse.status)


            deleteUser(addedUser.id)
        }

        @Test
        fun `add an workout when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            Assertions.assertEquals(404, retrieveUserById(userId).status)

            val addWorkoutResponse = addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,userId,workout[0].mincalories
            )
            Assertions.assertEquals(404, addWorkoutResponse.status)
        }
    }
    @Nested
    inner class ReadWorkout {

        @Test
        fun `get all workout from the database returns 200 or 404 response`() {
            val response = retrieveAllWorkout()
            if (response.status == 200){
                val retrievedWorkout:ArrayList<Workout>  = jsonNodeToObject(response)
                assertEquals(0, retrievedWorkout.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all workout by user id when user and workout exists returns 200 response`() {
            //Arrange - add a user and 3 associated workout that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            )
            addWorkout(
                workout[1].name,workout[1].description, workout[1].duration,addedUser.id,workout[0].mincalories
            )
            addWorkout(
                workout[2].name,workout[2].description, workout[2].duration,addedUser.id,workout[0].mincalories
            )

            //Assert and Act - retrieve the three added activities by user id
            val response = retrieveWorkoutByUserId(addedUser.id)
            Assertions.assertEquals(200, response.status)
            val retrievedWorkout = jsonNodeToObject<Array<Workout>>(response)
            Assertions.assertEquals(3, retrievedWorkout.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all workout by user id when no activities exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the activities by user id
            val response = retrieveWorkoutByUserId(addedUser.id)
            Assertions.assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all workout by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve activities by user id
            val response = retrieveWorkoutByUserId(userId)
            Assertions.assertEquals(404, response.status)
        }

        @Test
        fun `get workout by workout id when no workout exists returns 404 response`() {
            //Arrange
            val workoutId = -1

            val response = retrieveWorkoutByWorkoutId(workoutId)
            Assertions.assertEquals(404, response.status)
        }


        @Test
        fun `get workout by workout id when workout exists returns 200 response`() {

            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addWorkoutResponse =  addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            )
            Assertions.assertEquals(201, addWorkoutResponse.status)
            val addedWorkout = jsonNodeToObject<Workout>(addWorkoutResponse)


            val response = retrieveWorkoutByWorkoutId(addedWorkout.id)
            Assertions.assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

    }
    @Nested
    inner class UpdateWorkouts {

        @Test
        fun `updating an workout by workout id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val id = -1

            //Arrange - check there is no user for -1 id
            Assertions.assertEquals(404, retrieveUserById(userId).status)
            val addWorkoutResponse =  updatWorkout(id, updatedWorkoutName, updatedWorkoutDescription,
                updatedWorkoutDuration,userId, updatedWorkoutCalories)
            print(addWorkoutResponse.status)
            assertEquals(404,addWorkoutResponse.status)


        }

        @Test
        fun `updating an workout by workout id when it exists, returns 204 response`() {


            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addWorkoutResponse = addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            )
           assertEquals(201, addWorkoutResponse.status)
            val addedWorkout = jsonNodeToObject<Workout>(addWorkoutResponse)


            val updatedWorkoutResponse = updatWorkout(addedWorkout.id, updatedWorkoutName,
                updatedWorkoutDescription, updatedWorkoutDuration, addedUser.id, updatedWorkoutCalories)
            assertEquals(204, updatedWorkoutResponse.status)

//            //Assert that the individual fields were all updated as expected
            val retrievedWorkoutResponse = retrieveWorkoutByWorkoutId(addedWorkout.id)
            val updatedWorkout = jsonNodeToObject<Workout>(retrievedWorkoutResponse)
         assertEquals(updatedWorkoutName, updatedWorkout.name)
          assertEquals(updatedWorkoutDescription, updatedWorkout.description)
           assertEquals(updatedWorkoutDuration, updatedWorkout.duration)
           assertEquals(updatedWorkoutCalories, updatedWorkout.mincalories)

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }
//
    @Nested
    inner class DeleteWorkouts {

        @Test
        fun `deleting an workout by aworkout id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assertions.assertEquals(404, deleteWorkoutByWorkoutId(-1).status)
        }

        @Test
        fun `deleting workout by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assertions.assertEquals(404, deleteWorkoutByUserId(-1).status)
        }

        @Test
        fun `deleting an workout by id when it exists, returns a 204 response`() {


            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addWorkoutResponse = addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            )
            Assertions.assertEquals(201, addWorkoutResponse.status)


            val addedWorkout = jsonNodeToObject<Workout>(addWorkoutResponse)
            Assertions.assertEquals(204, deleteWorkoutByWorkoutId(addedWorkout.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all workouts by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated activities that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val workoutResponse1 = addWorkout(
                workout[0].name,workout[0].description, workout[0].duration,addedUser.id,workout[0].mincalories
            )
            Assertions.assertEquals(201, workoutResponse1.status)
            val workoutResponse2 = addWorkout(
                workout[1].name,workout[1].description, workout[1].duration,addedUser.id,workout[1].mincalories
            )
            Assertions.assertEquals(201, workoutResponse1.status)
            val workoutResponse3 = addWorkout(
                workout[2].name,workout[2].description, workout[2].duration,addedUser.id,workout[2].mincalories
            )
            Assertions.assertEquals(201, workoutResponse1.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted activities
            val addedWorkout1 = jsonNodeToObject<Workout>(workoutResponse1)
            val addedWorkout2 = jsonNodeToObject<Workout>(workoutResponse2)
            val addedWorkout3 = jsonNodeToObject<Workout>(workoutResponse3)
            Assertions.assertEquals(404, retrieveWorkoutByWorkoutId(addedWorkout1.id).status)
            Assertions.assertEquals(404, retrieveWorkoutByWorkoutId(addedWorkout2.id).status)
            Assertions.assertEquals(404, retrieveWorkoutByWorkoutId(addedWorkout3.id).status)
        }
    }

}