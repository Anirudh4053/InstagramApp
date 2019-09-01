package com.app.instagramapp.Other

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.instagramapp.R
import com.app.instagramapp.R.id.imageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.ic_custom_slider.view.*

class SliderViewPager(private val context: Context, var images: List<Uri>): PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater
    override fun instantiateItem(container: ViewGroup, position: Int): View {
        layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater.inflate(R.layout.ic_custom_slider, container, false)

        //view.imageView.setImageResource(images[position])

        Glide
            .with(context)
            .load(images[position])
            .centerCrop()
            .placeholder(R.drawable.ic_dummy)
            .into(view.imageView)

        container.addView(view)

        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount() = images.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any)
            = (container as ViewPager).removeView(`object` as View)
}