package com.billsAplication.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import javax.inject.Inject

class CrossFade@Inject constructor() {

    operator fun invoke(currentView: View, newView: View){
        currentView.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null)
        }
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        newView.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    newView.visibility = View.GONE
                }
            })
    }
}