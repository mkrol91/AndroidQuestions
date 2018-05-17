package com.example.mirek.androidquestions.splash

import android.animation.TimeInterpolator
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Handler
import android.support.annotation.IdRes
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.SingleLiveEvent
import com.example.mirek.androidquestions.data.source.DataRepository
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class SplashViewModel(context: Application, repository: DataRepository) : AndroidViewModel(context) {

    var changeBoundsAnimationCommand = SingleLiveEvent<Pair<Int, ChangeBounds>>()
    var fadeExplosionCommand = SingleLiveEvent<Triple<Float, Long, CompletableEmitter>>()
    var fadeAtCsCommand = SingleLiveEvent<Triple<Float, Long, CompletableEmitter>>()
    var changeConstraintsCommand = SingleLiveEvent<Pair<@IdRes Int, CompletableEmitter>>()

    private var disposables = CompositeDisposable()

    fun startAnimation(completedAnimationPhases: Int) {
        if (disposables.size() == 0) {
            disposables.add(Completable.concat(initialDelay() +
                    splashAnimations().drop(completedAnimationPhases))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i("abcd", "success")
                    }, {
                        Log.i("abcd", "error:" + it)
                    }))
        }
    }

    private fun initialDelay(): ArrayList<Completable> {
        return arrayListOf(
                Completable.create {
                    //TODO: How to do it better or how to remove it?
                    Handler().postDelayed({ it.onComplete() }, 100)
                }
        )
    }

    fun getlayoutForPhase(animationPhase: Int): Int {
        val animationPhases =
                arrayListOf(R.layout.splash_fragment_only_title,
                        R.layout.splash_fragment_rocket,
                        R.layout.splash_fragment_explosion,
                        R.layout.splash_fragment_initial_empty)

        if (animationPhase < animationPhases.size) {
            return animationPhases[animationPhase]
        } else {
            return R.layout.splash_fragment_explosion
        }
    }

    private fun splashAnimations(): ArrayList<Completable> {
        return arrayListOf(
                Completable.create {
                    Log.i("animationFlowDebug", "animate to splash_fragment_only_title")
                    changeBoundsAnimationCommand.value = Pair(getlayoutForPhase(0),
                            changeBoundsTransition(500, AccelerateInterpolator(), it))
                },
                Completable.create {
                    Log.i("animationFlowDebug", "animate to splash_fragment_rocket")
                    changeBoundsAnimationCommand.value = Pair(getlayoutForPhase(1),
                            changeBoundsTransition(2000, AccelerateInterpolator(), it))
                },
                Completable.create {
                    Log.i("animationFlowDebug", "animate to splash_fragment_explosion")
                    changeBoundsAnimationCommand.value = Pair(getlayoutForPhase(2),
                            changeBoundsTransition(200, OvershootInterpolator(), it))
                },
                Completable.create {
                    Log.i("animationFlowDebug", "fade explosion animation")
                    fadeExplosionCommand.value = Triple(0f, 200, it)
                },
                Completable.create {
                    Log.i("animationFlowDebug", "@CS animation" + getlayoutForPhase(3))
                    changeConstraintsCommand.value = Pair(getlayoutForPhase(3),it)


//                    fadeExplosionCommand.value = Triple(R.layout.splash_fragment_initial_empty,)
                    //Triple(0f)
//
//

                },
                Completable.create {
                    fadeAtCsCommand.value = Triple(1f, 5000, it)
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun changeBoundsTransition(durationTime: Long, interpolatorType: TimeInterpolator,
                               emitter: CompletableEmitter): ChangeBounds {
        return ChangeBounds().apply {
            duration = durationTime
            interpolator = interpolatorType
            addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    emitter.onComplete()
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

}