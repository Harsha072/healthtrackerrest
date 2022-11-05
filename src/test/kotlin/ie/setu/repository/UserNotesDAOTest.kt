package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.UserNote
import ie.setu.domain.db.Activities
import ie.setu.domain.db.UserNotes
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserNotesDAO
import ie.setu.helpers.populateActivityTable
import ie.setu.helpers.populateUserNotes
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.userNote
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserNotesDAOTest {
    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    @Nested
    inner class CreateNotes {
        @Test
        fun `multiple notes added to table can be retrieved successfully`() {
            transaction {


                //Arrange - create and populate tables with three users and three activities
                 populateUserTable()
                val userNotesDAO = populateUserNotes()
                //Act & Assert
                TestCase.assertEquals(3, userNotesDAO.getAll().size)
                TestCase.assertEquals(userNote[0], userNotesDAO.findByTitle(userNote[0].title))

            }
        }
    }
    @Nested
    inner class ReadNotes {

        @Test
        fun `getting all notes stored in table successfully`(){
            transaction {
                populateUserTable()
               val userNotes = populateUserNotes()
                TestCase.assertEquals(3, userNotes.getAll().size)
            }



        }
        @Test
        fun `get notes by user id that has no related notes, results in no record found`(){
            transaction {
               populateUserTable()
                val userNotes = populateUserNotes()
                TestCase.assertEquals(0, userNotes.findNoteByUserId(8).size)
            }



        }

        @Test
        fun `get note by user id that has related notes, results in correct note returned`() {
            transaction {
                populateUserTable()
                val userNotes = populateUserNotes()
                TestCase.assertEquals(userNote[0], userNotes.findNoteByUserId(1).get(0))
                TestCase.assertEquals(userNote[1], userNotes.findNoteByUserId(1).get(1))



            }

        }
        @Test
        fun `get all notes over empty table returns none`() {
            transaction {

                //Arrange - create and setup activityDAO object
                SchemaUtils.create(UserNotes)
                val userNoteDAO = UserNotesDAO()

                //Act & Assert
                TestCase.assertEquals(0, userNoteDAO.getAll().size)
            }
        }
        @Test
        fun `get note by title that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
               populateUserTable()
                val userNotes = populateUserNotes()
                //Act & Assert
                TestCase.assertEquals(null, userNotes.findByTitle("My family"))
            }
        }
        @Test
        fun `get note by id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val userNotes = populateUserNotes()
                //Act & Assert
                TestCase.assertEquals(null, userNotes.findNoteById(-1))
            }
        }

    }
    @Nested
    inner class UpdateNotes {

        @Test
        fun `updating existing note in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
               populateUserTable()
                val userNotes = populateUserNotes()

                //Act & Assert
                val noteupdated =  UserNote(id=1,title="My life", text="its going good", shared = "true", userId = 1)
                userNotes.updateNote(noteupdated.id, noteupdated)
                TestCase.assertEquals(noteupdated, userNotes.findNoteById(noteupdated.id))
            }
        }

        @Test
        fun `updating non-existant note in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
              populateUserTable()
                val userNotes = populateUserNotes()
                //Act & Assert
                val noteupdated = UserNote(id=1,title="My life", text="its going good", shared = "true", userId = 1)
                userNotes.updateNote(4, noteupdated)
             assertEquals(null, userNotes.findNoteById(9))
                assertEquals(3, userNotes.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteNotes {

        @Test
        fun `deleting a non-existant note (by title) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
              populateUserTable()
                val userNotes = populateUserNotes()

                //Act & Assert
                assertEquals(3, userNotes.getAll().size)
                userNotes.deleteNoteById(4)
                assertEquals(3, userNotes.getAll().size)
            }
        }

        @Test
        fun `deleting an existing note (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val userNotes = populateUserNotes()
                //Act & Assert
                assertEquals(3, userNotes.getAll().size)
                userNotes.deleteNoteById(userNote[0].id)
            assertEquals(2, userNotes.getAll().size)
            }
        }


        @Test
        fun `deleting notes when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
             populateUserTable()
                val userNotes = populateUserNotes()

                //Act & Assert
           assertEquals(3, userNotes.getAll().size)
                userNotes.deleteNoteByUserId(8)
               assertEquals(3, userNotes.getAll().size)
            }
        }

        @Test
        fun `deleting notes when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
             populateUserTable()
                val userNotes = populateUserNotes()

                //Act & Assert
              assertEquals(3, userNotes.getAll().size)
                userNotes.deleteNoteByUserId(1)
               assertEquals(1, userNotes.getAll().size)
            }
        }
    }
}