package ie.setu

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig


fun main(){
println("starting:::::::::")
        DbConfig().getDbConnection()
//
        JavalinConfig().startJavalinService()


    }
