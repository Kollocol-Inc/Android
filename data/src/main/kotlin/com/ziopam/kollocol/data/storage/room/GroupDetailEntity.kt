package com.ziopam.kollocol.data.storage.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ziopam.kollocol.domain.model.GroupDetail
import com.ziopam.kollocol.domain.model.GroupMember

@Entity(tableName = "group_details")
data class GroupDetailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val pendingCount: Int,
    val ownerId: String,
    val members: List<GroupMember>,
    val invitedUsers: List<GroupMember>
)

fun GroupDetailEntity.toGroupDetail() = GroupDetail(
    id = id,
    name = name,
    description = description,
    avatarUrl = avatarUrl,
    memberCount = memberCount,
    pendingCount = pendingCount,
    ownerId = ownerId,
    members = members,
    invitedUsers = invitedUsers
)

fun GroupDetail.toEntity() = GroupDetailEntity(
    id = id,
    name = name,
    description = description,
    avatarUrl = avatarUrl,
    memberCount = memberCount,
    pendingCount = pendingCount,
    ownerId = ownerId,
    members = members,
    invitedUsers = invitedUsers
)
