package com.example.movieapp.ViewPager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager: ViewPager {
    private var swipable = false

    constructor(context:Context?):super(context!!){}
    constructor(context:Context?, attrs: AttributeSet?):super(
        context!!,
        attrs
    ){
        swipable = true
    }

    override fun onTouchEvent(event: MotionEvent):Boolean{
        return if(swipable){
            super.onTouchEvent(event)
        }else{
            false
        }
    }

    override fun onInterceptTouchEvent(ev:MotionEvent):Boolean{
        return if(swipable) super.onInterceptTouchEvent(ev) else false
    }

    fun setSwipable(swipe:Boolean){
        swipable = swipe
    }
}