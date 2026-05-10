package com.ziopam.kollocol.data.storage.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ziopam.kollocol.data.datasource.remote.group.GroupDto
import com.ziopam.kollocol.domain.model.Group

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val pendingCount: Int,
    val ownerId: String,
    val membershipType: String
)

fun GroupEntity.toGroup() = Group(
    id = id,
    name = name,
    description = description,
    avatarUrl = avatarUrl,
    memberCount = memberCount,
    pendingCount = pendingCount,
    ownerId = ownerId
)

fun GroupDto.toEntity(membershipType: String) = GroupEntity(
    id = id,
    name = name,
    description = description,
    avatarUrl = avatarUrl,
    memberCount = memberCount,
    pendingCount = pendingCount,
    ownerId = ownerId,
    membershipType = membershipType
)
