package com.app.instagramapp.Home

import android.Manifest
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.app.instagramapp.Comments.CommentPage
import com.app.instagramapp.DB.DatabaseHandler
import com.app.instagramapp.NewPost.NewPostPage
import com.app.instagramapp.Other.*
import com.app.instagramapp.R
import com.app.instagramapp.model.Post
import com.denzcoskun.imageslider.models.SlideModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null

    private val VERTICAL_ITEM_SPACE = 24
    private lateinit var adapter: PostAdapter
   // val items = arrayListOf<Post>()
    var items: List<Post> = ArrayList<Post>()

    //camera
    //check permission
    internal var permissionsRequired = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var sentToSettings = false
    private var fileName: String = ""
    private var filePath: File? = null
    private var mTempFilePath: Uri? = null
    private var videoUri: Uri? = null
    private var TYPE:Int = 0

    //multiple images
    var imageEncoded: String = ""
    var imagesEncodedList = arrayListOf<Uri>()
    var imagesUriArrayList = arrayListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        dbHandler = DatabaseHandler(this)

        //load post
        initDB()
        val mLayoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        //add ItemDecoration
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recyclerView.adapter = adapter

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
    fun initDB() {
        items = (dbHandler as DatabaseHandler).post()
        println("items $items")
        adapter = PostAdapter(this,items, dbHandler!!,{
            println("productDetail: $it")

        },{
            println("Contribution: $it")
            deleteAlert(it)

        },{
            println("Add comment: $it")
        },{
            println("view comment: $it")
            val i = Intent(this,CommentPage::class.java)
            i.putExtra("postId",it.id)
            startActivityForResult(i,23)
        }){
            try {
                if(it.type==0) {
                    var result: List<String> = it.imagepath.split(",").map { it.trim() }
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND_MULTIPLE
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
                    intent.type = "image/jpeg" /* This example is sharing jpeg images. */

                    val files = ArrayList<Uri>()
                    result.forEach {
                        files.add(Uri.parse(it))
                    }
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
                    startActivity(intent)
                }
                else{

                    ShareCompat.IntentBuilder.from(this)
                        .setStream(Uri.parse(it.imagepath))
                        .setType("video/mp4")
                        .setChooserTitle("Share video...")
                        .startChooser()
                    //Toast.makeText(this,"Video cannot be shared",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        (recyclerView as RecyclerView).adapter = adapter
        adapter.notifyDataSetChanged();
    }
    fun deleteAlert(it:Post){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete this post?")
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.cancel()
            (dbHandler as DatabaseHandler).deletePost(it.id)
            (dbHandler as DatabaseHandler).deleteAllComments(it.id)
            initDB()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()

        }
        builder.show()
    }
    //camera functions
    fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsRequired,
                    PERMISSION_CALLBACK_CONSTANT
                )
            } else if (getPermissionRequest(this)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs Camera and Read-Write permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    sentToSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
                    Toast.makeText(this, "Go to Permissions to Grant Camera and Read-Write", Toast.LENGTH_LONG)
                        .show()
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this,permissionsRequired, PERMISSION_CALLBACK_CONSTANT)
            }
            setPermissionRequest(this,true)
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }

            if (allgranted) {
                proceedAfterPermission()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
            ) {
                //txtPermissions.setText("Permissions Required");
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Need Multiple Permissions")
                builder.setMessage("This app needs Camera and Read-Write permissions.")
                builder.setPositiveButton("Grant") { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(
                        this,
                        permissionsRequired,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.show()
            } else {
                Toast.makeText(this, "Unable to get Permission", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun proceedAfterPermission() {
        try {
            mTempFilePath = null
            filePath = null

            val options = arrayOf("Take Photo", "Choose From Gallery","Take Video","Cancel")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Option")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        dialog.dismiss()
                        cameraIntent()
                        TYPE = 0

                    }
                    options[item] == "Choose From Gallery" -> {
                        dialog.dismiss()
                        galleryIntent()
                        TYPE = 0
                    }
                    options[item] == "Take Video" -> {
                        dialog.dismiss()
                        takeVideo()
                        TYPE = 1
                    }
                    options[item] == "Remove Profile Pic" -> {
                        dialog.dismiss()
                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Camera Permission not given", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    private fun galleryIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE)
    }
    private fun cameraIntent() {
        //deleteBuyZengaImages()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //deleteImages()
        mTempFilePath = createImageUri()
        //mTempFilePath = getOutputMediaFileUri(1)
        println("mTempFilePath $mTempFilePath")
        if (mTempFilePath != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempFilePath)
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).forEach {
                val packageName = it.activityInfo.packageName
                grantUriPermission(packageName, mTempFilePath, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(intent, CAMERA_REQUEST)
        }

    }
    private fun createImageUri(): Uri? {
        val imageFileName = "image_"
        val storageDir = this.getExternalFilesDir(TEMP_FILES)
        println("storageDir $storageDir")
        val image = File.createTempFile(imageFileName, ".png", storageDir)

        if (image != null) {
            mTempFilePath = Uri.parse(image.absolutePath)
            try {
                return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, image)
            } catch (e: IllegalArgumentException) {
                return null
            } catch (e: java.lang.Exception) {
                return null
            }
        } else {
            return null
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            23->{
                initDB()
            }
            CAMERA_REQUEST->
                if (resultCode == Activity.RESULT_OK) {
//                    onCaptureImageResult(data)
                    val i = Intent(this@MainActivity,NewPostPage::class.java)
                    i.putExtra("imagePath",mTempFilePath.toString())
                    i.putExtra("type",TYPE)
                    startActivityForResult(i,23)
                }
            CROP_REQUEST->{
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    mTempFilePath = result.uri

                    val i = Intent(this@MainActivity,NewPostPage::class.java)
                    i.putExtra("imagePath",mTempFilePath.toString())
                    i.putExtra("type",TYPE)
                    startActivityForResult(i,23)
                    println("resultUri $mTempFilePath")
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    println("IMage error $error")
                    Toast.makeText(this, ERROR,Toast.LENGTH_SHORT).show()
                }
            }
            PICK_IMAGE_MULTIPLE->{
                if (resultCode == Activity.RESULT_OK && null!=data) {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (data.getData() != null) {

                        val mImageUri = data.getData()
                        println("imageRealPath ${getRealPath(mImageUri)}")
                        mTempFilePath = Uri.fromFile(File(getRealPath(mImageUri)))
                        val i = Intent(this@MainActivity,NewPostPage::class.java)
                        i.putExtra("imagePath",mTempFilePath.toString())
                        i.putExtra("type",TYPE)
                        startActivityForResult(i,23)
                    } else {
                        if (data.getClipData() != null) {
                            val mClipData = data.getClipData()
                            val mArrayUri = ArrayList<Uri>()
                            for (i in 0 until mClipData.getItemCount()) {

                                val item = mClipData.getItemAt(i)
                                val uri = item.getUri()
                                mArrayUri.add(uri)
                            }
                            for(i in 0 until mArrayUri.size){
                                imagesEncodedList.add(Uri.fromFile(File(getRealPath(mArrayUri[i]))))
                            }
                            val s = TextUtils.join(", ", imagesEncodedList)
                            val i = Intent(this@MainActivity,NewPostPage::class.java)
                            i.putExtra("imagePath",s)
                            i.putExtra("type",TYPE)
                            startActivityForResult(i,23)
                            Log.v("LOG_TAG", "Selected Images" + s)
                        }
                    }

                }
            }
            GALLERY_REQUEST->{
                if (resultCode == Activity.RESULT_OK) {

                    mTempFilePath = data?.data
                    println("path before $mTempFilePath")
                    if(mTempFilePath!=null) {
                        if (getPath(mTempFilePath!!).isNotEmpty()) {
                            mTempFilePath = convertFileToContentUri(this, File(getPath(mTempFilePath!!)))
                            println("path $mTempFilePath")

                            val i = Intent(this@MainActivity,NewPostPage::class.java)
                            i.putExtra("imagePath",mTempFilePath.toString())
                            i.putExtra("type",TYPE)
                            startActivityForResult(i,23)
                        }
                    }
                }
            }
            VIDEO_CAPTURE->{
                if (resultCode == Activity.RESULT_OK) {
                    println("Video ${data?.getData()} -- $videoUri")
                    val i = Intent(this@MainActivity,NewPostPage::class.java)
                    i.putExtra("imagePath",videoUri.toString())
                    i.putExtra("type",TYPE)
                    startActivityForResult(i,23)
                }
            }
        }
    }
    private fun getRealPath(uri: Uri):String{
        var cursor = this.contentResolver.query(uri, null, null, null, null)
        val result: String

        // for API 19 and above
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            cursor!!.moveToFirst()
            var image_id = cursor.getString(0)
            image_id = image_id.substring(image_id.lastIndexOf(":") + 1)
            cursor.close()

            cursor = this.contentResolver.query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID + " = ? ",
                arrayOf(image_id),
                null
            )

        }

        cursor!!.moveToFirst()
        result = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return result
    }

    private fun onCaptureImageResult(data: Intent?) {
        println("path onCaptureImageResult $mTempFilePath")

        if(mTempFilePath!=null) {
            val intent = CropImage.activity(mTempFilePath) .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                .getIntent(this)
            startActivityForResult(intent, CROP_REQUEST)
        }
    }
    private fun onSelectFromGalleryResult(data: Intent?) {
        mTempFilePath = data?.data
        if(mTempFilePath!=null){
            if(getPath(mTempFilePath!!).isNotEmpty()) {
                mTempFilePath = convertFileToContentUri(this, File(getPath(mTempFilePath!!)))
                println("path $mTempFilePath")
                val intent = CropImage.activity(mTempFilePath).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setRequestedSize(400, 400, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                    .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    .getIntent(this)
                startActivityForResult(intent, CROP_REQUEST)
            }

        }
    }
    fun convertFileToContentUri(context: Context, file: File): Uri {
        val cr = context.getContentResolver()
        val imagePath = file.absolutePath
        val imageName: String? = null
        val imageDescription: String? = null
        val uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription)
        return Uri.parse(uriString)
    }
    fun getPath(uri: Uri): String {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res?:""
    }
    fun takeVideo(){
        val mediaFile = File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/${System.currentTimeMillis()}.mp4")

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        videoUri = Uri.fromFile(mediaFile)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        startActivityForResult(intent, VIDEO_CAPTURE)
    }
    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            sentToSettings = false
            checkPermission()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_camera -> {
                checkPermission()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }
}
