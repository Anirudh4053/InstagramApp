package com.app.instagramapp.model

data class Post(
    var id: Int = 0,
    var name: String = "",
    var desc: String = "",
    var imagepath: String = "",
    var type: Int = 0,
    var datetime: String = ""
)

//@Parcelize
//data class Banner(var title:String, var image:Int = R.mipmap.ic_slider, var flag:Int = 0):
//    Parcelable