package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Manages Database Transactions and returns the results of Transaction
 */
class UserDAO {

    /**
     * returns the list  of all users
     */
    fun getAll() : ArrayList<User>{
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }
    /**
     * return the details of the user by user Id
     */
    fun findById(id: Int): User?{
        return transaction {
            Users.select() {
                Users.id eq id}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }
    /**
     * Adds [user] to the user table.
     * @return the id of the user
     */

    fun save(user: User) : Int?{
        return transaction {
            Users.insert {
                it[name] = user.name
                it[email] = user.email
            } get Users.id
        }
    }
    /**
     * returns the emailid of the user
     */
    fun findByEmail(email: String) :User?{
        return transaction {
            Users.select() {
                Users.email eq email}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }
    /**
     * deletes the user from user table.
     */
    fun delete(id: Int):Int {
        return transaction{ Users.deleteWhere{
            Users.id eq id
        }
        }
    }
    /**
     * updates user details to the user table.
     */
    fun update(id: Int, user: User): Int{
        return transaction {
            Users.update ({
                Users.id eq id}) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }

}