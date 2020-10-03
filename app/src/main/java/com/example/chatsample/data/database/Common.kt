package com.example.chatsample.data.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "users")
data class DbUserItem(
    @PrimaryKey
    @ColumnInfo(name = "f_user_id")
    val userId: Int,

    @ColumnInfo(name = "f_user_name")
    val userName: String,
)

@Dao
interface CommonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: DbUserItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(user: List<DbUserItem>)

    @Query("SELECT * FROM users WHERE :userId == f_user_id")
    suspend fun getUser(userId: Int): DbUserItem?
}
