package com.billsAplication.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import javax.inject.Inject

class MotionViewY@Inject constructor() {
    operator fun invoke(view: View,
                        currentHeight: Float,
                        newHeight: Float){
        val valueAnimator = ValueAnimator.ofFloat(currentHeight, newHeight)
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            view.translationY = value
        }
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 300
        valueAnimator.start()
    }
}