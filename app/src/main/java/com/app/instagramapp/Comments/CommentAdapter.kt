package com.app.instagramapp.Comments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.instagramapp.DB.DatabaseHandler
import com.app.instagramapp.Other.getUserName
import com.app.instagramapp.R
import com.app.instagramapp.model.Comment
import com.app.instagramapp.model.Post
import kotlinx.android.synthetic.main.ic_custom_comment_list.view.*

class CommentAdapter(private val mContext: Context, private val catList: List<Comment>, private val dbHandler: DatabaseHandler) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ic_custom_comment_list, parent, false)

        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: CommentAdapter.MyViewHolder, p1: Int) {

        val item = catList[p1]
        val username = getUserName(mContext)
        holder.itemView.username.text = username
        holder.itemView.username.visibility = View.GONE
        holder.itemView.comment.text = item.label


    }

    override fun getItemCount(): Int {
        return catList.size
    }
}