package ie.setu.domain

import org.joda.time.DateTime

data class WorkoutSession (var id: Int,
                           var started:DateTime,
                           var ended:DateTime,
                           var  totalCalories:Int,
                           var status:String,
                           var workoutId: Int,
                           var userId: Int)
