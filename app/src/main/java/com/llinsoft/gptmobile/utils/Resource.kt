package com.llinsoft.gptmobile.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    //object Loading : Resource<Nothing>()
    data class Error<out T>(val exception: Throwable) : Resource<T>()
}