package com.example.mirek.androidquestions.splash

import android.arch.lifecycle.Observer
import android.os.Bundle
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
        if (savedInstanceState != null) {
            completedAnimationPhases = savedInstanceState.get(COMPLETED_ANIMATION_PHASES) as Int
            if (completedAnimationPhases > 0) {
                viewDataBinding.viewmodel?.run {
                    val layout = getlayoutForPhase(completedAnimationPhases - 1)
                    //when user rotate screen during explosion, after rotation it should'n appear

                    ConstraintSet().apply {
                        Log.i("animationFlowDebug", "applying constraints from layout [ " + layout + " ]" + " (view restored)")
                        clone(activity, layout)
                    }.applyTo(splash_root)
                    if (layout == R.layout.splash_fragment_explosion) {
                        explosion.visibility = View.GONE
                    }
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
            it.changeBoundsAnimationCommand.observe(this, Observer {
                it?.let {
                    val (layoutId, changeBounds) = it
                    applyAnimationToRoot(changeBounds)
                    ++completedAnimationPhases;
                }
            })

            it.fadeAnimationCommand.observe(this, Observer {
                it?.let {
                    val (alpha, duration, emitter) = it
                    explosion.animate()
                            .alpha(alpha)
                            .setDuration(duration)
                            .withEndAction {
                                //TODO: Shouldn't it go to ViewModel? And if so - how to do this?
                                emitter.onComplete()
                            }
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COMPLETED_ANIMATION_PHASES, completedAnimationPhases);
    }

    private fun applyAnimationToRoot(preparedTransition: ChangeBounds) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        ConstraintSet().apply {
            viewDataBinding.viewmodel?.run {
                clone(activity, getlayoutForPhase(completedAnimationPhases))
            }
        }.applyTo(splash_root)
    }

    companion object {
        private val COMPLETED_ANIMATION_PHASES: String = "COMPLETED_ANIMATION_PHASES"

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

