package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_name_infor")
class RealNameInforEntity(

    @PrimaryKey
    var id: String = "",

    val userId: String,

    val country: String,

    val address: String,

    val cardID: String,

    val userRealName:String
)