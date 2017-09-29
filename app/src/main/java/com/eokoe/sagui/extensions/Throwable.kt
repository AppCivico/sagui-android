package com.eokoe.sagui.extensions

import com.eokoe.sagui.data.exceptions.SaguiException
import retrofit2.HttpException
import java.io.IOException

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
val Throwable.errorType: ErrorType
    get() {
        return when {
            this is HttpException -> {
                if (code() == 400) {
                    ErrorType.BAD_REQUEST
                } else if (code() == 401 || code() == 403) {
                    ErrorType.UNAUTHORIZED
                } else {
                    ErrorType.UNEXPECTED
                }
            }
            this is IOException -> {
                ErrorType.CONNECTION
            }
            this is SaguiException -> {
                ErrorType.CUSTOM
            }
            else -> ErrorType.UNEXPECTED
        }
    }

val Throwable.friendlyMessage: String
    get() {
        return when(errorType) {
            ErrorType.CONNECTION -> "Erro de conexão"
//            ErrorType.BAD_REQUEST -> TODO("not implemented")
//            ErrorType.UNAUTHORIZED -> TODO("not implemented")
            ErrorType.CUSTOM -> message!!
            ErrorType.UNEXPECTED -> "Erro inesperado"
            else -> message ?: "Erro inesperado"
        }
    }

enum class ErrorType {
    CONNECTION, BAD_REQUEST, UNAUTHORIZED, UNEXPECTED, CUSTOM
}