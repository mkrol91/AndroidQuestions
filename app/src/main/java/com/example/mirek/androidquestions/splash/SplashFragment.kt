package com.example.mirek.androidquestions.splash

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.databinding.SplashFragmentInitialEmptyBinding
import kotlinx.android.synthetic.main.splash_fragment_initial_empty.*

class SplashFragment() : Fragment() {

    private lateinit var viewDataBinding: SplashFragmentInitialEmptyBinding
    private var completedAnimationPhases: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.splash_fragment_initial_empty, container, false)
        Log.i("animationFlowDebug", "---animation layout ids------------------------------------------------------------------------------------------------------------------------")
        Log.i("animationFlowDebug", "splash_fragment_initial_empty: " + R.layout.splash_fragment_initial_empty)
        Log.i("animationFlowDebug", "splash_fragment_only_title: " + R.layout.splash_fragment_only_title)
        Log.i("animationFlowDebug", "splash_fragment_rocket: " + R.layout.splash_fragment_rocket)
        Log.i("animationFlowDebug", "splash_fragment_explosion: " + R.layout.splash_fragment_explosion)
        Log.i("animationFlowDebug", "--------------------------")
        viewDataBinding = SplashFragmentInitialEmptyBinding.bind(root).apply {
            viewmodel = (activity as SplashActivity).obtainViewModel()
        }

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("animationFlowDebug", "completedAnimationPhases: " + completedAnimationPhases)
        if (savedInstanceState != null) {
            completedAnimationPhases = savedInstanceState.get(COMPLETED_ANIMATION_PHASES) as Int
            if (completedAnimationPhases > 0) {
                viewDataBinding.viewmodel?.run {
                    restoreConstraintsInAnimationPhase(completedAnimationPhases)
                    restoreViewsVisibilityInAnimationPhase(completedAnimationPhases)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupAnimations()
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.run {
            Log.i("animationFlowDebug", "startAnimation, completedAnimationPhases: " + completedAnimationPhases)
            startAnimation(completedAnimationPhases)
        }
    }

    private fun setupAnimations() {
        viewDataBinding.viewmodel?.let {
            it.setNewConstraintsCommand.observe(this, Observer {
                it?.let {
                    syncConstraintsWithLayout(it)
                }
            })
            it.nextPhaseConstraints.observe(this, Observer {
                viewDataBinding.viewmodel?.run {
                    syncConstraintsWithLayout(getlayoutForPhase(completedAnimationPhases))
                }
            })
            it.changeBoundsAnimationCommand.observe(this, Observer {
                it?.let {
                    syncConstraintWithAnimation(it)
                    ++completedAnimationPhases;
                }
            })
            it.fadeExplosionCommand.observe(this, Observer {
                it?.let {
                    val (alpha, duration, emitter) = it
                    explosion.animate()
                            .alpha(alpha)
                            .setDuration(duration)
                            .withEndAction {
                                //TODO: Shouldn't it go to ViewModel? And if so - how to do this?
                                emitter.onComplete()
                            }
                    ++completedAnimationPhases
                }
            })
            it.fadeAtCsCommand.observe(this, Observer {
                it?.let {
                    with(atCs) {
                        visibility = android.view.View.VISIBLE
                        alpha = 0.0f
                        animate().alpha(it.first)
                                .setDuration(it.second)
                        ++completedAnimationPhases
                    }
                }
            })
            it.rootConstraintsChangedCommand.observe(this, Observer {
                it?.let {
                    ConstraintSet().apply {
                        Log.i("animationFlowDebug", "applying constraints from layout [ " + it + " ]" + " (view restored)")
                        clone(activity, it)
                    }.applyTo(splash_root)
                }
            })
            it.explosionVisibilityChanged.observe(this, Observer {
                //TODO: replace with databinding ? probably not...
                it?.let {
                    explosion.visibility = it
                }
            })
            it.atCsVisibilityChanged.observe(this, Observer {
                //TODO: replace with databinding ? probably not...
                it?.let {
                    atCs.visibility = it
                }
            })

        }
    }

    private fun syncConstraintsWithLayout(@IdRes layoutId: Int) {
        ConstraintSet().apply {
            viewDataBinding.viewmodel?.run {
                clone(activity, layoutId)
            }
        }.applyTo(splash_root)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COMPLETED_ANIMATION_PHASES, completedAnimationPhases);
    }

    private fun syncConstraintWithAnimation(preparedTransition: ChangeBounds) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        viewDataBinding.viewmodel?.syncConstraintsWithoutAnimation(completedAnimationPhases)
    }

    companion object {
        private val COMPLETED_ANIMATION_PHASES: String = "COMPLETED_ANIMATION_PHASES"

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

