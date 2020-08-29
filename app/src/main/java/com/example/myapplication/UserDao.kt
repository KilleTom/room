package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Query("select * from kille_tom_user where real_name is not null")
    fun getRealUsers():List<UserEntity>

    @Query("select max(age) from kille_tom_user")
    fun getMaxAge():Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdate(userEntities:List<UserEntity>)


}