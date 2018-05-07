package com.example.mirek.androidquestions.splash

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mirek.androidquestions.R
import kotlinx.android.synthetic.main.splash_fragment_initial.*

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash_fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialConstraints = ConstraintSet()
        initialConstraints.clone(splash_root)

        val finalConstraints = ConstraintSet()
        finalConstraints.clone(activity, R.layout.splash_fragment_second)

        toggleFirstAndSecondSplashState(initialConstraints, finalConstraints)
    }

    private fun toggleFirstAndSecondSplashState(initialConstraints: ConstraintSet, finalConstraints: ConstraintSet) {
        var set = false
        floatingActionButton1.setOnClickListener {
            TransitionManager.beginDelayedTransition(splash_root)
            val constraint = if (set) initialConstraints else finalConstraints
            constraint.applyTo(splash_root)
            set = !set
        }
    }

    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}