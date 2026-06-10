package com.fortune.paper.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

const val DATA_STORE_FILE_NAME = "fortune_paper.preferences_pb"

/**
 * 플랫폼별 createDataStore(...)가 파일 경로만 공급하고 생성 로직은 여기로 모은다.
 * (androidMain: Context.filesDir / iosMain: NSDocumentDirectory)
 */
fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
