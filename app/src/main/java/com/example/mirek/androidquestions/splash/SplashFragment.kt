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

//        val sixthTransition = customChangeBoundsTransition(500, AccelerateInterpolator(), {})
//        val fifthTransition = customChangeBoundsTransition(5000, AccelerateInterpolator(),
//                {
//                    //  applyAnimationToRoot(animationPhases[4], sixthChangeBounds)
//                })
//        val fourthTransition = customChangeBoundsTransition(1000, AccelerateInterpolator(),
//                {
//                    applyAnimationToRoot(animationPhases[3], fifthTransition)
//                })
//        val thirdTransition = customChangeBoundsTransition(1000, AccelerateInterpolator(),
//                {
//                    applyAnimationToRoot(animationPhases[2], fourthTransition)
//                })
//        val secondTransition = customChangeBoundsTransition(1000, AccelerateInterpolator(),
//                {
//                    applyAnimationToRoot(animationPhases[1], thirdTransition)
//                })

//        val animationPhases = arrayListOf(
//                AnimationPhase(
//                        ConstraintSet().apply {
//                            clone(splash_root)
//                        }, 1000, AccelerateInterpolator()),
//                AnimationPhase(
//                        ConstraintSet().apply {
//                            clone(activity, R.layout.splash_fragment_second)
//                        }, 1000, AccelerateInterpolator()),
//                AnimationPhase(
//                        ConstraintSet().apply {
//                            clone(activity, R.layout.splash_fragment_third)
//                        }, 1000, AccelerateInterpolator()),
//                AnimationPhase(
//                        ConstraintSet().apply {
//                            clone(activity, R.layout.splash_fragment_fourth)
//                        }, 1000, AccelerateInterpolator()),
//                AnimationPhase(
//                        ConstraintSet().apply {
//                            clone(activity, R.layout.splash_fragment_f)
//                        }, 5000, AccelerateInterpolator())
//        )

    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed(Runnable {
            val animation1 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_second)
                }, customChangeBoundsTransition(1000, AccelerateInterpolator(), {
                    Log.i("animationFlow", "anim 1 finished")
                    it.onComplete()
                }))
            }

            val animation2 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_third)
                }, customChangeBoundsTransition(1000, AccelerateInterpolator(), {
                    Log.i("animationFlow", "anim 2 finished")
                    it.onComplete()
                }))
            }

            val animation3 = Completable.create {
                applyAnimationToRoot(ConstraintSet().apply {
                    clone(activity, R.layout.splash_fragment_fourth)
                }, customChangeBoundsTransition(1000, AccelerateInterpolator(), {
                    Log.i("animationFlow", "anim 3 finished")
                    it.onComplete()
                }))
            }

            val animation4 = Completable.create {
                explosion.animate().alpha(0f).withEndAction {
                    it.onComplete()
                    Log.i("animationFlow", "anim 4 finished")
                }
            }

            animation1.andThen(animation2).andThen(animation3).andThen(animation4)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i("abcd", "success")
                    }, {
                        Log.i("abcd", "error:" + it)
                    })

        }, 10)
    }

    class AnimationPhase(var constraintSet: ConstraintSet,
                         var durationTime: Long,
                         var interpolator: TimeInterpolator)

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

