package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Activity

import ie.setu.domain.User
import ie.setu.domain.UserNote
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import junit.framework.TestCase.assertEquals
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserNotesControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    private val activity1 = activities.get(0)

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

    private fun addNote(title: String, text: String, shared: String,userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/notes")
            .body("""
                {
                   "title":"$title",
                   "noteText":"$text",
                   "shared":"$shared",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }
    private fun retrieveAllNotes(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/notes").asJson()
    }

    private fun retrieveNotesByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/notes").asJson()
    }
    private fun retrieveNotesById(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/notes/${id}").asJson()
    }

    private fun retrieveNotesByTitle(title: String): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/notes/title/'${title}'").asJson()
    }
    private fun deleteNotesById(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/notes/$id").asString()
    }


    private fun deleteNotesByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/notes").asString()
    }

    private fun updateNote(id: Int, title: String, text: String, shared: String,userId: Int):
            HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/notes/$id")
            .body(
                """
                {
                   "title":"$title",
                   "text":"$text",
                   "shared":"$shared",
                   "userId":$userId
                }
            """.trimIndent()
            ).asJson()
    }

    @Nested
    inner class CreateNote {

        @Test
        fun `add an note when a user exists for it, returns a 201 response`() {


            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addActivityResponse = addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            assertEquals(201, addActivityResponse.status)


            deleteUser(addedUser.id)
        }

        @Test
        fun `add an note when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            Assertions.assertEquals(404, retrieveUserById(userId).status)

            val addActivityResponse =  addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  userId
            )
            Assertions.assertEquals(404, addActivityResponse.status)
        }
    }
    @Nested
    inner class ReadNote {

        @Test
        fun `get all notes from the database returns 200 or 404 response`() {
            val response = retrieveAllNotes()
            if (response.status == 200){
                val retrievedNotes = jsonNodeToObject<Array<UserNote>>(response)
                Assertions.assertNotEquals(0, retrievedNotes.size)
            }
            else{
                Assertions.assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all notes by user id when user and notes exists returns 200 response`() {
            //Arrange - add a user and 3 associated activities that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            addNote(
                userNote[1].title, userNote[1].noteText,
                userNote[1].shared,  addedUser.id
            )
            addNote(
                userNote[2].title, userNote[2].noteText,
                userNote[2].shared,  addedUser.id
            )

            val response = retrieveNotesByUserId(addedUser.id)
            Assertions.assertEquals(200, response.status)
            val retrievedNotes = jsonNodeToObject<Array<UserNote>>(response)
            Assertions.assertEquals(3, retrievedNotes.size)

            //After - delete the added user and assert a 204 is returned (activities are cascade deleted)
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all notes by user id when no notes exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

           val  response = retrieveNotesByUserId(addedUser.id)
            Assertions.assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all notes by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve activities by user id
            val response = retrieveNotesByUserId(userId)
            Assertions.assertEquals(404, response.status)
        }

        @Test
        fun `get notes by note title when no notes exists returns 404 response`() {
            //Arrange
            val title ="my office"

            val response = retrieveNotesByTitle(title)
            Assertions.assertEquals(404, response.status)
        }


        @Test
        fun `get note by note id when note exists returns 200 response`() {

            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addNoteResponse =  addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            Assertions.assertEquals(201, addNoteResponse.status)
            val addedNote = jsonNodeToObject<UserNote>(addNoteResponse)


            val response = retrieveNotesByUserId(addedNote.userId)
            Assertions.assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)
        }

    }
    @Nested
    inner class UpdateNotes {

        @Test
        fun `updating an note by  id when note doesn't exist, returns a 404 response`() {
            val userId = -1
            val noteId = -1

            //Arrange - check there is no user for -1 id
            Assertions.assertEquals(404, retrieveUserById(userId).status)


         assertEquals(
                404, updateNote(noteId,
                    updatedtitle, updatedtext, updatedshared,
                     userId
                ).status
            )
        }

        @Test
        fun `updating a note by note id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())


              val addNoteResponse =  addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            Assertions.assertEquals(201, addNoteResponse.status)
           val addedNote = jsonNodeToObject<UserNote>(addNoteResponse)
            print(addedNote.id)
//
//
            print("addes note"+addedNote.id)
            val updatedNoteResponse = updateNote(addedNote.id, updatedtitle, updatedtext, updatedshared,addedUser.id)
            Assertions.assertEquals(204, updatedNoteResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedNoteResponse = retrieveNotesById(addedNote.id)
            print("retrieved "+retrievedNoteResponse+"\n")
            val updatedNote = jsonNodeToObject<UserNote>(retrievedNoteResponse)
            Assertions.assertEquals(updatedtitle, updatedNote.title)
            Assertions.assertEquals(updatedtext, updatedNote.noteText)
            Assertions.assertEquals(updatedshared, updatedNote.shared)


            //After - delete the user
            deleteUser(addedUser.id)
        }
    }
    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting note by note id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assertions.assertEquals(404, deleteNotesById(-1).status)
            retrieveNotesById(1)
        }

        @Test
        fun `deleting note by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a user that doesn't exist
            Assertions.assertEquals(404, deleteNotesByUserId(-1).status)
        }

        @Test
        fun `deleting an note by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addNoteResponse =  addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            Assertions.assertEquals(201, addNoteResponse.status)

            //Act & Assert - delete the added activity and assert a 204 is returned
            val addedActivity = jsonNodeToObject<UserNote>(addNoteResponse)
            Assertions.assertEquals(204, deleteNotesById(addedActivity.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all notes by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated activities that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addNoteResponse1 =  addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            Assertions.assertEquals(201, addNoteResponse1.status)
            val addNoteResponse2 = addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            Assertions.assertEquals(201, addNoteResponse2.status)
            val addNoteResponse3 = addNote(
                userNote[0].title, userNote[0].noteText,
                userNote[0].shared,  addedUser.id
            )
            Assertions.assertEquals(201, addNoteResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            Assertions.assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted activities
            val addedNote1 = jsonNodeToObject<UserNote>(addNoteResponse1)
            val addedNote2 = jsonNodeToObject<UserNote>(addNoteResponse2)
            val addedNote3 = jsonNodeToObject<UserNote>(addNoteResponse3)
            Assertions.assertEquals(404, retrieveNotesById(addedNote1.id).status)
            Assertions.assertEquals(404, retrieveNotesById(addedNote2.id).status)
            Assertions.assertEquals(404, retrieveNotesById(addedNote3.id).status)
        }
    }



}