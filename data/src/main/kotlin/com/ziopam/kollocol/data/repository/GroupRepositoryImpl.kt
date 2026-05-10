package com.ziopam.kollocol.data.repository

import android.content.Context
import android.net.Uri
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.group.CreateGroupRequestDto
import com.ziopam.kollocol.data.datasource.remote.group.GroupApi
import com.ziopam.kollocol.data.datasource.remote.group.GroupMembersRequestDto
import com.ziopam.kollocol.data.datasource.remote.group.UpdateGroupRequestDto
import com.ziopam.kollocol.data.storage.room.GroupDao
import com.ziopam.kollocol.data.storage.room.GroupDetailDao
import com.ziopam.kollocol.data.storage.room.toEntity
import com.ziopam.kollocol.data.storage.room.toGroup
import com.ziopam.kollocol.data.storage.room.toGroupDetail
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.domain.model.GroupDetail
import com.ziopam.kollocol.domain.repository.GroupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: GroupApi,
    private val safeApiCall: SafeApiCall,
    private val groupDao: GroupDao,
    private val groupDetailDao: GroupDetailDao,
    @ApplicationContext private val context: Context
) : GroupRepository {

    override val memberGroups: Flow<List<Group>> = groupDao.getGroupsByType("member").map { entities ->
        entities.map { it.toGroup() }
    }

    override val createdGroups: Flow<List<Group>> = groupDao.getGroupsByType("created").map { entities ->
        entities.map { it.toGroup() }
    }

    override suspend fun syncGroups(): AppResult<Unit> {
        val myResult = safeApiCall.call { api.getGroups("my") }
        val createdResult = safeApiCall.call { api.getGroups("created") }

        if (myResult is AppResult.Err) return AppResult.Err(myResult.error)
        if (createdResult is AppResult.Err) return AppResult.Err(createdResult.error)

        val createdDtos = (createdResult as AppResult.Ok).value.groups
        val createdIds = createdDtos.map { it.id }.toSet()

        val memberEntities = (myResult as AppResult.Ok).value.groups
            .filter { it.id !in createdIds }
            .map { it.toEntity("member") }
        val createdEntities = createdDtos.map { it.toEntity("created") }

        groupDao.syncGroupsByType("member", memberEntities)
        groupDao.syncGroupsByType("created", createdEntities)

        return AppResult.Ok(Unit)
    }

    override suspend fun getInviteeGroups(): AppResult<List<Group>> {
        val result = safeApiCall.call { api.getGroups("invitee") }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.groups.map { it.toDomain() })
            is AppResult.Err -> result
        }
    }

    override suspend fun createGroup(
        name: String,
        description: String,
        avatarUrl: String?,
        memberEmails: List<String>
    ): AppResult<Group> {
        val result = safeApiCall.call {
            api.createGroup(CreateGroupRequestDto(name, description, avatarUrl, memberEmails))
        }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.toDomain())
            is AppResult.Err -> result
        }
    }

    override suspend fun getGroupDetail(id: String): AppResult<GroupDetail> {
        val result = safeApiCall.call { api.getGroupDetail(id) }
        return when (result) {
            is AppResult.Ok -> {
                val detail = result.value.toDomain()
                groupDetailDao.upsert(detail.toEntity())
                AppResult.Ok(detail)
            }
            is AppResult.Err -> {
                if (result.error is AppError.NoInternet) {
                    val cached = groupDetailDao.getById(id)
                    if (cached != null) return AppResult.Ok(cached.toGroupDetail())
                }
                result
            }
        }
    }

    override suspend fun updateGroup(
        id: String,
        name: String,
        description: String,
        avatarUrl: String?
    ): AppResult<Group> {
        val result = safeApiCall.call {
            api.updateGroup(id, UpdateGroupRequestDto(name, description, avatarUrl))
        }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.toDomain())
            is AppResult.Err -> result
        }
    }

    override suspend fun deleteGroup(id: String): AppResult<Unit> {
        return safeApiCall.call { api.deleteGroup(id) }
    }

    override suspend fun inviteMembers(id: String, emails: List<String>): AppResult<Unit> {
        return safeApiCall.call { api.inviteMembers(id, GroupMembersRequestDto(emails)) }
    }

    override suspend fun kickMembers(id: String, emails: List<String>): AppResult<Unit> {
        return safeApiCall.call { api.kickMembers(id, GroupMembersRequestDto(emails)) }
    }

    override suspend fun leaveGroup(id: String): AppResult<Unit> {
        return safeApiCall.call { api.leaveGroup(id) }
    }

    override suspend fun acceptGroupInvite(groupId: String): AppResult<Unit> {
        return safeApiCall.call { api.acceptInvite(groupId) }
    }

    override suspend fun declineGroupInvite(groupId: String): AppResult<Unit> {
        return safeApiCall.call { api.declineInvite(groupId) }
    }

    override suspend fun uploadGroupAvatar(uri: Uri): AppResult<String> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return AppResult.Err(AppError.Unknown("Cannot open image"))

        val bytes = withContext(Dispatchers.IO) {
            inputStream.use { it.readBytes() }
        }

        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val requestBody = bytes.toRequestBody(mimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestBody)

        val result = safeApiCall.call { api.uploadAvatar(part) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.avatarUrl)
            is AppResult.Err -> result
        }
    }
}
