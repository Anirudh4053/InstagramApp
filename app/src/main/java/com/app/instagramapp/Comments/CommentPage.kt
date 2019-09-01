package com.app.instagramapp.Comments

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.app.instagramapp.DB.DatabaseHandler
import com.app.instagramapp.Other.VerticalSpaceItemDecoration
import com.app.instagramapp.R
import com.app.instagramapp.model.Comment
import kotlinx.android.synthetic.main.activity_comment_page.*
import kotlinx.android.synthetic.main.content_comment_page.*
import kotlinx.android.synthetic.main.ic_comment_layout.view.*
import java.io.File


class CommentPage : AppCompatActivity() {
    var dbHandler: DatabaseHandler? = null
    private val VERTICAL_ITEM_SPACE = 24
    var items: List<Comment> = ArrayList<Comment>()
    private lateinit var adapter: CommentAdapter
    var postId:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_page)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        val extras:Bundle? = intent.extras
        if(extras!=null){
            postId = extras.getInt("postId",0)
            println("postId $postId")
        }
        dbHandler = DatabaseHandler(this)

        initDB()
        val mLayoutManager = LinearLayoutManager(this@CommentPage)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        //add ItemDecoration
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recyclerView.adapter = adapter
        include.sentImage.setOnClickListener {
            val commentTxt = include.comment.text.toString()
            if(!commentTxt.isNullOrEmpty()){
                include.comment.setText("")

                var success: Boolean = false
                val comment: Comment = Comment()
                comment.label = commentTxt
                comment.postId = postId
                success = dbHandler!!.addComment(comment) as Boolean
                println("added comment $success")

                if(success){
                    initDB()
                }

            }
        }
    }
    fun initDB() {

        items = (dbHandler as DatabaseHandler).getCommentByPost(postId)
        println("items $items")
        adapter = CommentAdapter(this,items, dbHandler!!)
        (recyclerView as RecyclerView).adapter = adapter
        adapter.notifyDataSetChanged();
        if(items.size!=0) {
            recyclerView.scrollToPosition(items.size - 1);
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
