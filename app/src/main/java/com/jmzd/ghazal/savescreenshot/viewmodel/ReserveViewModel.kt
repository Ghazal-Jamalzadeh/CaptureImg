package com.jmzd.ghazal.savescreenshot.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jmzd.ghazal.savescreenshot.model.Reserve
import com.jmzd.ghazal.savescreenshot.repository.ReserveRepository
import com.jmzd.ghazal.savescreenshot.utils.StoreReserveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Ghazal on 11/14/2022.
 */
@HiltViewModel
class ReserveViewModel @Inject constructor(private val repository: ReserveRepository) : ViewModel() {

    val reserveLiveData = MutableLiveData<Reserve>()

    fun getMyReseveDetails() {
        //dataStore
        reserveLiveData.postValue(repository.getMyReserve())
    }

}