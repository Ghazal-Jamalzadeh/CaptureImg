package com.jmzd.ghazal.savescreenshot.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Ghazal on 11/15/2022.
 */
class StoreReserveData @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            Constants.RESERVE_INFO_DATASTORE)

        val reserveNum = intPreferencesKey(Constants.RESERVE_NUM)
        val reserveData = stringPreferencesKey(Constants.RESERVE_DATE)
    }

    suspend fun saveReserveNum(num: Int) {
        context.dataStore.edit {
            it[reserveNum] = num
        }
    }

    suspend fun saveReserveDate(date: String) {
        context.dataStore.edit {
            it[reserveData] = date
        }
    }

    fun getReserveNum() = context.dataStore.data.map {
        it[reserveNum] ?: 0
    }

    fun getReserveDate() = context.dataStore.data.map {
        it[reserveData] ?: ""
    }

}