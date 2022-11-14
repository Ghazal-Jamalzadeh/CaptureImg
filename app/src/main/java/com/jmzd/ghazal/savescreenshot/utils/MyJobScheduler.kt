package com.jmzd.ghazal.savescreenshot.utils

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

/**
 * Created by Ghazal on 11/14/2022.
 */
@SuppressLint("SpecifyJobSchedulerIdRange")
class MyJobScheduler : JobService(){

    private val TAG = "MyJobScheduler"

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob:------------------> started ")
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob: --------------------> sstoped")
        return false
    }
}