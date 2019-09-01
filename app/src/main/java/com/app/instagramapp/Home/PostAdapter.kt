package com.app.instagramapp.Home

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.instagramapp.DB.DatabaseHandler
import com.app.instagramapp.Other.getUserName
import com.app.instagramapp.R
import com.app.instagramapp.model.Comment
import com.app.instagramapp.model.Post
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.ic_comment_layout.view.*
import kotlinx.android.synthetic.main.ic_custom_item.view.*
import tcking.github.com.giraffeplayer2.GiraffePlayer
import tcking.github.com.giraffeplayer2.PlayerListener
import tv.danmaku.ijk.media.player.IjkTimedText


class PostAdapter(private val mContext: Context, private val catList: List<Post>, private val dbHandler: DatabaseHandler,
                  private val onItemClick:(Post)->Unit, private val onMenuClick:(Post)->Unit,
                  private val onCommentSent:(Post)->Unit,
                  private val onViewComments:(Post)->Unit,
                  private val onShareImage:(Post)->Unit) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {


    private var dots: ArrayList<TextView> = arrayListOf()
    private var imagePathUri = arrayListOf<Uri>()

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
        holder.itemView.name.text = username
        holder.itemView.userName.text = username
        holder.itemView.caption.text = item.desc
        holder.itemView.singleCommentLayout.visibility = View.GONE

        holder.itemView.setOnClickListener {
            onItemClick(item)

        }
        holder.itemView.delete.setOnClickListener {
            onMenuClick(item)
            if(item.type==1){
                holder.itemView.videoView.player.stop()
            }
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
            holder.itemView.itemcomment.text = comm.label
            holder.itemView.commentName.text = username
        }

        holder.itemView.sentImage.setOnClickListener {
            val commentTxt = holder.itemView.includeComment.comment.text.toString()
            if(!commentTxt.isNullOrEmpty()){
                onCommentSent(item)
                holder.itemView.singleCommentLayout.visibility = View.VISIBLE
                holder.itemView.itemcomment.text = commentTxt
                holder.itemView.commentName.text = username
                holder.itemView.includeComment.comment.setText("")

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
                            //holder.itemView.allcomments.text = "view $count comment"
                            holder.itemView.allcomments.visibility = View.GONE
                        }
                        else {
                            holder.itemView.allcomments.text = "view all $count comments"
                            holder.itemView.allcomments.visibility = View.VISIBLE
                        }

                    }
                    else
                        holder.itemView.allcomments.visibility = View.GONE
                }
            }
        }
        holder.itemView.allcomments.setOnClickListener {
            onViewComments(item)
        }
        holder.itemView.shareImage.setOnClickListener {
            onShareImage(item)
        }
        holder.itemView.chatImage.setOnClickListener {
            onViewComments(item)
        }

        if(item.type==1){
            holder.itemView.videoView.visibility = View.VISIBLE
            holder.itemView.image_slider.visibility = View.INVISIBLE
            holder.itemView.videoView
                .setVideoPath(item.imagepath)
                .setFingerprint(p1).setPlayerListener(object:PlayerListener{
                    override fun onTimedText(giraffePlayer: GiraffePlayer?, text: IjkTimedText?) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onPrepared(giraffePlayer: GiraffePlayer?) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onRelease(giraffePlayer: GiraffePlayer?) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onCompletion(giraffePlayer: GiraffePlayer?) {
                        giraffePlayer?.stop()
                    }

                    override fun onPause(giraffePlayer: GiraffePlayer?) {
                        println("onPause") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onLazyLoadError(giraffePlayer: GiraffePlayer?, message: String?) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onTargetStateChange(oldState: Int, newState: Int) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDisplayModelChange(oldModel: Int, newModel: Int) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onSeekComplete(giraffePlayer: GiraffePlayer?) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onInfo(giraffePlayer: GiraffePlayer?, what: Int, extra: Int): Boolean {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                        return false
                    }

                    override fun onBufferingUpdate(giraffePlayer: GiraffePlayer?, percent: Int) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onCurrentStateChange(oldState: Int, newState: Int) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onLazyLoadProgress(giraffePlayer: GiraffePlayer?, progress: Int) {
                        println("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onError(giraffePlayer: GiraffePlayer?, what: Int, extra: Int): Boolean {
                        println("onError") //To change body of created functions use File | Settings | File Templates.
                        return false
                    }

                    override fun onPreparing(giraffePlayer: GiraffePlayer?) {
                        println("onPreparing") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onStart(giraffePlayer: GiraffePlayer?) {
                        println("onStart") //To change body of created functions use File | Settings | File Templates.
                    }

                })
        }
        else{
            holder.itemView.videoView.visibility = View.GONE
            holder.itemView.image_slider.visibility = View.VISIBLE
            val imageList = ArrayList<SlideModel>()
            var result: List<String> = item.imagepath.split(",").map { it.trim() }
            imagePathUri.clear()
            result.forEach {
                imagePathUri.add(Uri.parse(it))
                imageList.add(SlideModel(it))
            }
            holder.itemView.image_slider.setImageList(imageList,true)
        }

    }
    /*private fun addBottomDots(holder: PostAdapter.MyViewHolder,currentPage: Int) {
        println("slider ${Slider().initCode(imagePathUri.size)}")
        dots.clear()
        dots.addAll(Slider().initCode(imagePathUri.size))


        holder.itemView.layoutDots.removeAllViews()
        for (i in 0 until dots.size) {
            dots[i] = TextView(mContext)
            dots[i].text = Html.fromHtml("&#8226;")
            dots[i].textSize = 30F
            dots[i].setTextColor(ContextCompat.getColor(mContext, R.color.grey))
            holder.itemView.layoutDots.addView(dots[i])
        }

        if (dots.size > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(mContext, R.color.blue))
    }
    private fun setUpViewPager(holder: PostAdapter.MyViewHolder){
        println("imagePathUri.size ${imagePathUri.size}")
        val viewPagerAdapter = SliderViewPager(mContext,imagePathUri)
        holder.itemView.view_pager.adapter = viewPagerAdapter
        holder.itemView.view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(imagePathUri.size>1)
                    addBottomDots(holder,position);
            }

        })
        holder.itemView.view_pager.offscreenPageLimit = imagePathUri.size
    }*/

    override fun getItemCount(): Int {
        return catList.size
    }


}