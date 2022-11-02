package ie.setu.config


import org.jetbrains.exposed.sql.Database

class DbConfig{

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{
        return Database.connect(

            "jdbc:postgresql://ec2-3-220-207-90.compute-1.amazonaws.com:5432/dn6majjot7p7m?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "szvrzwfdhlksze",
            password = "1c926deeea3b6ef8aebe860126bfbec70e9f2bae1290abcf0f8844e897737c5a")
    }

}
