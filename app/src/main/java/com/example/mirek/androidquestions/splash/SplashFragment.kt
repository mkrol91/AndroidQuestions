package com.example.mirek.androidquestions.splash

import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.util.emptyUnit
import kotlinx.android.synthetic.main.splash_fragment_initial.*

class SplashFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialConstraints = ConstraintSet()
        initialConstraints.clone(splash_root)

        val secondTransition = customTransition({
            interpolator = AccelerateInterpolator()
            duration = 3000
        }, {})

        val firstTransition = customTransition(
                {
                    interpolator = AccelerateInterpolator()
                    duration = 3000
                },
                {
                    val thirdConstraints = ConstraintSet()
                    thirdConstraints.clone(activity, R.layout.splash_fragment_third)
                    applyAnimationToRoot(thirdConstraints, secondTransition)
                }
        )

        Handler().postDelayed({
            val secondConstraints = ConstraintSet()
            secondConstraints.clone(activity, R.layout.splash_fragment_second)
            applyAnimationToRoot(secondConstraints, firstTransition)
        }, 3000)

    }

    fun customTransition(block: ChangeBounds.() -> Unit, endAction: ChangeBounds.() -> Unit): ChangeBounds {
        val changeBounds = ChangeBounds()
        changeBounds.block()
        changeBounds.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                changeBounds.endAction()
            }

            override fun onTransitionResume(transition: Transition?) {
            }

            override fun onTransitionPause(transition: Transition?) {
            }

            override fun onTransitionCancel(transition: Transition?) {
            }

            override fun onTransitionStart(transition: Transition?) {
            }
        })
        return changeBounds
    }

    private fun applyAnimationToRoot(newConstraints: ConstraintSet, preparedTransition: ChangeBounds) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        newConstraints.applyTo(splash_root)
    }

//    Transition explode = new Explode()
//    .setEpicenterCallback(new Transition.EpicenterCallback() {
//        @Override
//        public Rect onGetEpicenter(Transition transition) {
//            return viewRect;
//        }
//    });
//    explode.setDuration(1000);
//    TransitionManager.beginDelayedTransition(recyclerView, explode);

    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

