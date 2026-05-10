package com.ziopam.kollocol.domain.model

data class Group(
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val pendingCount: Int,
    val ownerId: String
)

data class GroupDetail(
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val pendingCount: Int,
    val ownerId: String,
    val members: List<GroupMember>,
    val invitedUsers: List<GroupMember>
)

data class GroupMember(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?
)
