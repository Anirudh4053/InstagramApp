package com.app.instagramapp.NewPost

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.app.instagramapp.DB.DatabaseHandler
import com.app.instagramapp.R
import com.app.instagramapp.model.Post
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_new_post_page.*
import kotlinx.android.synthetic.main.content_new_post_page.*

class NewPostPage : AppCompatActivity() {

    private var mTempFilePath: Uri? = null
    var dbHandler: DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post_page)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { finish() }
        dbHandler = DatabaseHandler(this)
        val extras:Bundle? = intent.extras
        if(extras!=null){
            mTempFilePath = Uri.parse(extras.getString("imagePath",""))
            extras.getString("")
            println("new post mTempFilePath ${extras.getString("imagePath","")}")
        }

        //println("new post mTempFilePath ${intent.getStringExtra("imagePath")}")

        Glide
            .with(this)
            .load(mTempFilePath)
            .centerCrop()
            .placeholder(R.drawable.ic_dummy)
            .into(imageView)

        shareBtn.setOnClickListener {
            hideKeyboard()
            savePost()
        }
    }
    fun savePost(){
        var success: Boolean = false
        val post: Post = Post()
        post.name = "anirudh4053"
        post.desc = caption.text.toString()
        post.imagepath = mTempFilePath.toString()

        success = dbHandler?.addPost(post) as Boolean
        println("success $success -- $post")
        Toast.makeText(this,"Uploaded successfully",Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
    fun hideKeyboard() {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
