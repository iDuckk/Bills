package com.billsAplication.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import javax.inject.Inject

class RotationView@Inject constructor() {
    operator fun invoke(view: View,
                        currentHeight: Float,
                        newHeight: Float){
        val valueAnimator = ValueAnimator.ofFloat(currentHeight, newHeight)
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            view.rotation = value
        }
        valueAnimator.duration = 500
        valueAnimator.start()
    }
}