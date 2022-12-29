package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.UserNote
import ie.setu.domain.db.UserNotes
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import ie.setu.utils.mapToUserNote
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserNotesDAO {

    fun getAll() : ArrayList<UserNote>{
        val userNoteList: ArrayList<UserNote> = arrayListOf()
        transaction {
            UserNotes.selectAll().map {
                userNoteList.add(mapToUserNote(it)) }
        }
        return userNoteList
    }
    fun findByTitle(title: String): UserNote?{
        return transaction {
            UserNotes.select() {
                UserNotes.title eq title}
                .map{ mapToUserNote(it) }
                .firstOrNull()
        }
    }
    fun findNoteByUserId(userId: Int): List<UserNote>{
        return transaction {
            UserNotes.select() {
                UserNotes.userId eq userId}
                .map{ mapToUserNote(it) }

        }
    }
    fun findNoteById(id: Int): UserNote?{
        return transaction {
            UserNotes.select() {
                UserNotes.id eq id}
                .map{ mapToUserNote(it) }
                .firstOrNull()

        }
    }
    fun saveNote(userNote: UserNote) : Int?{
        return transaction {
            UserNotes.insert {
                it[title] = userNote.title
                it[noteText] = userNote.noteText
                it[shared]=userNote.shared
                it[userId]=userNote.userId
            } get UserNotes.id
        }
    }

    fun updateNote(id: Int, userNote: UserNote): Int {
        return transaction {
            UserNotes.update({
                UserNotes.id eq id
            }) {
                it[title] = userNote.title
                it[noteText] = userNote.noteText
                it[shared]=userNote.shared
                it[userId]=userNote.userId
            }
        }
    }
    fun deleteNoteById(id: Int):Int {
        return transaction{ UserNotes.deleteWhere{
            UserNotes.id eq id
        }
        }
    }
    fun deleteNoteByUserId(userid: Int):Int {
        return transaction{ UserNotes.deleteWhere{
            UserNotes.userId eq userid
        }
        }
    }
}