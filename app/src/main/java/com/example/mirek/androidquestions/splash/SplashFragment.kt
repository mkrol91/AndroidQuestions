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
import io.reactivex.CompletableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.splash_fragment_initial.*

class SplashFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment_initial, container, false)
    }

    override fun onResume() {
        super.onResume()
        Completable.concat(createAnimations())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.i("abcd", "success")
                }, {
                    Log.i("abcd", "error:" + it)
                })
    }

    private fun createAnimations(): ArrayList<Completable> {
        return arrayListOf(
                Completable.create {
                    //TODO: How to do it better or how to remove it?
                    Handler().postDelayed({ it.onComplete() }, 100)
                },
                Completable.create {
                    applyAnimationToRoot(R.layout.splash_fragment_second, changeBoundsTransition(500, AccelerateInterpolator(), it))
                },
                Completable.create {
                    applyAnimationToRoot(R.layout.splash_fragment_third, changeBoundsTransition(2000, AccelerateInterpolator(), it))
                },
                Completable.create {
                    applyAnimationToRoot(R.layout.splash_fragment_fourth, changeBoundsTransition(200, OvershootInterpolator(), it))
                },
                Completable.create {
                    explosion.animate().alpha(0f).withEndAction { it.onComplete() }.duration = 200
                },
                Completable.create {
                    applyAnimationToRoot(R.layout.splash_fragment_end, changeBoundsTransition(300, OvershootInterpolator(), it))
                },
                Completable.create {
                    with(atCs) {
                        visibility = View.VISIBLE
                        alpha = 0f
                        animate().alpha(1f).withEndAction { it.onComplete() }.duration = 5000
                    }
                }
        )
    }

    fun changeBoundsTransition(durationTime: Long, interpolatorType: TimeInterpolator,
                               emitter: CompletableEmitter): ChangeBounds {
        return ChangeBounds().apply {
            duration = durationTime
            interpolator = interpolatorType
            addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    Log.i("abcd", "trans end")
                    emitter.onComplete()
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

    private fun applyAnimationToRoot(nextFrameId: Int, preparedTransition: ChangeBounds) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        ConstraintSet().apply {
            clone(activity, nextFrameId)
        }.applyTo(splash_root)
    }

    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

