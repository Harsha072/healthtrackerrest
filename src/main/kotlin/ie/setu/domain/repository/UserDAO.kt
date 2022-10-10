package ie.setu.domain.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User


class UserDAO {

    private val users = arrayListOf<User>(
        User(name = "harsh", email = "harsha@gmail.com", id = 0),
        User(name = "Ben", email = "ben@gamil.ie", id = 1),
        User(name = "anitha", email = "ani@google.com", id = 2),
        User(name = "anil", email = "anil@singer.com", id = 3)
    )

    fun getAll() : ArrayList<User>{
        return users
    }
    fun findById(id: Int): User?{
        return users.find {it.id == id}
    }
    fun save(user: User){
        users.add(user)
    }

}