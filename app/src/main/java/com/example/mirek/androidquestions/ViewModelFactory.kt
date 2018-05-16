package com.example.mirek.androidquestions

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.mirek.androidquestions.data.Injection
import com.example.mirek.androidquestions.data.source.DataRepository
import com.example.mirek.androidquestions.splash.SplashViewModel

class ViewModelFactory private constructor(private val application: Application,
                                           private val repository: DataRepository) : ViewModelProvider.NewInstanceFactory() {


    //Injecting parameters to viewmodel during creation
    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(SplashViewModel::class.java) ->
                        SplashViewModel(application, repository)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null //writes to this field are immediately made visible to other threads.

        fun getInstance(application: Application) = INSTANCE
                ?: synchronized(ViewModelFactory::class.java) {
                    //todo: why synchronized?
                    INSTANCE //todo: why another check?
                            ?: ViewModelFactory(application, Injection.provideDataRepository(application.applicationContext))
                                    .also { INSTANCE = it }
                }
    }

}