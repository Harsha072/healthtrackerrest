package ie.setu.config


import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database

class DbConfig{

    fun getDbConnection() :Database{

        val logger = KotlinLogging.logger {}
        logger.info{"Starting DB Connection..."}

        val PGUSER = "merkpzxn"
        val PGPASSWORD = "eAi-Yz_XxA4fjHKWE8d5DwTOnJfmSfEL"
        val PGHOST = "lucky.db.elephantsql.com"
        val PGPORT = "5432"
        val PGDATABASE = "merkpzxn"

        //url format should be jdbc:postgresql://host:port/database
        val url = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"

        val dbConfig = Database.connect(url,
            driver="org.postgresql.Driver",
            user = PGUSER,
            password = PGPASSWORD
        )
        logger.info{"db url - connection: " + dbConfig.url}

        return dbConfig
    }
}
