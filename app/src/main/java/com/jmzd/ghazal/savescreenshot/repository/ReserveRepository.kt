package com.jmzd.ghazal.savescreenshot.repository

import com.jmzd.ghazal.savescreenshot.model.Reserve
import javax.inject.Inject

/**
 * Created by Ghazal on 11/14/2022.
 */
class ReserveRepository @Inject constructor(private val reserve: Reserve) {

    fun getMyReserve() = reserve


}