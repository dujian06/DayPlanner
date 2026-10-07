package com.example.dayplanner.di

import android.content.Context
import com.example.dayplanner.data.local.AppDatabase
import com.example.dayplanner.data.local.CountdownDao
import com.example.dayplanner.data.local.ScheduleDao
import com.example.dayplanner.data.repository.CountdownRepository
import com.example.dayplanner.data.repository.HolidayRepository
import com.example.dayplanner.data.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.get(context)

    @Provides
    @Singleton
    fun provideCountdownDao(db: AppDatabase): CountdownDao = db.countdownDao()

    @Provides
    @Singleton
    fun provideScheduleDao(db: AppDatabase): ScheduleDao = db.scheduleDao()

    @Provides
    @Singleton
    fun provideCountdownRepository(dao: CountdownDao): CountdownRepository =
        CountdownRepository(dao)

    @Provides
    @Singleton
    fun provideScheduleRepository(dao: ScheduleDao): ScheduleRepository =
        ScheduleRepository(dao)

    @Provides
    @Singleton
    fun provideHolidayRepository(@ApplicationContext context: Context): HolidayRepository =
        HolidayRepository(context)
}
