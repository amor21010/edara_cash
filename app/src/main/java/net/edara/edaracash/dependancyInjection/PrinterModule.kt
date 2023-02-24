package net.edara.edaracash.dependancyInjection

import android.content.Context
import net.edara.sunmiprinterutill.PrinterUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PrinterModule {
    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): PrinterUtil {

        return PrinterUtil(appContext)
    }
}