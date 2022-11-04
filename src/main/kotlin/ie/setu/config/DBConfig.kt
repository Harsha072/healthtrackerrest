package ie.setu.config


import org.jetbrains.exposed.sql.Database

class DbConfig{

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{
        return Database.connect(
            "jdbc:postgresql://ec2-34-194-216-153.compute-1.amazonaws.com:5432/d24r67ul0gah78?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "ledrlgqizvueup",
            password = "ff2ae6d25ea73c6ddcec5583232dd080eb3783ed97a489684e713ae456a02105")
    }


}
