package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Workouts
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.WorkoutDAO
import ie.setu.helpers.*
import junit.framework.TestCase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val workout1 = workout.get(0)
private val workout2 = workout.get(1)
private val workout3 = workout.get(2)
class WorkoutDAOTest {
    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    @Nested
    inner class CreateWorkouts {
        @Test
        fun `multiple activity added to table can be retrieved successfully`() {
            transaction {


                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                //Act & Assert
                TestCase.assertEquals(3, workoutDAO.getAllWorkouts().size)
                TestCase.assertEquals(workout1, workoutDAO.findByWorkoutId(workout1.id))

            }
        }
    }
    @Nested
    inner class ReadWorkouts {

        @Test
        fun `getting all workouts stored in table successfully`(){
            transaction {
                val usersDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                TestCase.assertEquals(3, activityDAO.getAll().size)
            }



        }
        @Test
        fun `get workouts by user id that has no related workouts, results in no record found`(){
            transaction {
                val usersDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                TestCase.assertEquals(0, activityDAO.findByUserId(4).size)
            }



        }

        @Test
        fun `get workout by user id that has related workout, results in correct workout returned`() {
            transaction {
                val usersDAO = populateUserTable()
               val workoutDAO = populateWorkoutTable()

               TestCase.assertEquals(workout1, workoutDAO.findWorkoutByUserId(1).get(0))
               TestCase.assertEquals(workout3, workoutDAO.findWorkoutByUserId(1).get(1))



            }

        }
        @Test
        fun `get all workout over empty table returns none`() {
            transaction {

                //Arrange - create and setup activityDAO object
                SchemaUtils.create(Workouts)
                val workoutDAO = WorkoutDAO()

                //Act & Assert
                TestCase.assertEquals(0, workoutDAO.getAllWorkouts().size)
            }
        }
        @Test
        fun `get workout by workout id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                //Act & Assert
                TestCase.assertEquals(null, workoutDAO.findByWorkoutId(4))
            }
        }

    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating existing activity in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                val activity3updated = Activity(id = 3, description = "Cardio", duration = 42.0,
                    calories = 220, started = DateTime.now(), userId = 2)
                activityDAO.updateActivityBasedOnActivityId(activity3updated.id, activity3updated)
                assertEquals(activity3updated, activityDAO.findByActivityId(3))
            }
        }

        @Test
        fun `updating non-existant activity in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                val activity4updated = Activity(id = 4, description = "Cardio", duration = 42.0,
                    calories = 220, started = DateTime.now(), userId = 2)
                activityDAO.updateActivityBasedOnActivityId(4, activity4updated)
                assertEquals(null, activityDAO.findByActivityId(4))
                assertEquals(3, activityDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteWorkouts {

        @Test
        fun `deleting a non-existant workout (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()

                //Act & Assert
                assertEquals(3, workoutDAO.getAllWorkouts().size)
                workoutDAO.deleteWorkoutByWorkoutId(4)
                assertEquals(3, workoutDAO.getAllWorkouts().size)
            }
        }

        @Test
        fun `deleting an existing activity (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
//                assertEquals(3, activityDAO.getAll().size)
//                activityDAO.deleteActivityByActivityId(activity3.id)
//                assertEquals(2, activityDAO.getAll().size)
            }
        }


        @Test
        fun `deleting workouts when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()

                //Act & Assert
                assertEquals(3, workoutDAO.getAllWorkouts().size)
                workoutDAO.deleteWorkoutByUserId(3)
                assertEquals(3, workoutDAO.getAllWorkouts().size)
            }
        }

        @Test
        fun `deleting workout when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()

                //Act & Assert
                assertEquals(3, workoutDAO.getAllWorkouts().size)
                workoutDAO.deleteWorkoutByUserId(1)
                assertEquals(1, workoutDAO.getAllWorkouts().size)
            }
        }
    }

}