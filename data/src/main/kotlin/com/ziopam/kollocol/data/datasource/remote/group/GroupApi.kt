package com.ziopam.kollocol.data.datasource.remote.group

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupApi {
    @GET("groups")
    suspend fun getGroups(@Query("filter") filter: String = "my"): GetGroupsResponseDto

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequestDto): GroupDto

    @GET("groups/{id}")
    suspend fun getGroupDetail(@Path("id") groupId: String): GroupWithMembersDto

    @PUT("groups/{id}")
    suspend fun updateGroup(@Path("id") groupId: String, @Body request: UpdateGroupRequestDto): GroupDto

    @DELETE("groups/{id}")
    suspend fun deleteGroup(@Path("id") groupId: String)

    @POST("groups/{id}/invite")
    suspend fun inviteMembers(@Path("id") groupId: String, @Body request: GroupMembersRequestDto)

    @POST("groups/{id}/kick")
    suspend fun kickMembers(@Path("id") groupId: String, @Body request: GroupMembersRequestDto)

    @POST("groups/{id}/leave")
    suspend fun leaveGroup(@Path("id") groupId: String)

    @POST("groups/{id}/accept")
    suspend fun acceptInvite(@Path("id") groupId: String)

    @POST("groups/{id}/decline")
    suspend fun declineInvite(@Path("id") groupId: String)

    @Multipart
    @POST("groups/avatar/upload")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): GroupAvatarUploadResponseDto
}
