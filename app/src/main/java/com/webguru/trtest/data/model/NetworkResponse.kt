package com.webguru.trtest.data.model

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface NetworkError {
    data class Http(val code: Int, val body: String?) : NetworkError
    object NetworkUnavailable : NetworkError
    object Timeout : NetworkError
    data class Unknown(val cause: Throwable) : NetworkError
}

suspend fun <T> safe(call: suspend () -> T): Result<T> = try {
    Result.success(call())
} catch (e: HttpException) {
    Result.failure(RuntimeException(NetworkError.Http(e.code(), e.response()?.errorBody()?.string()).toString()))
} catch (e: SocketTimeoutException) {
    Result.failure(RuntimeException(NetworkError.Timeout.toString()))
} catch (e: IOException) {
    Result.failure(RuntimeException(NetworkError.NetworkUnavailable.toString()))
} catch (e: Throwable) {
    Result.failure(RuntimeException(NetworkError.Unknown(e).toString()))
}
