package com.example.mirek.androidquestions.splash

import android.animation.TimeInterpolator
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.example.mirek.androidquestions.R
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.splash_fragment_initial.*

class SplashFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed(Runnable {
            val animation1 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_second)
                }, customChangeBoundsTransition(500, AccelerateInterpolator(), {
                    Log.i("animationFlow", "anim 1 finished")
                    it.onComplete()
                }))
            }

            val animation2 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_third)
                }, customChangeBoundsTransition(2000, AccelerateInterpolator(), {
                    Log.i("animationFlow", "anim 2 finished")
                    it.onComplete()
                }))
            }

            val animation3 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_fourth)
                }, customChangeBoundsTransition(200, OvershootInterpolator(), {
                    Log.i("animationFlow", "anim 3 finished")
                    it.onComplete()
                }))
            }

            val animation4 = Completable.create {
                explosion.animate().alpha(0f).withEndAction {
                    it.onComplete()
                    Log.i("animationFlow", "anim 4 finished")
                }.duration = 200
            }

            val animation5 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_end)
                }, customChangeBoundsTransition(300, OvershootInterpolator(), {
                    Log.i("animationFlow", "anim 5 finished")
                    it.onComplete()
                }))
            }

            val animation6 = Completable.create {
                atCs.visibility = View.VISIBLE
                atCs.alpha = 0f
                atCs.animate().alpha(1f).withEndAction {
                    it.onComplete()
                    Log.i("animationFlow", "anim 5 finished")
                }.duration = 5000
            }

            animation1.andThen(animation2).andThen(animation3).andThen(animation4).andThen(animation5)
                    .andThen(animation6)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i("abcd", "success")
                    }, {
                        Log.i("abcd", "error:" + it)
                    })

        }, 10)
    }

    fun customChangeBoundsTransition(durationTime: Long, interpolatorType: TimeInterpolator,
                                     onTransitionEnd: ChangeBounds.() -> Unit): ChangeBounds {
        return ChangeBounds().apply {
            duration = durationTime
            interpolator = interpolatorType
            addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    Log.i("abcd", "trans end")
                    onTransitionEnd()
                }

                override fun onTransitionResume(transition: Transition) {
                    Log.i("abcd", "trans resume")
                }

                override fun onTransitionPause(transition: Transition) {
                    Log.i("abcd", "trans pause")
                }

                override fun onTransitionCancel(transition: Transition) {
                    Log.i("abcd", "trans cancel")
                }

                override fun onTransitionStart(transition: Transition) {
                    Log.i("abcd", "trans start")
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

