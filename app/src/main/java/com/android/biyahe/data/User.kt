package com.android.biyahe.data

data class User (
    var id : String = "",
    var username : String = "",
    var password : String = "",
    var bookmarkList: MutableList<String>
)