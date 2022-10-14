package ie.setu.domain.repository

import ie.setu.domain.User
import java.util.regex.Matcher
import java.util.regex.Pattern


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
        println("the user"+user)
        users.add(user)
        print("added user "+users);
    }
    fun findByEmail(email: String): User? {
        return users.find { it.email == email }
    }
    fun delete(id: Int){
        val user = users.find {it.id==id};
        if(user!==null){
            users.remove(user);
            println("deleted user "+user)
        }
        else print("user not found") ;
    }
    fun update(id: Int, newuser: User){
        var user = users.find {it.id==id};
        if(user!==null){
            users.remove(user);
            users.add(newuser)
            print("added new user"+users)
        }
        else print("user not found") ;
    }

}