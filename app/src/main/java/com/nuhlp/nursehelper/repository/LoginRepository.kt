package com.nuhlp.nursehelper.repository

import com.nuhlp.nursehelper.datasource.datastore.DataStoreKey
import com.nuhlp.nursehelper.datasource.datastore.LoginDataStore
import com.nuhlp.nursehelper.datasource.room.user.UserAccount
import com.nuhlp.nursehelper.datasource.room.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
class LoginRepository(private val dataStore: LoginDataStore,private val room: UserDatabase) {

    val isLogin: Flow<Boolean> = dataStore.getPreferenceFlow(DataStoreKey.IS_LOGIN)
    val isAgreeTerms: Flow<Boolean> = dataStore.getPreferenceFlow(DataStoreKey.IS_AGREE_TERMS)


    suspend fun setIsLoginToDataStore(value: Boolean){
        withContext(Dispatchers.IO) {
            dataStore.saveToPreferencesStore(value,DataStoreKey.IS_LOGIN)
        }
    }

    suspend fun setTermsToDataStore(value: Boolean){
        withContext(Dispatchers.IO) {
            dataStore.saveToPreferencesStore(value,DataStoreKey.IS_AGREE_TERMS)
        }
    }

    suspend fun setUserToDatabase(user: UserAccount){
        withContext(Dispatchers.IO) {
            room.userDao.setUser(user)
        }
    }

    fun getAvailableId(userId: String):Flow<Boolean>{
        return  room.userDao.getAvailableId(userId).map { asBool(it) }
    }

    suspend fun validUser(user : UserAccount) :Boolean = withContext(Dispatchers.IO){
        asBool(room.userDao.countExistedUser(user.id,user.pw))
    }

    private fun asBool(resultInt: Int):Boolean{
        return  when(resultInt){
            1 -> true
            else -> false
        }
    }







}

