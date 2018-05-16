package com.example.mirek.androidquestions.splash

import android.animation.TimeInterpolator
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Handler
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

    var animationPhaseCompletedCommand = SingleLiveEvent<Pair<Int, ChangeBounds>>()

    private var disposables = CompositeDisposable()


    fun startAnimation(currentLayoutId: Int = -1, explosionId: Int = -1, atCsId: Int = -1) {
        if (disposables.size() == 0) {
            disposables.add(Completable.concat(provideAnimations(currentLayoutId, explosionId, atCsId))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i("abcd", "success")
                    }, {
                        Log.i("abcd", "error:" + it)
                    }))
        }
    }

    //View model hold UI data for the activity, activity is only respon for how to draw data on screen

    private fun provideAnimations(currentLayoutId: Int, explosionId: Int, atCsId: Int):
            ArrayList<Completable> {
        return arrayListOf(
                Completable.create {
                    //TODO: How to do it better or how to remove it?
                    Handler().postDelayed({ it.onComplete() }, 100)
                },
                Completable.create {
                    when (currentLayoutId) {
                        R.layout.splash_fragment_only_title, -1 ->
                            animationPhaseCompletedCommand.value = Pair(R.layout.splash_fragment_only_title,
                                    changeBoundsTransition(500, AccelerateInterpolator(), it))
                        else -> it.onComplete()
                    }
                },
                Completable.create {
                    when (currentLayoutId) {
                        R.layout.splash_fragment_rocket, -1 ->
                            animationPhaseCompletedCommand.value = Pair(R.layout.splash_fragment_rocket,
                                    changeBoundsTransition(2000, AccelerateInterpolator(), it))
                        else -> it.onComplete()
                    }
                },
                Completable.create {
                    when (currentLayoutId) {
                        R.layout.splash_fragment_explosion, -1 ->
                            animationPhaseCompletedCommand.value = Pair(R.layout.splash_fragment_explosion,
                                    changeBoundsTransition(200, OvershootInterpolator(), it))
                        else -> it.onComplete()
                    }
                },
                Completable.create {
                    //                    changeBoundsAnimation.value = AnimationPhases.EXPLOSION_HIDE
//
//
//                    explosion.animate().alpha(0f).withEndAction { it.onComplete() }.duration = 200
//
//
//                    changeBoundsAnimation.value = Pair(R.layout.splash_fragment_explosion,
//                            changeBoundsTransition(200, OvershootInterpolator(), it))
                },
                Completable.create {
                    //                    changeBoundsAnimation.value = AnimationPhases.FINAL_TITLE
//
//
//
////                    with(atCs) {
////                        visibility = android.view.View.VISIBLE
////                        alpha = 0f
////                        animate().alpha(1f).withEndAction { it.onComplete() }.duration = 5000
////                    }
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

}