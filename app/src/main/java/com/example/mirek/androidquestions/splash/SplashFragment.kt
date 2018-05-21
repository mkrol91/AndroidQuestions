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
import io.reactivex.CompletableEmitter
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
            setNewConstraints()
            nextPhaseConstraints()
            changeBoundsAnimationCommand()
            fadeExplosionCommand()
            incCompletedAnimationPhases()
            fadeAtCsCommand()
            explosionVisibilityChanged()
            atCsVisibilityChanged()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COMPLETED_ANIMATION_PHASES, completedAnimationPhases);
    }

    private fun syncConstraintWithAnimation(preparedTransition: ChangeBounds?) {
        if (preparedTransition != null) {
            TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
            viewDataBinding.viewmodel?.syncConstraintsWithoutAnimation(completedAnimationPhases)
        }
    }

    private fun SplashViewModel.atCsVisibilityChanged() {
        atCsVisibilityChanged {
            atCs.visibility = it
        }
    }

    private fun SplashViewModel.explosionVisibilityChanged() {
        explosionVisibilityChanged {
            explosion.visibility = it
        }
    }

    private fun SplashViewModel.fadeAtCsCommand() {
        fadeAtCsCommand {
            with(atCs) {
                visibility = View.VISIBLE
                alpha = 0.0f
                animate().alpha(it.first)
                        .setDuration(it.second)
            }
        }
    }

    private fun SplashViewModel.fadeExplosionCommand() {
        fadeExplosionCommand {
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
    }

    private fun SplashViewModel.changeBoundsAnimationCommand() {
        changeBoundsAnimationCommand {
            syncConstraintWithAnimation(it)
        }
    }

    private fun SplashViewModel.nextPhaseConstraints() {
        nextPhaseConstraints {
            syncConstraintsWithLayout(getlayoutForPhase(completedAnimationPhases))
        }
    }

    private fun SplashViewModel.setNewConstraints() {
        setNewConstraintsCommand {
            syncConstraintsWithLayout(it)
        }
    }

    private fun SplashViewModel.incCompletedAnimationPhases() {
        incCompletedAnimationPhases {
            ++completedAnimationPhases
        }
    }

    companion object {
        private val COMPLETED_ANIMATION_PHASES: String = "COMPLETED_ANIMATION_PHASES"

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

    fun splashViewModel(block: SplashViewModel.() -> Unit) = viewDataBinding.viewmodel?.let(block)

    private fun SplashViewModel.setNewConstraintsCommand(block: (Int?) -> Unit): SingleLiveEvent<Int>? = setNewConstraintsCommand.also {
        it.observe(this@SplashFragment, Observer {
            block(it)
        })
    }

    private fun SplashViewModel.nextPhaseConstraints(block: () -> Unit): SingleLiveEvent<Nothing>? = nextPhaseConstraints.also {
        it.observe(this@SplashFragment, Observer {
            block()
        })
    }

    private fun SplashViewModel.changeBoundsAnimationCommand(block: (ChangeBounds?) -> Unit) = changeBoundsAnimationCommand.also {
        it.observe(this@SplashFragment, Observer {
            block(it)
        })
    }

    private fun SplashViewModel.fadeExplosionCommand(block: (Triple<Float, Long, CompletableEmitter>?) -> Unit) = fadeExplosionCommand.also {
        it.observe(this@SplashFragment, Observer {
            block(it)
        })
    }

    fun SplashViewModel.fadeAtCsCommand(block: (Triple<Float, Long, CompletableEmitter>) -> Unit) = fadeAtCsCommand.also {
        it.observe(this@SplashFragment, Observer {
            if (it != null) {
                block(it)
            }
        })
    }

    private fun SplashViewModel.explosionVisibilityChanged(block: (Int) -> Unit) = explosionVisibilityChanged.also {
        it.observe(this@SplashFragment, Observer {
            if (it != null) {
                block(it)
            }
        })
    }

    private fun SplashViewModel.atCsVisibilityChanged(block: (Int) -> Unit) = atCsVisibilityChanged.also {
        it.observe(this@SplashFragment, Observer {
            if (it != null) {
                block(it)
            }
        })
    }

    private fun SplashViewModel.incCompletedAnimationPhases(block: () -> Unit) = incCompletedAnimationPhases.also {
        it.observe(this@SplashFragment, Observer {
            block()
        })
    }
}

