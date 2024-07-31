package com.example.translateconnector.domain.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translateconnector.domain.entity.FirebaseAuthEntity
import com.example.translateconnector.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {
    var isPhoneError = mutableStateOf(false)
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    var loading = mutableStateOf(false)
    val firebaseUser = MutableLiveData<FirebaseAuthEntity>()

    fun login() {
        viewModelScope.launch {
            loading.value = true
            loginRepository.login(email.value, password.value).catch { error ->
                //TODO handle error
            }.collect { data ->
                firebaseUser.value = data
            }
            loading.value = false
        }
    }


}