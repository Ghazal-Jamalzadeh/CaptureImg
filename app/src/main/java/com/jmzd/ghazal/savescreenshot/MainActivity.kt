package com.jmzd.ghazal.savescreenshot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.jmzd.ghazal.savescreenshot.databinding.ActivityMainBinding
import com.jmzd.ghazal.savescreenshot.model.Reserve
import com.jmzd.ghazal.savescreenshot.utils.StoreReserveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    //dataStore
    @Inject
    lateinit var reserveData: StoreReserveData

    @Inject
    lateinit var reserve: Reserve

    private var num = 0
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmBtn.setOnClickListener {

            collectData()

        }

    }

    private fun collectData() {

        if (binding.firstName.text.toString().isEmpty()) {
            Toast.makeText(this, "First Name is empty", Toast.LENGTH_SHORT).show()
        } else if (binding.lastName.text.toString().isEmpty()) {
            Toast.makeText(this, "Last Name is empty", Toast.LENGTH_SHORT).show()
        } else if (binding.mobile.text.toString().isEmpty()) {
            Toast.makeText(this, "Mobile is empty", Toast.LENGTH_SHORT).show()
        } else {


            val currentTime: String =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())


            reserve.firstNeme = binding.firstName.text.toString()
            reserve.lastName = binding.lastName.text.toString()
            reserve.mobile = binding.mobile.text.toString()
            reserve.time = currentTime


            lifecycle.coroutineScope.launchWhenCreated {

                reserveData.getReserveNum().collect {
                    num = it
                }
            }
            generateNum()

            val intent = Intent(this, ShowInfoActivity::class.java)
            startActivity(intent)
            finish()

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

