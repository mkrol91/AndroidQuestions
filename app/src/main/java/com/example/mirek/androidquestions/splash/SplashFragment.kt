package com.example.mirek.androidquestions.splash

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.SingleLiveEvent
import com.example.mirek.androidquestions.databinding.SplashFragmentInitialEmptyBinding
import kotlinx.android.synthetic.main.splash_fragment_initial_empty.*

class SplashFragment() : Fragment() {

    private lateinit var viewDataBinding: SplashFragmentInitialEmptyBinding
    private var completedAnimationPhases: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.splash_fragment_initial_empty, container, false)
        viewDataBinding = SplashFragmentInitialEmptyBinding.bind(root).apply {
            viewmodel = (activity as SplashActivity).obtainViewModel()
        }

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            completedAnimationPhases = savedInstanceState.get(COMPLETED_ANIMATION_PHASES) as Int
            if (completedAnimationPhases > 0) {
                splashViewModel {
                    restoreConstraintsInAnimationPhase(completedAnimationPhases)
                    restoreViewsVisibilityInAnimationPhase(completedAnimationPhases)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupActionsForFragment()
    }

    override fun onResume() {
        super.onResume()
        splashViewModel {
            startAnimation(completedAnimationPhases)
        }
    }

    private fun setupActionsForFragment() {
        splashViewModel {
            onSingleLiveEvent(setNewConstraintsCommand) {
                syncConstraintsWithLayout(it)
            }
            onSingleLiveEvent(nextPhaseConstraints) {
                syncConstraintsWithLayout(getlayoutForPhase(completedAnimationPhases))
            }
            onSingleLiveEvent(changeBoundsAnimationCommand) {
                syncConstraintWithAnimation(it)
            }
            onSingleLiveEvent(fadeExplosionCommand) {
                if (it != null) {
                    val (alpha, duration, emitter) = it
                    explosion.animate()
                            .alpha(alpha)
                            .setDuration(duration)
                            .withEndAction {
                                //TODO: Shouldn't it go to ViewModel? And if so - how to do this?
                                emitter.onComplete()
                            }
                }
            }
            onSingleLiveEvent(incCompletedAnimationPhases) {
                ++completedAnimationPhases
            }
            onSingleLiveEvent(fadeAtCsCommand) {
                with(atCs) {
                    it?.let {
                        visibility = View.VISIBLE
                        alpha = 0.0f
                        animate().alpha(it.first)
                                .setDuration(it.second)
                    }
                }
            }
            onSingleLiveEvent(explosionVisibilityChanged) {
                if (it != null)
                    explosion.visibility = it
            }
            onSingleLiveEvent(atCsVisibilityChanged) {
                if (it != null)
                    atCs.visibility = it
            }
        }
    }

    private fun syncConstraintsWithLayout(@IdRes layoutId: Int?) {
        if (layoutId != null)
            ConstraintSet().apply {
                splashViewModel {
                    clone(activity, layoutId)
                }
            }.applyTo(splash_root)
    }

    private fun syncConstraintWithAnimation(preparedTransition: ChangeBounds?) {
        if (preparedTransition != null) {
            TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
            viewDataBinding.viewmodel?.syncConstraintsWithoutAnimation(completedAnimationPhases)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COMPLETED_ANIMATION_PHASES, completedAnimationPhases);
    }

    private fun splashViewModel(block: SplashViewModel.() -> Unit) = viewDataBinding.viewmodel?.let(block)

    private fun <T> onSingleLiveEvent(event: SingleLiveEvent<T>, block: (T?) -> Unit): SingleLiveEvent<T>? =
            event.also {
                it.observe(this@SplashFragment, Observer {
                    block(it)
                })
            }

    companion object {
        private val COMPLETED_ANIMATION_PHASES: String = "COMPLETED_ANIMATION_PHASES"

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

}

