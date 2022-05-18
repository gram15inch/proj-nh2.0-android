package com.nuhlp.nursehelper



import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.MutableLiveData
import com.nuhlp.nursehelper.data.DataStore
import com.nuhlp.nursehelper.data.DataStoreImpl
import com.nuhlp.nursehelper.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks


import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class LoginRepositoryTest {


    @Mock
    lateinit var mockDataStore: DataStore

    lateinit var loginRepository: LoginRepository

    @Before
    fun setUp(){

    }

    @Test
    fun setIsLoginToLiveData() {
        runBlocking {
            `when`(mockDataStore.preferenceFlow).thenReturn(flow { emit(true) })
            `when`(mockDataStore.IS_LOGIN).thenReturn(booleanPreferencesKey("is_login"))

            loginRepository = LoginRepository(mockDataStore)
            loginRepository.setIsLoginToLiveData(true)
            verify(mockDataStore, times(1)).saveIsLoginToPreferencesStore(any(Boolean::javaClass))

        }

    }




}
