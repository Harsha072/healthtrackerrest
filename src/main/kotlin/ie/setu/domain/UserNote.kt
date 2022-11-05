package ie.setu.domain

data class UserNote (var id: Int,
                    var title:String,
                    var text:String,
                    var shared:String,
                    var userId: Int)