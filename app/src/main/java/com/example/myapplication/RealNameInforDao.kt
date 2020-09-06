package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RealNameInforDao {

    @Query("select * from real_name_infor ")
    fun getAllRealData():List<RealNameInforEntity>

    @Query("select * from real_name_infor where userId=:id")
    fun getRealDataForUserId(id:String):RealNameInforEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdate(entities:List<RealNameInforEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdate(entity: RealNameInforEntity)

}