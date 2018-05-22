package com.example.mirek.androidquestions.splash

import android.animation.TimeInterpolator
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.example.mirek.androidquestions.Event
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.data.source.DataRepository
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class SplashViewModel(context: Application, repository: DataRepository) : AndroidViewModel(context) {

    var changeBoundsAnimationCommand = MutableLiveData<Event<ChangeBounds>>()
    val fadeExplosionCommand = MutableLiveData<Event<Triple<Float, Long, CompletableEmitter>>>()
    var fadeAtCsCommand = MutableLiveData<Event<Triple<Float, Long, CompletableEmitter>>>()
    var explosionVisibilityChanged = MutableLiveData<Event<Int>>()
    var atCsVisibilityChanged = MutableLiveData<Event<Int>>()
    var setNewConstraintsCommand = MutableLiveData<Event<Int>>()
    var nextPhaseConstraints = MutableLiveData<Event<Int>>()
    var incCompletedAnimationPhases = MutableLiveData<Event<Int>>()

    private var disposables = CompositeDisposable()

    fun startAnimation(completedAnimationPhases: Int) {
        if (disposables.size() == 0) {
            disposables.add(Completable.concat(initialDelay() +
                    splashAnimations().drop(completedAnimationPhases))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {}))
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
                    changeBoundsAnimationCommand.value = Event(changeBoundsTransition(500, AccelerateInterpolator(), it))
                },
                Completable.create {
                    changeBoundsAnimationCommand.value = Event(changeBoundsTransition(2000, AccelerateInterpolator(), it))
                },
                Completable.create {
                    changeBoundsAnimationCommand.value = Event(changeBoundsTransition(200, OvershootInterpolator(), it))
                },
                Completable.create {
                    fadeExplosionCommand.value = Event(Triple(0f, 200L, it))
                    incCompletedAnimationPhases.value = Event(1)
                },
                Completable.create {
                    nextPhaseConstraints.value = Event(1)
                    incCompletedAnimationPhases.value = Event(1)
                    it.onComplete()
                },
                Completable.create {
                    fadeAtCsCommand.value = Event(Triple(1f, 5000L, it))
                    incCompletedAnimationPhases.value = Event(1)
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
                    incCompletedAnimationPhases.value = Event(1)
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

    fun restoreConstraintsInAnimationPhase(completedAnimationPhases: Int) {
        setNewConstraintsCommand.value = Event(getlayoutForPhase(completedAnimationPhases - 1))
    }

    fun restoreViewsVisibilityInAnimationPhase(completedAnimationPhases: Int) {
        when (completedAnimationPhases) {
            4 -> explosionVisibilityChanged.value = Event(View.GONE)
            5, 6 -> atCsVisibilityChanged.value = Event(View.VISIBLE)
        }
    }

    fun syncConstraintsWithoutAnimation(completedAnimationPhases: Int) {
        setNewConstraintsCommand.value = Event(getlayoutForPhase(completedAnimationPhases))
    }

}