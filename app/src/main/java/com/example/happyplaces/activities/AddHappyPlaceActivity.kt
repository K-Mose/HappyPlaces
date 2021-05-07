package com.example.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.happyplaces.R
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private var cal = Calendar.getInstance()
    private var savedPath: Uri? = null
    private var mLongitude: Double = 0.0
    private var mLatitude: Double = 0.0

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            setSupportActionBar(toolbarAddPlace)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbarAddPlace.setNavigationOnClickListener {
                onBackPressed()
            }

            dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
            etDate.setOnClickListener(this@AddHappyPlaceActivity)
            tvAddImage.setOnClickListener(this@AddHappyPlaceActivity)
            btnSave.setOnClickListener(this@AddHappyPlaceActivity)
        }


    }

    // onClick()를 각각 정해주지 않고 통합해서 사용 when id로 구분
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery",
                "Capture photo from Camera")
                pictureDialog.setItems(pictureDialogItems){ _, which ->
                    when(which){
                        0-> choosePhotoFromGallery()
                        1-> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save ->{
                binding.apply {
                    when{
                        etTitle.text.isNullOrEmpty() -> Toast.makeText(this@AddHappyPlaceActivity, "Please Enter Title", Toast.LENGTH_SHORT).show()
                        etDescription.text.isNullOrEmpty() -> Toast.makeText(this@AddHappyPlaceActivity, "Please Enter Description", Toast.LENGTH_SHORT).show()
                        etLocation.text.isNullOrEmpty() -> Toast.makeText(this@AddHappyPlaceActivity, "Please Enter Location", Toast.LENGTH_SHORT).show()
                        savedPath == null -> Toast.makeText(this@AddHappyPlaceActivity, "Please Select an Image", Toast.LENGTH_SHORT).show()
                        else -> {
                            val happyPlaceMode = HappyPlaceModel(
                                    0,
                                    etTitle.text.toString(),
                                    savedPath.toString(),
                                    etDescription.text.toString(),
                                    etDate.text.toString(),
                                    etLocation.text.toString(),
                                    mLatitude,
                                    mLongitude
                            )
                            val dbHandler = DatabaseHandler(this@AddHappyPlaceActivity)
                            val addHappyPlaceResult = dbHandler.addHappyPlace(happyPlaceMode)
                            if(addHappyPlaceResult > 0){ // 정상
                                Toast.makeText(this@AddHappyPlaceActivity, "The happy place details are inserted successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }


                }
            }
        }
    }

    // https://github.com/Karumi/Dexter#multiple-permissions
    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this@AddHappyPlaceActivity).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    // External Storage 설명 - https://developer.android.com/training/data-storage
                    // intent 생성, Gallery 미디어파일 접근 / Intent(String action, Uri uri)
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                    // 실행 후 결과는 onActivityResult
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun takePhotoFromCamera(){
        Dexter.withActivity(this@AddHappyPlaceActivity).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                Log.d("CAMERAINTENT:", "onPermission")
                if(report!!.areAllPermissionsGranted()){
                    Log.d("CAMERAINTENT:", "areAllPermissionsGranted")
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA)
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){ // Intent가 정상 작동
            if(requestCode == GALLERY){ // 응답 받은게 Gallery 이면
                if(data != null){ // 데이터(이미지) != null
                    val contentURI = data.data // Uri getData()
                    try{
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI) // deprecated 됐지만 아직 대안이 없어서 사용함
                        savedPath = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved image : ", "Path :: $savedPath")
                        binding.ivPlaceImage.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed to load the Image from gallery", Toast.LENGTH_SHORT).show()
                    }

                }
            }else if(requestCode == CAMERA){
                if(data != null){
                    val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                    savedPath = saveImageToInternalStorage(thumbnail)
                    Log.e("Saved image : ", "Path :: $savedPath")
                    binding.ivPlaceImage.setImageBitmap(thumbnail)
                }
            }
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required for this feature. It can be enabled under the Applications Settings.")
                .setPositiveButton("Go to Settings"){
                    _, _ ->
                    try{
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel"){  dialog, _ ->
                    dialog.dismiss()
                }.show()

    }

    private fun updateDateInView(){
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE) // 이 앱에서만 접근 가능하게 설정
        file = File(file, "${UUID.randomUUID()}.jpg") // 랜덤 UUID로 파일 생성
        Log.d("FILE:","$file")
        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}