package com.example.fakeinstagram.navigation.model

data class PushDTO(
    //puxh받는사람 토큰
    var to : String? = null,
    var notification : Notification = Notification()
){
    data class Notification(
        //push 내용
        var body : String? = null,
        //push 제목
        var title : String? = null
    )
}