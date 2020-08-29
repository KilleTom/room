package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kille_tom_user")
class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "real_name")
    val realName: String?,
    @ColumnInfo(name = "nick_name")
    val nickName: String,
    val sex: String,
    var age:Int
)
