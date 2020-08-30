# 作用于Dao的注解用来作为Room的

Room做为jetpack的核心组件之一，其目的是通过对sqlLite进行封装，目的在于更为便捷的操作数据库。

## Room的配置与使用

1. 首先通过以下配置语句远程依赖使用Room

~~~groovy
implementation 'androidx.room:room-runtime:2.2.4'
kapt 'androidx.room:room-compiler:2.2.5'
~~~

  在使用讲解之前，先简单的介绍以下room的一些注解

| 注解               | 含义                                                         |
| ------------------ | ------------------------------------------------------------ |
| Entity             | 数据表名                                                     |
| ColumnInfo         | 作用于Entity，对某些字段进行命名,如不使用：则Entity对象中的成员变量则会以其命名变作为数据表表中的字段名 |
| PrimaryKey         | 主键，设置autoGenerate是否为自增                             |
| Dao                | 数据库操作接口                                               |
| Query              | 作用于Dao内可用于查询，也可用户条件删除                      |
| Insert             | 作用于Dao内用于插入数据                                      |
| OnConflictStrategy | Insert的配置选项                                             |
| Delete             | 作用于Dao内用于删除数据                                      |
| Database           | 数据库的注解，用以表明数据库，并且返回业务所需的Dao对象      |
| entities           | 作用于Database注解，表示业务所需多少张数据表。               |
|                    |                                                              |



2.构建Entity 、 Dao 、DataBase

1. entity的构建

   首先利用Entity注解表明示例数据表表名为：kille_tom_user;
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

### 查询操作

在sql中我们常常需要做一些查询操作，例如查询某一列、查求指定的最小值、最大值，或者模糊搜寻匹配等。

如下代码示例针对上一节中的UserEnitity进行演示讲解：

1. 指定条件的查询
   在UserEntity中，当realName不为空时则代表用户已经实名了，那么如何从user表中检索出已经实名的用户呢可以通过以下方式去实现：

   ~~~kotlin
   @Query("select * from kille_tom_user where real_name is not null")
   fun getRealUsers():List<UserEntity>
   ~~~
   
2. 模糊搜索查询

   在UserEntity中，birthday代表着用户设置的生日，那么我们如何实现查询指定年份出生的用户呢，例如1993年出生的用户,可以使用`like`左匹配去建立查询

   `注意在Room中使用like进行匹配搜索 % 符号是不能写在sql语句中，要通过这样的方式去实现`

   `注意在Room中传递的参数必须用作sql语句中的调用否则编译时期就会报错，如何引用通过利用 : 符号与参数进行调用例如如下示例`
   
   ```kotlin
     @Query("select * from kille_tom_user where birthday like :year")
     fun getBirthYearUser(year:String):List<UserEntity>
   
     AppDBManager.getDB().getUserDao().getBirthYearUser("1993%")
   ```
   
3. 组合条件查询
   
   针对1、2示例我们可以实现类似的一个需求，查询已经实名的用户并且是指定年份出生的用户，例如1993年出生的用户并且已实名。
   
   ~~~kotlin
   @Query("select * from kille_tom_user where real_name is not null and birthday like :year")
   fun getRealInBirthYearUsers(year:String):List<UserEntity>
   ~~~
  
4. 求某列的最大值以及最少值
   
   在在UserEntity中，age代表着用户设置的年龄，那么我们要求出年龄的最大值或者最小值则需要使用 max 以及 min 函数去求。例如这样
   
   ```kotlin
       @Query("select max(age) from kille_tom_user")
       fun getMaxAge():Int?
   
       @Query("select min(age) from kille_tom_user")
       fun getMinAge():Int?
   ```
   
   
### 插入操作

在插入数据时，往往会存在一些已有的数据，当需要覆盖插入的时候则需要利用`OnConflictStrategy.REPLACE`这一配置，默认配置为`OnConflictStrategy.ABORT`

一般在客户端中对数据进行缓存保存时，往往服务器的数据都是唯一最新的时候一般都会这样使用，例如统一更新用户信息并且替换已有用户信息来保证数据的唯一性例如

```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insertUpdate(userEntities:List<UserEntity>)
```



## Room的兼容与升级

在Room中，数据库也是存在版本号的，目的在于区分版本数据，并且将低版本的转向为高版本，但是需要开发者去配置数据库进行兼容升级。

还记得我们的`AppDB`吗，不记得没关系，我们再次温故一下：

```kotlin
@Database(
    entities = [
        (UserEntity::class)], version = 1, exportSchema = true
)
abstract class AppDB : RoomDatabase(){

    abstract fun getUserDao(): UserDao
}
```

在上述代码中，entities代表这这个数据库中有多少张数据表，version顾名思义就是数据库的版本号。

还记得我们得UserEntity吗？在UserEntity中我们有realName代表者用户是否实名了，那么相应的用户实名信息，我们应该如何存储？有两种方式：

1. 新增一张实名数据表，以及用户数据表新增一个reaIId，将实名数据表的主键与用户表中realId进行关联。
2. 直接在用户数据表中扩展数据字段存储响应数据。

