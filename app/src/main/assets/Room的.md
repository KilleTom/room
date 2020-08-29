# Room的

Room做为jetpack的核心组件之一，其目的是通过对sqlLite进行封装，是的对数据库的操作变为简易的操作数据。

## Room的配置与使用

1. 首先通过以下配置语句远程依赖使用Room

~~~groovy
implementation 'androidx.room:room-runtime:2.2.4'
kapt 'androidx.room:room-compiler:2.2.5'
~~~

  在使用讲解之前，先简单的介绍以下room的一些注解

| 注解                       | 含义                                                         |
| -------------------------- | ------------------------------------------------------------ |
| Entity                     | 数据表对应的数据表名                                         |
| ColumnInfo                 | 作用于Entity，对某些字段进行命名如不使用，则Entity对象中的成员变量则会以其命名变作为数据表表中的字段名 |
| PrimaryKey                 | 主键，设置autoGenerate是否为自增                             |
| Dao                        | 数据库操作的接口                                             |
| Query                      | 作用于Dao的注解用来作为查询使用，也可以作用与条件删除数据    |
| Insert                     | 用来插入数据，                                               |
| OnConflictStrategy.REPLACE | 作用与Insert目的在于插入时候对已存在的数据进行覆盖插入       |
| Delete                     | 作用与Dao内用以删除数据                                      |
| Database                   | 数据库的注解用来创建生产该数据库内的数据表，并返回Dao对象进行操作数据库 |
| entities                   | 作用于Database注解，用来表明这个数据库内会有多少张数据表     |
|                            |                                                              |



2.构建Entity 、 Dao 、DataBase

1. entity的构建

   首先利用Entity注解表明这张数据表表名:kille_tom_user;
   其次利用@PrimaryKey(autoGenerate = false) 表明用户id为主键并且不可递增;
   
   其次利用ColumnInfo 对realName以及nickName分别进行数据字段命名。
   
   ~~~kotlin
   @Entity(tableName = "kille_tom_user")
   class UserEntity(
       
       @PrimaryKey(autoGenerate = false)
       val id: String,
       @ColumnInfo(name = "real_name")
       val realName: String?,
       @ColumnInfo(name = "nick_name")
       val nickName: String,
       val sex: String
   )
   ~~~

​       

2. dao的构建

   Dao 的使用是声明好需要注解的接口然后利用相关的注解将接口内的方法进行注解，然后room会根据其内部的规则将相应的操做转化为实际的sql操作。

   ~~~kotlin
   @Dao
   interface UserDao {
   
       @Query("select * from kille_tom_user where real_name is not null")
       fun getRealUsers():List<UserEntity>
   
       @Query("select max(age) from kille_tom_user")
       fun getMaxAge():Int?
   
       @Insert(onConflict = OnConflictStrategy.REPLACE)
       fun insertUpdate(userEntities:List<UserEntity>)
       
   
   }
   ~~~

   

3. dataBase的构建

   dataBase则需要声明一个抽象的类并且继承RoomDataBase并且利用相关的注解将实际需要的数据表与它关联起来，并且通过使用的抽象方法需要返回操作的DAO对象，其余的实际操作则会由Room在其内部实现好实际需要返回的数据库对象让开发者操作。

   ~~~kotlin
   @Database(
       entities = [
           (UserEntity::class)], version = 1, exportSchema = true
   )
   abstract class AppDB : RoomDatabase(){
   
       abstract fun getUserDao(): UserDao
   }
   ~~~

   

4. 数据库的初始化以及全局单列

   在前面的代码示例演示了如何声明这么数据库的关键部件，那么可以通过使用`Room.databaseBuilde`这个类中构建出来；

   注意操作为了避免内存泄漏最好使用Application中的context进行初始化并且全局单列避免过多的初始化或者不必要的操作；

   例如这样的实现去构建全局的单列

   ```kotlin
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
   ```



## 常用的SQL操作

在前一节中我们学习了如何构建并且初始化Room，在这一节中，我们会学习到常用的SQL操作，例如简单的增删改查、模糊搜索、min()、max()等操作。



## Room的兼容与升级

## 

