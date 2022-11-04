package ie.setu.repository


import ie.setu.domain.Workout

import ie.setu.domain.db.Workouts

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
        fun `multiple sessions added to table can be retrieved successfully`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                val workoutSessionDAO = populateWorkoutSessionTable()
                //Act & Assert
              assertEquals(3, workoutSessionDAO.getAllWorkoutsSesiion().size)

            }
        }
    }
    @Nested
    inner class ReadWorkouts {

        @Test
        fun `getting all workouts stored in table successfully`(){
            transaction {
                val usersDAO = populateUserTable()
                val workoutsDAO = populateWorkoutTable()
                TestCase.assertEquals(3, workoutsDAO.getAllWorkouts().size)
            }



        }
        @Test
        fun `get workouts by user id that has no related workouts, results in no record found`(){
            transaction {
                val usersDAO = populateUserTable()
                val workoutsDAO = populateWorkoutTable()
                TestCase.assertEquals(0, workoutsDAO.findWorkoutByUserId(4).size)
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


                SchemaUtils.create(Workouts)
                val workoutDAO = WorkoutDAO()

                //Act & Assert
                TestCase.assertEquals(0, workoutDAO.getAllWorkouts().size)
            }
        }
        @Test
        fun `get workout by workout id that has no records, results in no record returned`() {
            transaction {

                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                //Act & Assert
                TestCase.assertEquals(null, workoutDAO.findByWorkoutId(4))
            }
        }

    }

    @Nested
    inner class UpdateWorkouts {

        @Test
        fun `updating existing workout in table results in successful update`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutsDAO = populateWorkoutTable()

                //Act & Assert
                val workoutupdated =  Workout(id=1,name = "asthetics", description = "fitness training", duration = 7.5, userId = 1,mincalories=200)
                workoutsDAO.updateWorkoutBasedOnWorkoutId(workoutupdated.id, workoutupdated)
                assertEquals(workoutupdated, workoutsDAO.findByWorkoutId(1))
            }
        }

        @Test
        fun `updating non-existant workout in table results in no updates`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutsDAO = populateWorkoutTable()

                //Act & Assert
                val workoutupdated =  Workout(id=6,name = "cardio", description = "Light weight training", duration = 7.5, userId = 1,mincalories=200)
                workoutsDAO.updateWorkoutBasedOnWorkoutId(4, workoutupdated)
                assertEquals(null, workoutsDAO.findByWorkoutId(4))

            }
        }
    }
    @Nested
    inner class DeleteWorkouts {

        @Test
        fun `deleting a non-existant workout (by id) in table results in no deletion`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()

                //Act & Assert
                assertEquals(3, workoutDAO.getAllWorkouts().size)
                workoutDAO.deleteWorkoutByWorkoutId(4)
                assertEquals(3, workoutDAO.getAllWorkouts().size)
            }
        }

        @Test
        fun `deleting an existing workout (by id) in table results in record being deleted`() {
            transaction {


                val userDAO = populateUserTable()
                val workoutDAO = populateWorkoutTable()
                //Act & Assert
                assertEquals(3, workoutDAO.getAllWorkouts().size)
                workoutDAO.deleteWorkoutByWorkoutId(workout3.id)
                assertEquals(2, workoutDAO.getAllWorkouts().size)
            }
        }


        @Test
        fun `deleting workouts when none exist for user id results in no deletion`() {
            transaction {


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