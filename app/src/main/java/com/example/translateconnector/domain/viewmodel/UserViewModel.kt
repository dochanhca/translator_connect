package com.example.translateconnector.domain.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translateconnector.domain.entity.UserEntity
import com.example.translateconnector.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    val userEntity = MutableLiveData<UserEntity>()

    init {
        getUser("123")
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            loading.value = true
            userRepository.getUserDetail(userId).catch { error ->
                Log.e("Network", error.message ?: "")
            }.collect { data ->
                userEntity.value = data
            }
            loading.value = false
        }
    }

}