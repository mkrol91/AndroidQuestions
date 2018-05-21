package com.example.mirek.androidquestions.splash

import android.animation.TimeInterpolator
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Handler
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.util.Log
import android.view.View
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

    var changeBoundsAnimationCommand = SingleLiveEvent<ChangeBounds>()
    var fadeExplosionCommand = SingleLiveEvent<Triple<Float, Long, CompletableEmitter>>()
    var fadeAtCsCommand = SingleLiveEvent<Triple<Float, Long, CompletableEmitter>>()
    var rootConstraintsChangedCommand = SingleLiveEvent<Int>()
    var explosionVisibilityChanged = SingleLiveEvent<Int>()
    var atCsVisibilityChanged = SingleLiveEvent<Int>()
    var setNewConstraintsCommand = SingleLiveEvent<Int>()
    var nextPhaseConstraints = SingleLiveEvent<Nothing>()
    var incCompletedAnimationPhases = SingleLiveEvent<Nothing>()

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
                        R.layout.splash_fragment_explosion,
                        R.layout.splash_fragment_initial_empty,
                        R.layout.splash_fragment_initial_empty)

        if (animationPhase < animationPhases.size) {
            return animationPhases[animationPhase]
        } else {
            return animationPhases.last()
        }
    }

    private fun splashAnimations(): ArrayList<Completable> {
        return arrayListOf(
                Completable.create {
                    Log.i("animationFlowDebug", "animate to splash_fragment_only_title")
                    changeBoundsAnimationCommand.value = changeBoundsTransition(500, AccelerateInterpolator(), it)
                },
                Completable.create {
                    Log.i("animationFlowDebug", "animate to splash_fragment_rocket")
                    changeBoundsAnimationCommand.value = changeBoundsTransition(2000, AccelerateInterpolator(), it)
                },
                Completable.create {
                    Log.i("animationFlowDebug", "animate to splash_fragment_explosion")
                    changeBoundsAnimationCommand.value = changeBoundsTransition(200, OvershootInterpolator(), it)
                },
                Completable.create {
                    Log.i("animationFlowDebug", "fade explosion animation")
                    fadeExplosionCommand.value = Triple(0f, 200, it)
                    incCompletedAnimationPhases.call()
                },
                Completable.create {
                    Log.i("animationFlowDebug", "change constraints to splash_fragment_init phase")
                    nextPhaseConstraints.call()
                    it.onComplete()
                },
                Completable.create {
                    Log.i("animationFlowDebug", "showing @CS animation")
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
                    Log.i("animationFlowDebug", "transitionResume: " + transition.duration)
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

    fun restoreConstraintsInAnimationPhase(completedAnimationPhases: Int) {
        rootConstraintsChangedCommand.value = getlayoutForPhase(completedAnimationPhases - 1)
    }

    fun restoreViewsVisibilityInAnimationPhase(completedAnimationPhases: Int) {
        when (completedAnimationPhases) {
            4 -> explosionVisibilityChanged.value = View.GONE
            5, 6 -> atCsVisibilityChanged.value = View.VISIBLE
        }
    }

    fun syncConstraintsWithoutAnimation(completedAnimationPhases: Int) {
        setNewConstraintsCommand.value = getlayoutForPhase(completedAnimationPhases)
    }

}