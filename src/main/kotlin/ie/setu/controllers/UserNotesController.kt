package ie.setu.controllers


import ie.setu.domain.UserNote
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.repository.UserNotesDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object UserNotesController {
    val userDao = UserDAO()
    val userNotesDAO = UserNotesDAO()

    @OpenApi(
        summary = "get all notes",
        operationId = "getAllNotes",
        tags = ["Notes"],
        path = "/api/notes",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<UserNote>::class)])]
    )
    fun getAllNotes(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        if (userNotesDAO.getAll().size != 0) {
            ctx.json(userNotesDAO.getAll())
            ctx.status(200)
        } else {
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Note",
        operationId = "addNote",
        tags = ["Notes"],
        path = "/api/notes",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200")]
    )
    fun addNote(ctx: Context) {
        val userNote: UserNote = jsonToObject(ctx.body())

        val userId = userDao.findById(userNote.userId)
        if (userId != null) {
            val noteId = userNotesDAO.saveNote(userNote)
            if (noteId != null) {
                userNote.id = noteId
                ctx.json(userNote)
                ctx.status(201)
            }
            else{
                ctx.status(404)
            }

        } else {
            ctx.status(404)
        }


    }

    @OpenApi(
        summary = "Get note by title",
        operationId = "getNotesByTitle",
        tags = ["Notes"],
        path = "/api/notes/{title}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("title", String::class, "The notes title")],
        responses = [OpenApiResponse("200", [OpenApiContent(UserNote::class)])]
    )
    fun getNotesByTitle(ctx: Context) {
        print("title being called")
        val userNote = userNotesDAO.findByTitle(ctx.pathParam("title"))
        if (userNote != null) {
            ctx.json(userNote)
            ctx.status(200)
        } else {
            ctx.status(404)
        }
    }
    @OpenApi(
        summary = "Get note by user",
        operationId = "getNotesByUserId",
        tags = ["Notes"],
        path = "/api/users/{user-id}/notes",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", String::class, "The user id")],
        responses = [OpenApiResponse("200", [OpenApiContent(UserNote::class)])]
    )
    fun getNotesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            print("user id "+ctx.pathParam("user-id").toInt())
            val userNote = userNotesDAO.findNoteByUserId(ctx.pathParam("user-id").toInt())
            if (userNote.isNotEmpty())
            {   print("im stil here"+userNote)
                ctx.json(userNote)
                ctx.status(200)
            } else {
                print("no note")
                ctx.status(404)
            }
        }
        else{
            print("no user")
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "get note by note ID",
        operationId = "getUserNoteByNoteId",
        tags = ["Notes"],
        path = "/api/notes/{notes-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("notes-id", Int::class, "The note ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun getUserNoteByNoteId(ctx: Context) {
        print("being called getUserNoteByNoteId:::::")
        val userNote = userNotesDAO.findNoteById(ctx.pathParam("notes-id").toInt())
        if (userNote != null){
            ctx.json(userNote)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }
    @OpenApi(
        summary = "Update note by ID",
        operationId = "updateUserById",
        tags = ["Notes"],
        path = "/api/notes/{notes-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("notes-id", Int::class, "The notes ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateUserNote(ctx: Context) {
        print("helloo\n")
        val userNote: UserNote = jsonToObject(ctx.body())
        print("path param"+ctx.pathParam("notes-id").toInt()+"\n")
        print("user NOtw id"+userNote.id+"\n")
        if(userNotesDAO.findNoteById(ctx.pathParam("notes-id").toInt()) != null){
            print("note exsists so update")
            userNotesDAO.updateNote(ctx.pathParam("notes-id").toInt(),userNote)
            ctx.status(204)
        }
        else{
            print("does not exixst")
            ctx.status(404)
        }
    }


    @OpenApi(
        summary = "Delete note by ID",
        operationId = "deleteNote",
        tags = ["Notes"],
        path = "/api/notes/{note-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("notes-id", Int::class, "The note ID")],
        responses = [OpenApiResponse("204")]
    )
    fun deleteNoteById(ctx: Context) {
        print("delete notes by  id \n")
        if (userNotesDAO.deleteNoteById(ctx.pathParam("notes-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete note by user ID",
        operationId = "deleteNote",
        tags = ["Notes"],
        path = "/api/users/{user-id}/notes",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses = [OpenApiResponse("204")]
    )
    fun deleteNoteByUserId(ctx: Context) {
        print("delete notes by user id \n")
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            if (userNotesDAO.deleteNoteByUserId(ctx.pathParam("user-id").toInt()) != 0)
                ctx.status(204)
            else
                ctx.status(404)
        }
        else{
            ctx.status(404)
        }
    }




}