package com.ziopam.kollocol.core.network

import com.google.gson.Gson
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.model.ApiErrorResponse
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
        } catch (t: Exception) {
            println("SafeApiCall Unknown error: ${t.message}")
            AppResult.Err(AppError.Unknown(t.message))
        }
    }

    private fun <T> mapHttp(e: HttpException): AppResult<T> {
        val code = e.code()

        return when (code) {
            400 -> AppResult.Err(AppError.BadRequest(parseError(e)?.message))
            401 -> AppResult.Err(AppError.Unauthorized)
            403 -> AppResult.Err(AppError.Forbidden)
            404 -> AppResult.Err(AppError.NotFound)
            429 -> AppResult.Err(AppError.TooManyRequests)
            500 -> AppResult.Err(AppError.ServerError)
            else -> {
                val parsed = parseError(e)
                AppResult.Err(
                    AppError.Unknown(
                        parsed?.message ?: "HTTP $code"
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
