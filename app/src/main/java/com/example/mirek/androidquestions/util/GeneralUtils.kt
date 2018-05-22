package com.example.mirek.androidquestions.util

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import com.example.mirek.androidquestions.Event
import com.example.mirek.androidquestions.SingleLiveEvent

fun emptyUnit(): () -> Unit = {}

fun <T> Fragment.onWrappedEvent(event: MutableLiveData<Event<T>>, block: (T?) -> Unit) =
        event.also {
            it.observe(this, Observer {
                it?.getContentIfNotHandled()?.let {
                    block(it)
                }
            })
        }

//Unused showcase
fun <T> onSingleLiveEvent(fragment: Fragment, event: SingleLiveEvent<T>, block: (T?) -> Unit) =
        event.also {
            it.observe(fragment, Observer {
                block(it)
            })
        }