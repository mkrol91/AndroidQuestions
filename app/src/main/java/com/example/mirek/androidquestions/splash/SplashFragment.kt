package com.example.mirek.androidquestions.splash

import android.animation.TimeInterpolator
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import com.example.mirek.androidquestions.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.splash_fragment_initial.*
import java.util.concurrent.TimeUnit

class SplashFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animationPhases = arrayListOf(
                ConstraintSet().apply {
                    clone(splash_root)
                },
                ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_second)
                },
                ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_third)
                },
                ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_fourth)
                },
                ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_f)
                }
        )


        val sixthChangeBounds = customChangeBoundsTransition(500, AccelerateInterpolator(), {})
        val fifthChangeBounds = customChangeBoundsTransition(5000, AccelerateInterpolator(),
                {
                    //  applyAnimationToRoot(animationPhases[4], sixthChangeBounds)
                })
        val fourthChangeBounds = customChangeBoundsTransition(1000, AccelerateInterpolator(),
                {
                    applyAnimationToRoot(animationPhases[3], fifthChangeBounds)
                })
        val thiChangeBounds = customChangeBoundsTransition(1000, AccelerateInterpolator(),
                {
                    applyAnimationToRoot(animationPhases[2], fourthChangeBounds)
                })
        val secChangeBounds = customChangeBoundsTransition(1000, AccelerateInterpolator(),
                {
                    applyAnimationToRoot(animationPhases[1], thiChangeBounds)
                })

        Observable.just(1).subscribeOn(Schedulers.io())
                .delay(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    applyAnimationToRoot(animationPhases[0], secChangeBounds)
                }
    }

    fun customChangeBoundsTransition(durationTime: Long, interpolatorType: TimeInterpolator,
                                     onTransitionEnd: ChangeBounds.() -> Unit): ChangeBounds {
        return ChangeBounds().apply {
            duration = durationTime
            interpolator = interpolatorType
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

    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

