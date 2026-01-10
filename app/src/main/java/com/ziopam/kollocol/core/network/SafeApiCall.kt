package com.ziopam.kollocol.core.network

import com.google.gson.Gson
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.data.datasource.remote.ApiErrorResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafeApiCall @Inject constructor(
    private val gson: Gson
) {
    suspend fun <T> call(block: suspend () -> T): AppResult<T> {
        return try {
            AppResult.Ok(block())
        } catch (e: HttpException) {
            mapHttp(e)
        } catch (_: SocketTimeoutException) {
            AppResult.Err(AppError.Timeout)
        } catch (_: IOException) {
            AppResult.Err(AppError.NoInternet)
        } catch (t: Throwable) {
            AppResult.Err(AppError.Unknown(t.message))
        }
    }

    private fun <T> mapHttp(e: HttpException): AppResult<T> {
        val code = e.code()

        return when (code) {
            401 -> AppResult.Err(AppError.Unauthorized)
            403 -> AppResult.Err(AppError.Forbidden)
            else -> {
                val parsed = parseError(e)
                AppResult.Err(
                    AppError.Http(
                        code = code,
                        error = parsed?.error,
                        message = parsed?.message
                    )
                )
            }
        }
    }

    private fun parseError(e: HttpException): ApiErrorResponse? {
        val body = e.response()?.errorBody()?.string() ?: return null
        return try {
            gson.fromJson(body, ApiErrorResponse::class.java)
        } catch (_: Exception) {
            null
        }
    }
}
