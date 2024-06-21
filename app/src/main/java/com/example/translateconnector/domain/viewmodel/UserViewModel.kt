package com.example.translateconnector.domain.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translateconnector.data.network.CommonError
import com.example.translateconnector.domain.entity.UserEntity
import com.example.translateconnector.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    val loading = MutableLiveData<Boolean>()
    var userEntity = MutableLiveData<UserEntity>()

    fun getUser(userId: String): Unit {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loading.value = true
                userRepository.getUserDetail(userId).collect{
                    data -> userEntity.value = data!!
                }
            } catch (error: CommonError) {
                //TODO handle error
            } finally {
                loading.value = false
            }
        }
    }

}