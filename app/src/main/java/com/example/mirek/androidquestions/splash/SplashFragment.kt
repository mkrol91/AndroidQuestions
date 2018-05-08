package com.example.mirek.androidquestions.splash

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.Explode
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import com.example.mirek.androidquestions.R
import kotlinx.android.synthetic.main.splash_fragment_initial.*

class SplashFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialConstraints = ConstraintSet()
        initialConstraints.clone(splash_root)

        val secondChangeBounds = customChangeBoundsTransition({
            interpolator = AccelerateInterpolator()
            duration = 1000
        }, {
            val explode = ChangeBounds()
            explode.duration = 500
            explode.interpolator = AccelerateInterpolator()

            val fourthConstraints = ConstraintSet()
            fourthConstraints.clone(activity, R.layout.splash_fragment_fourth)
            applyAnimationToRoot(fourthConstraints, explode)
        })

        val firstChangeBounds = customChangeBoundsTransition(
                {
                    interpolator = AccelerateInterpolator()
                    duration = 1000
                },
                {
                    val thirdConstraints = ConstraintSet()
                    thirdConstraints.clone(activity, R.layout.splash_fragment_third)
                    applyAnimationToRoot(thirdConstraints, secondChangeBounds)
                }
        )

        Handler().postDelayed({
            val secondConstraints = ConstraintSet()
            secondConstraints.clone(activity, R.layout.splash_fragment_second)
            applyAnimationToRoot(secondConstraints, firstChangeBounds)
        }, 1000)

    }

    fun customChangeBoundsTransition(initParameters: ChangeBounds.() -> Unit, onTransitionEnd: ChangeBounds.() -> Unit): ChangeBounds {
        return ChangeBounds().apply {
            initParameters()
            addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    onTransitionEnd()
                }

                override fun onTransitionResume(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionStart(transition: Transition) {
                }
            })
        }
    }


    private fun applyAnimationToRoot(newConstraints: ConstraintSet, preparedTransition: ChangeBounds) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        newConstraints.applyTo(splash_root)
    }

    private fun applyAnimationToRoot(newConstraints: ConstraintSet, preparedTransition: Explode) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        newConstraints.applyTo(splash_root)
    }


    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

