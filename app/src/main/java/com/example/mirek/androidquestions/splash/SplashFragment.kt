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
    private var currentLayoutId: Int = R.layout.splash_fragment_initial_empty;

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
            currentLayoutId = savedInstanceState.get(CURRENT_LAYOUT) as Int
            ConstraintSet().apply {
                clone(activity, currentLayoutId)
            }.applyTo(splash_root)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupAnimations()

        viewDataBinding.viewmodel?.run {
            savedInstanceState?.run {
                startAnimation(currentLayoutId, explosion.id, atCs.id)
            } ?: startAnimation()
        }
    }


    private fun setupAnimations() {
        viewDataBinding.viewmodel?.let {
            it.animationPhaseCompletedCommand.observe(this, Observer {
                it?.let {
                    val (layoutId, changeBounds) = it
                    currentLayoutId = layoutId
                    Log.i("animPhases", "layout id: $currentLayoutId")
                    applyAnimationToRoot(currentLayoutId, changeBounds)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_LAYOUT, currentLayoutId);
    }

    private fun applyAnimationToRoot(nextFrameId: Int, preparedTransition: ChangeBounds) {
        TransitionManager.beginDelayedTransition(splash_root, preparedTransition)
        ConstraintSet().apply {
            clone(activity, nextFrameId)
        }.applyTo(splash_root)
    }

    companion object {

        private const val CURRENT_LAYOUT = "CURRENT_LAYOUT"

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}

