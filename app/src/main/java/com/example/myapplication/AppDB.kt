package com.example.myapplication

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        (UserEntity::class),(RealNameInforEntity::class)], version = 3, exportSchema = true
)
abstract class AppDB : RoomDatabase(){

    abstract fun getUserDao(): UserDao

    abstract fun getRealNameDao():RealNameInforDao
}