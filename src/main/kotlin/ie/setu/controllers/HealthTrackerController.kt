package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.plugin.openapi.annotations.*
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO
import ie.setu.utils.jsonToObject


object HealthTrackerController {

    private val userDao = UserDAO()
    private val activityDAO= ActivityDAO()
    @OpenApi(
        summary = "Get all users",
        operationId = "getAllUsers",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )
    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }

    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add User",
        operationId = "addUser",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.POST,
        responses  = [OpenApiResponse("200")]
    )
    fun addUser(ctx: Context) {
        val user : User = jsonToObject(ctx.body())
        val userId = userDao.save(user)
        if (userId != null) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
    }

    @OpenApi(
        summary = "Get user by Email",
        operationId = "getUserByEmail",
        tags = ["User"],
        path = "/api/users/email/{email}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("email", Int::class, "The user email")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteUser(ctx: Context){
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateUser(ctx: Context){
        val foundUser : User = jsonToObject(ctx.body())
        if ((userDao.update(id = ctx.pathParam("user-id").toInt(), user=foundUser)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }




    /***Activity specific code starts here****/
    @OpenApi(
        summary = "get all activities",
        operationId = "getAllActivities",
        tags = ["Activities"],
        path = "/api/activities",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        if(activityDAO.getAll().size!=0){
            val mapper = jacksonObjectMapper()
                .registerModule(JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

            ctx.json(mapper.writeValueAsString( activityDAO.getAll() ))
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }


    }

    @OpenApi(
        summary = "get activity by user ID",
        operationId = "getActivityByUserId",
        tags = ["Activities"],
        path = "/api/users/{user-id}/activities",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            print("ctx"+ctx.pathParam("user-id").toInt())
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            print("acti "+activities)
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = jacksonObjectMapper()
                    .registerModule(JodaModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                ctx.json(mapper.writeValueAsString(activities))
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary = "get activity by activity ID",
        operationId = "getActivityByActivityId",
        tags = ["Activities"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            val mapper = jacksonObjectMapper()
                .registerModule(JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            ctx.json(mapper.writeValueAsString(activity))
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary = "Add Activity",
        operationId = "addActivity",
        tags = ["Activities"],
        path = "/api/activities",
        method = HttpMethod.POST,
        responses  = [OpenApiResponse("200")]
    )
    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = jacksonObjectMapper()
            .registerModule(JodaModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        val activity = mapper.readValue<Activity>(ctx.body())
        val userId = userDao.findById(activity.userId)
        if (userId != null) {
            val activityId = activityDAO.save(activity)
            activity.id = activityId
            ctx.json(activity)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }


    }
    @OpenApi(
        summary = "Delete activity by ID",
        operationId = "deleteActivityById",
        tags = ["Activities"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        if(activityDAO.findByActivityId(ctx.pathParam("activity-id").toInt()) !=null && activityDAO.findByActivityId(ctx.pathParam("activity-id").toInt())!=null){
            activityDAO.deleteActivityByActivityId(ctx.pathParam("activity-id").toInt())
            ctx.status(204)
        }
        else{
            ctx.status(404)
        }

    }

    @OpenApi(
        summary = "Delete activity by user ID",
        operationId = "deleteActivityById",
        tags = ["Activities"],
        path = "/api/users/{user-id}/activities",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteActivityByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activitiesByUser = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activitiesByUser.isNotEmpty()) {
                activityDAO.deleteActivityByUserId(ctx.pathParam("user-id").toInt())
                ctx.status(204)
            }
            else{
                ctx.status(404)
            }
            //return saying that users got deleted
        }
        else{
            ctx.status(404)
        }

    }
    @OpenApi(
        summary="Update Activity by ID",
        operationId="updateActivityById",
        tags=["Activities"],
        path="/api/activities/{activity-id}",
        method=HttpMethod.PATCH,
        pathParams=[OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses=[OpenApiResponse("204")]
    )
    fun updateActivityById(ctx: Context) {
        val activityUpdates : Activity = jsonToObject(ctx.body())

//        val activitiesList = activityDAO.findByActivityId(ctx.pathParam("activity-id").toInt())
        if(activityDAO.findByActivityId(ctx.pathParam("activity-id").toInt())!=null){
            activityDAO.updateActivityBasedOnActivityId(ctx.pathParam("activity-id").toInt(),activityUpdates)
            ctx.status(204)
        }
        else{
            ctx.status(404)
        }

    }





}
