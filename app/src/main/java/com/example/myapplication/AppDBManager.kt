package com.example.myapplication

import android.app.Application
import android.content.Context
import androidx.room.Room

object AppDBManager {

    private var db: AppDB? = null

    fun initDB(context: Application, uuid: String) {

        if (db == null){
            synchronized(AppDB::class.java){
                if (db == null){

                    db = Room.databaseBuilder(context.applicationContext, AppDB::class.java, "${uuid}_${context::class.java.simpleName}_app_db")
                        .allowMainThreadQueries()
                        .build()
                }
            }
        }


    }

    @Throws
    fun getDB(): AppDB {
        return db ?: throw RuntimeException("LandRomDB Null")
    }

    fun clear() {
        db = null
    }
}