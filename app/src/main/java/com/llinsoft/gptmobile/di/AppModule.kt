package com.llinsoft.gptmobile.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.llinsoft.gptmobile.PromptDatabase
import com.llinsoft.gptmobile.data.local.database.PromptDataSource
import com.llinsoft.gptmobile.data.local.database.PromptDataSourceImpl
import com.llinsoft.gptmobile.domain.PrepopulateDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver {
        return AndroidSqliteDriver(
            schema = PromptDatabase.Schema,
            context = app,
            name = "prompt.db"
        )
    }

    @Provides
    @Singleton
    fun providePersonDataSource(driver: SqlDriver): PromptDataSource {
        return PromptDataSourceImpl(PromptDatabase(driver))
    }

    @Provides
    @Singleton
    fun providePrepopulateDatabase(database: PromptDataSource): PrepopulateDatabase {
        return PrepopulateDatabase(database)
    }
}