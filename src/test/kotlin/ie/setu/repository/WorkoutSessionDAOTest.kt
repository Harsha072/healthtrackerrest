package ie.setu.repository

import ie.setu.domain.WorkoutSession
import ie.setu.domain.db.WorkoutSessions
import ie.setu.domain.repository.WorkoutSessionDAO
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.populateWorkoutSessionTable
import ie.setu.helpers.populateWorkoutTable
import ie.setu.helpers.workoutSession
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WorkoutSessionDAOTest {
    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    @Nested
    inner class CreateWorkoutsSession {
        @Test
        fun `multiple session added to table can be retrieved successfully`() {
            transaction {


                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutSessionDAO = populateWorkoutSessionTable()
                //Act & Assert
                TestCase.assertEquals(3, workoutSessionDAO.getAllWorkoutsSesiion().size)

            }
        }
    }
    @Nested
    inner class ReadWorkoutsSession {

        @Test
        fun `getting all workoutsSession stored in table successfully`(){
            transaction {
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutSessionDAO = populateWorkoutSessionTable()
               assertEquals(3, workoutSessionDAO.getAllWorkoutsSesiion().size)
            }



        }
        @Test
        fun `get workoutsSession by user id that has no related workoutSession, results in no record found`(){
            transaction {
                val usersDAO = populateUserTable()
                val userId=-9
                val workoutDAO = populateWorkoutTable()
                val workoutSessionDAO = populateWorkoutSessionTable()
                assertEquals(0, workoutSessionDAO.findWorkoutSessionByUserId(userId).size)
            }



        }

        @Test
        fun `get workoutSession by user id that has related workout, results in correct workout returned`() {
            transaction {
                val usersDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()

                TestCase.assertEquals(workoutSession[0], workoutDAOSession.findWorkoutSessionByUserId(1).get(0))
                TestCase.assertEquals(workoutSession[1], workoutDAOSession.findWorkoutSessionByUserId(1).get(1))



            }

        }
        @Test
        fun `get all workout over empty table returns none`() {
            transaction {


                SchemaUtils.create(WorkoutSessions)

                val workoutSessionDAO = WorkoutSessionDAO()

                //Act & Assert
                TestCase.assertEquals(0, workoutSessionDAO.getAllWorkoutsSesiion().size)
            }
        }
        @Test
        fun `get workoutSession by workoutSession id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()
                //Act & Assert
             assertEquals(null, workoutDAOSession.findByWorkoutSessionId(4))
            }
        }

    }
//
    @Nested
    inner class UpdateWorkoutSession {

        @Test
        fun `updating existing session in table results in successful update`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()

                //Act & Assert
                val session3updated = WorkoutSession(id=1,started= DateTime.now(),
                    ended= DateTime.now(), totalCalories = 400,status="pending", workoutId = 2, userId = 1)
                workoutDAOSession.updateWorkoutSessionBasedOnWorkoutSessionId(session3updated.id, session3updated)
                assertEquals(session3updated, workoutDAOSession.findByWorkoutSessionId(1))
            }
        }

        @Test
        fun `updating non-existant session in table results in no updates`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()

                //Act & Assert
                val sessionupdated = WorkoutSession(id=5,started= DateTime.now(),
                    ended= DateTime.now(), totalCalories = 400,status="completed", workoutId = 2, userId = 1)
                workoutDAOSession.updateWorkoutSessionBasedOnWorkoutSessionId(5, sessionupdated)
                assertEquals(null, workoutDAOSession.findByWorkoutSessionId(5))

            }
        }
    }
    @Nested
    inner class DeleteWorkoutsSession {

        @Test
        fun `deleting a non-existant workoutsession (by id) in table results in no deletion`() {
            transaction {

                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()

                //Act & Assert
                assertEquals(3, workoutDAOSession.getAllWorkoutsSesiion().size)
                workoutDAOSession.deleteWorkoutSessionByWorkoutSessionId(4)
                assertEquals(3, workoutDAOSession.getAllWorkoutsSesiion().size)
            }
        }

        @Test
        fun `deleting an existing session (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()

                assertEquals(3, workoutDAOSession.getAllWorkoutsSesiion().size)
                workoutDAOSession.deleteWorkoutSessionByWorkoutSessionId(workoutSession[0].id)
                assertEquals(2, workoutDAOSession.getAllWorkoutsSesiion().size)
            }
        }


        @Test
        fun `deleting session when none exist for user id results in no deletion`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()
                //Act & Assert
                assertEquals(3, workoutDAOSession.getAllWorkoutsSesiion().size)
                workoutDAOSession.deleteWorkoutSessionByUserId(10)
                assertEquals(3, workoutDAOSession.getAllWorkoutsSesiion().size)
            }
        }

        @Test
        fun `deleting session when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutDAOSession = populateWorkoutSessionTable()
                //Act & Assert
                assertEquals(3, workoutDAOSession.getAllWorkoutsSesiion().size)
                workoutDAOSession.deleteWorkoutSessionByUserId(1)
                assertEquals(2,  workoutDAOSession.getAllWorkoutsSesiion().size)
            }
        }
    }
}