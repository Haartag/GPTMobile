package com.llinsoft.gptmobile.domain

import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.GenericIOException
import com.aallam.openai.api.exception.OpenAIServerException
import io.ktor.client.network.sockets.SocketTimeoutException
import java.net.UnknownHostException

class ErrorToTextConverter {
    fun convertTokenException(exception: Throwable): String {
        return when (exception) {
            is AuthenticationException -> "Invalid token. Please check token and try again."
            is UnknownHostException -> "No internet connection. Please enable internet connection and try again."
            is GenericIOException -> "No internet connection. Please enable internet connection and try again."
            is SocketTimeoutException -> "Server down. Please try again later."
            is OpenAIServerException -> "Server down. Please try again later."
            else -> "Unknown error occurred."
        }
    }
}


