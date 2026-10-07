package com.example.dayplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dayplanner.data.model.CountdownEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CountdownDao {

    @Query("SELECT * FROM countdown_events ORDER BY targetDate ASC")
    fun observeAll(): Flow<List<CountdownEvent>>

    @Query(
        """SELECT * FROM countdown_events
           WHERE :category = 'all' OR category = :category
           ORDER BY targetDate ASC"""
    )
    fun observeByCategory(category: String): Flow<List<CountdownEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CountdownEvent)

    @Delete
    suspend fun delete(event: CountdownEvent)
}
