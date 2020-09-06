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
    var birthday:String?,
    var age:Int,
    @ColumnInfo(name = "real_country")
    val realCountry: String?,
    @ColumnInfo(name = "real_address")
    val realAddress: String?,
    @ColumnInfo(name = "card_id")
    val cardID: String?
)
