package com.nuhlp.nursehelper.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.nuhlp.nursehelper.datasource.LoginDataStoreImpl
import com.nuhlp.nursehelper.datasource.room.user.UserAccount
import com.nuhlp.nursehelper.datasource.room.user.getUserDatabase
import com.nuhlp.nursehelper.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class LoginViewModel(application: Application) : AndroidViewModel(application){
    private val loginRepository = LoginRepository(LoginDataStoreImpl(application),
        getUserDatabase(application)
    )
    val isLogin : LiveData<Boolean> = loginRepository.isLogin.asLiveData()
    val isAgreeTerm : LiveData<Boolean> = loginRepository.isAgreeTerms.asLiveData()

    var ID=MutableLiveData("")
    var PW=MutableLiveData("")

    fun loginSuccess(){
        viewModelScope.launch{
            loginRepository.setIsLoginToDataStore(true)
        }
    }
    fun loginFail(){
        viewModelScope.launch{
            loginRepository.setIsLoginToDataStore(false)
        }
    }
    fun agreeTerms(){
        viewModelScope.launch {
            loginRepository.setTermsToDataStore(true)
        }
    }

    fun getAvailableId(userId :String): Flow<Boolean> = loginRepository.getAvailableId(userId)

   fun setUser()= CoroutineScope(Dispatchers.IO).launch {
       Log.d("LoginViewModel","call setUser")
       val user = createUser(ID.value!!,PW.value!!)
       Log.d("LoginViewModel","create user : ${user}")

       if(user.isValid()){
           loginRepository.setUserToDatabase(user)
           //propertyReset()
       }
       else
           throw java.lang.IllegalArgumentException("user not Valid :${user.id}/${user.pw}/${user.registrationDate}")
    }

    private fun createUser(id: String, pw: String): UserAccount {
        val today = UserAccount.asFormattedDate(Calendar.getInstance())
        return UserAccount(id, pw, today)
    }


    fun propertyReset(){
        ID.value = ""
        PW.value = ""
    }

    suspend fun validUser(id: String, pw: String): Boolean {
        return loginRepository.validUser(createUser(id,pw))
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}