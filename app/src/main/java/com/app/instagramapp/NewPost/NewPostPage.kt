package com.app.instagramapp.NewPost

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.app.instagramapp.DB.DatabaseHandler
import com.app.instagramapp.Other.Slider
import com.app.instagramapp.Other.SliderViewPager
import com.app.instagramapp.R
import com.app.instagramapp.model.Post
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_new_post_page.*
import kotlinx.android.synthetic.main.content_new_post_page.*
import android.content.Intent
import android.webkit.MimeTypeMap
import java.io.File


class NewPostPage : AppCompatActivity(), ViewPager.OnPageChangeListener {


    private var videoPathUri: Uri? = null
    var dbHandler: DatabaseHandler? = null
    private var TYPE:Int = 0
    private var imagePath:String = ""
    private var imagePathUri = arrayListOf<Uri>()
    //slider
    private var dots: ArrayList<TextView> = arrayListOf()
    private lateinit var viewPagerAdapter: SliderViewPager


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


            TYPE = extras.getInt("type",0)
            if(TYPE==1){
                videoPathUri = Uri.parse(extras.getString("imagePath",""))
            }
            else{
                imagePath = extras.getString("imagePath","")
            }
            println("new post mTempFilePath $TYPE -- ${extras.getString("imagePath","")}")
            var result: List<String> = imagePath.split(",").map { it.trim() }
            result.forEach {
                imagePathUri.add(Uri.parse(it))
            }
        }

        if(TYPE==1) {
            videoView.visibility = View.VISIBLE
            videoView.setVideoURI(videoPathUri)
            videoView.setZOrderOnTop(true);
            videoView.start()
        }
        else {
            videoView.visibility = View.GONE
            //slider
            setUpViewPager()
            if(imagePathUri.size>1)
                addBottomDots(0)
        }
        shareBtn.setOnClickListener {
            hideKeyboard()
            savePost()
        }


    }
    override fun onPageScrollStateChanged(p0: Int) {
        println("viewpager onPageScrollStateChanged $p0") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        println("viewpager onPageScrolled $p0") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageSelected(position: Int) {
        if(imagePathUri.size>1)
            addBottomDots(position);
        println("viewpager onPageSelected $position") //To change body of created functions use File | Settings | File Templates.

    }
    private fun addBottomDots(currentPage: Int) {
        println("slider ${Slider().initCode(imagePathUri.size)}")
        dots.clear()
        dots.addAll(Slider().initCode(imagePathUri.size))


        layoutDots.removeAllViews()
        for (i in 0 until dots.size) {
            dots[i] = TextView(this)
            dots[i].text = Html.fromHtml("&#8226;")
            dots[i].textSize = 30F
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.grey))
            layoutDots.addView(dots[i])
        }

        if (dots.size > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.blue))
    }
    private fun setUpViewPager(){
        viewPagerAdapter = SliderViewPager(this,imagePathUri)
        view_pager.adapter = viewPagerAdapter
        view_pager.addOnPageChangeListener(this)
        view_pager.offscreenPageLimit = imagePathUri.size
    }
    fun savePost(){
        var imagePathArray:String = ""
        if(TYPE==0) {
            imagePathArray = TextUtils.join(", ", imagePathUri)
        }

        var success: Boolean = false
        val post: Post = Post()
        post.name = "anirudh4053"
        post.desc = caption.text.toString()
        if(TYPE==1) {
            post.imagepath = videoPathUri.toString()
        }
        else{
            post.imagepath = imagePathArray
        }

        post.type = TYPE

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
