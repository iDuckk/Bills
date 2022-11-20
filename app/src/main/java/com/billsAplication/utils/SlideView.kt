package com.billsAplication.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import javax.inject.Inject

class SlideView@Inject constructor() {
    operator fun invoke(view: View,
                        currentHeight: Int,
                        newHeight: Int){

        val slideAnimator = ValueAnimator
            .ofInt(currentHeight, newHeight)
            .setDuration(500)

        slideAnimator.addUpdateListener { animation1: ValueAnimator ->
            val value = animation1.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }

        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationSet.play(slideAnimator)
        animationSet.start()
    }
}