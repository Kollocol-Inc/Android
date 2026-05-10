package com.ziopam.kollocol.data.datasource.remote.group

import com.google.gson.annotations.SerializedName
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.domain.model.GroupDetail
import com.ziopam.kollocol.domain.model.GroupMember

data class GetGroupsResponseDto(
    @SerializedName("groups")
    val groups: List<GroupDto> = emptyList()
)

data class GroupDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("member_count")
    val memberCount: Int = 0,
    @SerializedName("pending_count")
    val pendingCount: Int = 0,
    @SerializedName("owner_id")
    val ownerId: String = ""
) {
    fun toDomain() = Group(
        id = id,
        name = name,
        description = description,
        avatarUrl = avatarUrl,
        memberCount = memberCount,
        pendingCount = pendingCount,
        ownerId = ownerId
    )
}

data class GroupWithMembersDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("member_count")
    val memberCount: Int = 0,
    @SerializedName("pending_count")
    val pendingCount: Int = 0,
    @SerializedName("owner_id")
    val ownerId: String = "",
    @SerializedName("members")
    val members: List<GroupMemberDto> = emptyList(),
    @SerializedName("invited_users")
    val invitedUsers: List<GroupMemberDto> = emptyList()
) {
    fun toDomain() = GroupDetail(
        id = id,
        name = name,
        description = description,
        avatarUrl = avatarUrl,
        memberCount = memberCount,
        pendingCount = pendingCount,
        ownerId = ownerId,
        members = members.map { it.toDomain() },
        invitedUsers = invitedUsers.map { it.toDomain() }
    )
}

data class GroupMemberDto(
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("last_name")
    val lastName: String = "",
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
) {
    fun toDomain() = GroupMember(
        userId = userId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = avatarUrl
    )
}

data class CreateGroupRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("member_emails")
    val memberEmails: List<String>
)

data class UpdateGroupRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)

data class GroupMembersRequestDto(
    @SerializedName("emails")
    val emails: List<String>
)

data class GroupAvatarUploadResponseDto(
    @SerializedName("avatar_url")
    val avatarUrl: String = ""
)
