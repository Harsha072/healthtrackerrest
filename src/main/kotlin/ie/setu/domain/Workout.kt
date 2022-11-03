package ie.setu.domain


data class Workout (var id: Int,
                    var name:String,
                     var description:String,
                     var duration: Double,
                     var userId: Int)