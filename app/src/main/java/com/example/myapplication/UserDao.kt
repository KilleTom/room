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

    @Query("select min(age) from kille_tom_user")
    fun getMinAge():Int?

    @Query("select * from kille_tom_user where birthday like :year")
    fun getBirthYearUser(year:String):List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpdate(userEntities:List<UserEntity>)


    @Query("select * from kille_tom_user where real_name is not null and birthday like :year")
    fun getRealInBirthYearUsers(year:String):List<UserEntity>
}