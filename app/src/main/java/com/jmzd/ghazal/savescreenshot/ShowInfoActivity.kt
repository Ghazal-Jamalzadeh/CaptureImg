package com.jmzd.ghazal.savescreenshot

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.material.snackbar.Snackbar
import com.jmzd.ghazal.savescreenshot.databinding.ActivityShowInfoBinding
import com.jmzd.ghazal.savescreenshot.model.Reserve
import com.jmzd.ghazal.savescreenshot.utils.StoreReserveData
import com.jmzd.ghazal.savescreenshot.viewmodel.ReserveViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ShowInfoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowInfoBinding

    private val viewModel: ReserveViewModel by viewModels()

    //dataStore
    @Inject
    lateinit var reserveData: StoreReserveData

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityShowInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fab.setOnClickListener{
            checkPermissins()
        }

        viewModel.getMyReseveDetails()
        viewModel.reserveLiveData.observe(this){
        showInfo(it)
        }


    }

    private fun showInfo (reserve : Reserve){

        binding.apply {

            lifecycle.coroutineScope.launchWhenCreated {

                reserveData.getReserveNum().collect {

            num.txtTitle.text = "Num:"
            num.txtInfo.text = it.toString()
            firstName.txtTitle.text = "First Name:"
            firstName.txtInfo.text = reserve.firstNeme
            lastName.txtTitle.text = "Last Name:"
            lastName.txtInfo.text = reserve.lastName
            mobile.txtTitle.text = "Mobile:"
            mobile.txtInfo.text = reserve.mobile
            time.txtTitle.text = "Time:"
            time.txtInfo.text = reserve.time
                }
            }
        }

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
                                        applicationContext.packageName,
                                        null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                            } else {
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
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
//        isFromOut = true
        startActivityForResult(intent, 101)
    }

    private fun captureImage(){
        val bitmap = getBitmapFromView(binding.scrollView,
            binding.scrollView.getChildAt(0).height,
            binding.scrollView.getChildAt(0).width)

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

        val appStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Saved_images")

        if (!appStorageDir.exists()) {
            appStorageDir.mkdirs()
        }

        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val fname = "Image-$timeStamp.jpg"
        val file = File(appStorageDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

            showSnackBar("Saved to $appStorageDir")

        } catch (e: Exception) {
            e.printStackTrace()
            showSnackBar("There is a problem :(((")
        }
    }

    private fun showSnackBar(message : String){
        val rootView = findViewById<View>(android.R.id.content)
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}