package com.ziopam.kollocol.core.ui.avatar

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val DEFAULT_MAX_AVATAR_BYTES: Long = 1_048_576L // 1MB

@Composable
fun rememberAvatarPicker(
    mimeFilter: String = "image/*",
    onAvatarSelected: (Uri) -> Unit,
    onError: (String) -> Unit,
    maxBytes: Long = DEFAULT_MAX_AVATAR_BYTES
): () -> Unit {
    val resolver = LocalContext.current.contentResolver

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        val err = validateImageUri(
            resolver = resolver,
            uri = uri,
            maxBytes = maxBytes
        )

        if (err != null) onError(err) else onAvatarSelected(uri)
    }

    return remember(mimeFilter) {
        { launcher.launch(mimeFilter) }
    }
}

private fun validateImageUri(
    resolver: ContentResolver,
    uri: Uri,
    maxBytes: Long,
): String? {
    val mime = resolver.getType(uri)
    if (mime == null || !mime.startsWith("image/")) {
        return "Можно выбрать только фотографию"
    }

    val size = runCatching {
        resolver.openAssetFileDescriptor(uri, "r")?.use { afd -> afd.length } ?: -1L
    }.getOrElse { -1L }

    if (size != -1L && size > maxBytes) {
        return "Файл слишком большой (макс. 1 МБ)"
    }

    return null
}