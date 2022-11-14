package com.jmzd.ghazal.savescreenshot

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jmzd.ghazal.savescreenshot.databinding.ActivityMainBinding
import com.jmzd.ghazal.savescreenshot.utils.MyJobScheduler
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    val JOB_ID = 1001;
    val REFRESH_INTERVAL  : Long = 15 * 60 * 1000 // 15 minutes



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener{

            checkPermissins()

        }

        scheduleJob()

    }


    private fun checkPermissins(){
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                if (Environment.isExternalStorageManager()) {
                        captureImage()
                                } else {
                                    val intent = Intent()
                                    intent.action =
                                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                                    val uri = Uri.fromParts("package",
                                        getApplicationContext().getPackageName(),
                                        null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            } else {
                                Log.d(TAG, "onPermissionsChecked: finne ")
                                captureImage()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken,
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_permission_title) )
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", getPackageName(), null)
        intent.data = uri
//        isFromOut = true
        startActivityForResult(intent, 101)
    }

    private fun captureImage(){
        val bitmap = getBitmapFromView(binding.scrollView,
            binding.scrollView.getChildAt(0).getHeight(),
            binding.scrollView.getChildAt(0).getWidth())

        storeImage(bitmap!!)
    }

    //create bitmap from the ScrollView
    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun storeImage(finalBitmap: Bitmap) {

        val appStorageDir = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "Saved_images")

        if (!appStorageDir.exists()) {
            appStorageDir.mkdirs()
            Log.d(TAG, "directory created")
        }else{
            Log.d(TAG, "already exist: ")
        }

        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val fname = "Image-$timeStamp.jpg"
        val file = File(appStorageDir, fname)
        Log.d(TAG, "saveImage: file -> $file")
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Log.d(TAG, "saveImage: ")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "saveImage: err ${e.message}")
        }
    }

    private fun scheduleJob() {
        val jobScheduler = applicationContext
            .getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(this, MyJobScheduler::class.java)
        val jobInfo = JobInfo.Builder(JOB_ID, componentName)
//            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPeriodic(REFRESH_INTERVAL)
            .setPersisted(true)
            .build()
        jobScheduler.schedule(jobInfo)
    }

    }

