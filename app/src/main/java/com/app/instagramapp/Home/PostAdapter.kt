package com.app.instagramapp.Home

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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.ic_comment_layout.view.*
import kotlinx.android.synthetic.main.ic_custom_item.view.*
import kotlinx.android.synthetic.main.ic_custom_item.view.comment



class PostAdapter(private val mContext: Context, private val catList: List<Post>, private val dbHandler: DatabaseHandler,
                  private val onItemClick:(Post)->Unit, private val onMenuClick:(Post)->Unit,
                  private val onCommentSent:(Post)->Unit,
                  private val onViewComments:(Post)->Unit) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*var title: TextView
        var image: ImageView

        init {
            title = view.findViewById(R.id.title)
            image = view.findViewById(R.id.image) as ImageView
        }*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ic_custom_item, parent, false)

        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: PostAdapter.MyViewHolder, p1: Int) {

        val item = catList[p1]
        val username = getUserName(mContext)
        /*holder.itemView.name.text = item.name
        var imageUrl:String = ""

        imageUrl = item.largeImage?:""
        holder.itemView.price.text = "$CURR${item.salePrice.toString()}"
        val circularProgressDrawable = CircularProgressDrawable(mContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide
            .with(mContext)
            .load(imageUrl)
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(holder.itemView.prodImage)
            */
        holder.itemView.name.text = username
        holder.itemView.userName.text = username
        holder.itemView.caption.text = item.desc
        holder.itemView.singleCommentLayout.visibility = View.GONE
        Glide
            .with(mContext)
            .load(item.imagepath)
            .centerCrop()
            .placeholder(R.drawable.ic_dummy)
            .into(holder.itemView.mainImage)
        holder.itemView.setOnClickListener {
            onItemClick(item)

        }
        holder.itemView.delete.setOnClickListener {
            onMenuClick(item)
        }

        val count = dbHandler.getCommentCount(item.id)
        if(count!=0){
            if(count==1){
                /*holder.itemView.allcomments.text = "view $count comment"*/
                holder.itemView.allcomments.visibility = View.GONE
            }
            else {
                holder.itemView.allcomments.text = "view all $count comments"
                holder.itemView.allcomments.visibility = View.VISIBLE
            }


        }
        else
            holder.itemView.allcomments.visibility = View.GONE

        val comm = dbHandler.getLatestComment(item.id)
        println("comm $comm")
        if(comm.id!=0){
            holder.itemView.singleCommentLayout.visibility = View.VISIBLE
            holder.itemView.comment.text = comm.label
            holder.itemView.commentName.text = username
        }

        holder.itemView.sentImage.setOnClickListener {
            val commentTxt = holder.itemView.includeComment.comment.text.toString()
            if(!commentTxt.isNullOrEmpty()){
                onCommentSent(item)
                holder.itemView.singleCommentLayout.visibility = View.VISIBLE
                holder.itemView.comment.text = commentTxt
                holder.itemView.commentName.text = username
                holder.itemView.includeComment.comment.text = ""

                var success: Boolean = false
                val comment: Comment = Comment()
                comment.label = commentTxt
                comment.postId = item.id
                success = dbHandler.addComment(comment) as Boolean

                println("added comment $success")

                if(success){
                    val count = dbHandler.getCommentCount(item.id)
                    if(count!=0){
                        if(count==1){
                            holder.itemView.allcomments.text = "view $count comment"
                        }
                        else
                            holder.itemView.allcomments.text = "view all $count comments"
                        holder.itemView.allcomments.visibility = View.VISIBLE

                    }
                    else
                        holder.itemView.allcomments.visibility = View.GONE
                }
            }
        }
        holder.itemView.allcomments.setOnClickListener {
            onViewComments(item)
        }
        holder.itemView.chatImage.setOnClickListener {
            onViewComments(item)
        }


    }

    override fun getItemCount(): Int {
        return catList.size
    }
}