package com.example.myapplication

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDBManager {

    private var db: AppDB? = null

    fun initDB(context: Application, uuid: String) {

        if (db == null){
            synchronized(AppDB::class.java){
                if (db == null){

                    db = Room.databaseBuilder(context.applicationContext, AppDB::class.java, "${uuid}_${context::class.java.simpleName}_app_db")
                        .allowMainThreadQueries()
                        .addMigrations(DB_MARGIN_1_to_2,DB_MARGIN_2_to_3)
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

    private val DB_MARGIN_1_to_2 = object :Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("create table real_name_infor (id BLOB primary key autoincrement not null,userId text not null,country text not null,address text not null,cardID text not null,userRealName text not null)")
        }
    }

    private val DB_MARGIN_2_to_3 = object :Migration(2,3){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("alter table kille_tom_user add column real_country text default null")
            database.execSQL("alter table kille_tom_user add column real_address text default null")
            database.execSQL("alter table kille_tom_user add column card_id text default null")
        }
    }
}