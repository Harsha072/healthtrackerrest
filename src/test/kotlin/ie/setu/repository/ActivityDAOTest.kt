package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.activities
import ie.setu.helpers.populateActivityTable
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.users
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

//retrieving some test data from Fixtures
private val activity1 = activities.get(0)
private val activity2 = activities.get(1)
private val activity3 = activities.get(2)


class ActivityDAOTest {
    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }
    @Nested
    inner class CreateActivities {
        @Test
        fun `multiple activity added to table can be retrieved successfully`() {
            transaction {


                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
               assertEquals(activity1, activityDAO.findByActivityId(activity1.id))
//                assertEquals(activity2, activityDAO.findByActivityId(activity2.id))
//                assertEquals(activity3, activityDAO.findByActivityId(activity3.id))
            }
        }
    }
    @Nested
    inner class ReadActivities {

        @Test
        fun `getting all activities stored in table successfully`(){
            transaction {
                val usersDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                assertEquals(3, activityDAO.getAll().size)
            }



        }
        @Test
        fun `get activities by user id that has no related activity, results in no record found`(){
            transaction {
                val usersDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                TestCase.assertEquals(0, activityDAO.findByUserId(4).size)
            }



        }

        @Test
        fun `get activity by user id that has related activity, results in correct activity returned`() {
            transaction {
                val usersDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                assertEquals(activity1, activityDAO.findByUserId(1).get(0))
                assertEquals(activity2, activityDAO.findByUserId(1).get(1))



            }

        }
        @Test
        fun `get all activities over empty table returns none`() {
            transaction {

                //Arrange - create and setup activityDAO object
                SchemaUtils.create(Activities)
                val activityDAO = ActivityDAO()

                //Act & Assert
                assertEquals(0, activityDAO.getAll().size)
            }
        }
        @Test
        fun `get activity by activity id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()
                //Act & Assert
                assertEquals(null, activityDAO.findByActivityId(4))
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
                kotlin.test.assertEquals(null, activityDAO.findByActivityId(4))
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
            }
        }
    }
    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting a non-existant activity (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
                activityDAO.deleteActivityByActivityId(4)
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing activity (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
                activityDAO.deleteActivityByActivityId(activity3.id)
                kotlin.test.assertEquals(2, activityDAO.getAll().size)
            }
        }


        @Test
        fun `deleting activities when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
                activityDAO.deleteActivityByUserId(3)
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting activities when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                kotlin.test.assertEquals(3, activityDAO.getAll().size)
                activityDAO.deleteActivityByUserId(1)
                kotlin.test.assertEquals(1, activityDAO.getAll().size)
            }
        }
    }



}