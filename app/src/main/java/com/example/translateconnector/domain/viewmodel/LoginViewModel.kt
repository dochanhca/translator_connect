package com.example.translateconnector.domain.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {
    var isPhoneError = mutableStateOf(false)
    var email = mutableStateOf("")
    var password = mutableStateOf("")



}