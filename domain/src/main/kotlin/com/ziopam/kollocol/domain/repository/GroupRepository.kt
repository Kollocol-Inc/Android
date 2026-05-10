package com.ziopam.kollocol.domain.repository

import android.net.Uri
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.domain.model.GroupDetail
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    val memberGroups: Flow<List<Group>>
    val createdGroups: Flow<List<Group>>
    suspend fun syncGroups(): AppResult<Unit>
    suspend fun getInviteeGroups(): AppResult<List<Group>>
    suspend fun createGroup(name: String, description: String, avatarUrl: String?, memberEmails: List<String>): AppResult<Group>
    suspend fun getGroupDetail(id: String): AppResult<GroupDetail>
    suspend fun updateGroup(id: String, name: String, description: String, avatarUrl: String?): AppResult<Group>
    suspend fun deleteGroup(id: String): AppResult<Unit>
    suspend fun inviteMembers(id: String, emails: List<String>): AppResult<Unit>
    suspend fun kickMembers(id: String, emails: List<String>): AppResult<Unit>
    suspend fun leaveGroup(id: String): AppResult<Unit>
    suspend fun acceptGroupInvite(groupId: String): AppResult<Unit>
    suspend fun declineGroupInvite(groupId: String): AppResult<Unit>
    suspend fun uploadGroupAvatar(uri: Uri): AppResult<String>
}
