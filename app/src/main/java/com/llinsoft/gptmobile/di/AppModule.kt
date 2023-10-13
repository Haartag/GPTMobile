package com.llinsoft.gptmobile.di

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.llinsoft.gptmobile.PromptDatabase
import com.llinsoft.gptmobile.data.local.database.PromptDataSource
import com.llinsoft.gptmobile.data.local.database.PromptDataSourceImpl
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesHelper
import com.llinsoft.gptmobile.data.local.datastore.PreferencesDataStoreHelper
import com.llinsoft.gptmobile.domain.OpenAiManager
import com.llinsoft.gptmobile.domain.PrepopulateDatabase
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

    @Provides
    @Singleton
    fun providePreferencesDataStoreHelper(@ApplicationContext appContext: Context): PreferencesDataStoreHelper {
        return PreferencesDataStoreHelper(appContext)
    }

    @Provides
    @Singleton
    fun provideEncryptedPreferencesHelper(@ApplicationContext appContext: Context): EncryptedPreferencesHelper {
        return EncryptedPreferencesHelper(appContext)
    }

    @Provides
    @Singleton
    fun provideOpenAiManager(encryptedPreferencesHelper: EncryptedPreferencesHelper): OpenAiManager {
        return OpenAiManager(encryptedPreferencesHelper)
    }

}