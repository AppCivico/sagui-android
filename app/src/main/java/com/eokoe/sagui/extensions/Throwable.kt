package com.eokoe.sagui.extensions

import retrofit2.HttpException
import java.io.IOException

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
val Throwable.friendlyMessage: String
    get() {
        var err = message ?: "Erro inesperado"
        when {
            this is HttpException -> {
                if (code() == 400) {
                    TODO("not implemented")
                } else if (code() == 401 || code() == 403) {
                    TODO("not implemented")
                } else {
                    err = "Erro inesperado"
                }
            }
            this is IOException -> {
                err = "Erro de conexão"
            }
        }
        return err
    }