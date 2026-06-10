package com.fortune.paper.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.fortune.paper.data.local.createDataStore
import com.fortune.paper.platform.notification.LocalNotifier
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** 플랫폼 API가 필요한 의존성만 제공 (DataStore, LocalNotifier). */
val platformModule = module {
    single<DataStore<Preferences>> { createDataStore(androidContext()) }
    single { LocalNotifier(androidContext()) }
}
