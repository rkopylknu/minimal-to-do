package com.rkopylknu.minimaltodo.di

import android.content.Context
import androidx.room.Room
import com.rkopylknu.minimaltodo.data.room.AppDatabase
import com.rkopylknu.minimaltodo.data.room.ToDoItemDao
import com.rkopylknu.minimaltodo.util.APP_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            APP_DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideToDoItemDao(appDatabase: AppDatabase): ToDoItemDao {
        return appDatabase.toDoItemDao
    }
}