package com.ziopam.kollocol.data.datasource.remote.group

import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApi {
    @POST("groups/{id}/accept")
    suspend fun acceptInvite(@Path("id") groupId: String)

    @POST("groups/{id}/decline")
    suspend fun declineInvite(@Path("id") groupId: String)
}
