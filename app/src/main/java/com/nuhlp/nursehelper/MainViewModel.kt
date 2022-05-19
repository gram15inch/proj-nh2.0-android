package com.nuhlp.nursehelper

import android.app.Application
import androidx.lifecycle.*
import com.nuhlp.nursehelper.data.DataStoreImpl
import com.nuhlp.nursehelper.repository.LoginRepository
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel (application: Application) : AndroidViewModel(application) {


    private val loginRepository = LoginRepository(DataStoreImpl(application))

    val isLogin : LiveData<Boolean> = loginRepository.isLogin.asLiveData()

    fun loginSuccess(){
        viewModelScope.launch{
            loginRepository.setIsLoginToLiveData(true)
        }
    }

    fun loginFail(){
        viewModelScope.launch{
            loginRepository.setIsLoginToLiveData(false)
        }
    }
/*
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        refreshDataFromRepository()
    }*/
/*
    private fun refreshDataFromRepository() {
  viewModelScope.launch {
            try {
                videosRepository.refreshVideos()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(playlist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }

    }*/

/**
     * Resets the network error flag.
     */

   /* fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }
*/
    class Factory(val app: Application) : ViewModelProvider.Factory {
       override fun <T : ViewModel> create(modelClass: Class<T>): T {
           if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
               @Suppress("UNCHECKED_CAST")
               return MainViewModel(app) as T
           }
           throw IllegalArgumentException("Unable to construct viewmodel")
       }
    }

}