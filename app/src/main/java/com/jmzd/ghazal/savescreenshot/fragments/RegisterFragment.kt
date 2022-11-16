package com.jmzd.ghazal.savescreenshot.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.jmzd.ghazal.savescreenshot.R
import com.jmzd.ghazal.savescreenshot.ShowInfoActivity
import com.jmzd.ghazal.savescreenshot.databinding.ActivityMainBinding
import com.jmzd.ghazal.savescreenshot.model.Reserve
import com.jmzd.ghazal.savescreenshot.utils.StoreReserveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val TAG = "RegisterFragment110"
    private lateinit var binding: ActivityMainBinding

    //dataStore
    @Inject
    lateinit var reserveData: StoreReserveData

    @Inject
    lateinit var reserve: Reserve

    private var num = 0
    private lateinit var job: Job
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityMainBinding.inflate(inflater, container, false)

        binding.confirmBtn.setOnClickListener {

            collectData()

        }
        return binding.root
    }

    private fun collectData() {

        binding.apply {
            if (firstName.text.toString().isEmpty()) {
                Toast.makeText(context, "First Name is empty", Toast.LENGTH_SHORT).show()
            } else if (lastName.text.toString().isEmpty()) {
                Toast.makeText(context, "Last Name is empty", Toast.LENGTH_SHORT).show()
            } else if (mobile.text.toString().isEmpty()) {
                Toast.makeText(context, "Mobile is empty", Toast.LENGTH_SHORT).show()
            } else {
                try {

                    createReserve()
                    generateNum()

                    findNavController().popBackStack()
                    findNavController().navigate(R.id.action_registerFragment_to_showReserveInfoFragment)

                } catch (e: Exception) {
                    Log.d(TAG, "${e.message}")
                }
            }
        }

    }

    private fun createReserve() {
        val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        Log.d(TAG, "createReserve: $reserve")
        reserve.firstNeme = binding.firstName.text.toString()
        reserve.lastName = binding.lastName.text.toString()
        reserve.mobile = binding.mobile.text.toString()
        reserve.time = currentTime

        lifecycle.coroutineScope.launchWhenCreated {

            reserveData.getReserveNum().collect {
                num = it
            }
        }

    }

    private fun generateNum() {

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        job = lifecycle.coroutineScope.launchWhenCreated {

            reserveData.getReserveDate().collect {

                if (it == currentDate) {
                    num++
                    reserveData.saveReserveNum(num)
                } else {
                    reserveData.saveReserveDate(currentDate)
                    reserveData.saveReserveNum(0)
                }
                job.cancel()

            }
        }


    }


}