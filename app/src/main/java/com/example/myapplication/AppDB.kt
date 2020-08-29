package com.example.myapplication

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        (UserEntity::class)], version = 1, exportSchema = true
)
abstract class AppDB : RoomDatabase(){

    abstract fun getUserDao(): UserDao
}