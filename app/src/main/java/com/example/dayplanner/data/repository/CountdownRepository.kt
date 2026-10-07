package com.example.dayplanner.data.repository

import com.example.dayplanner.data.local.CountdownDao
import com.example.dayplanner.data.model.CountdownEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountdownRepository @Inject constructor(
    private val dao: CountdownDao
) {
    fun getByCategory(category: String): Flow<List<CountdownEvent>> =
        dao.observeByCategory(category)

    suspend fun add(event: CountdownEvent) = dao.insert(event)

    suspend fun remove(event: CountdownEvent) = dao.delete(event)
}
