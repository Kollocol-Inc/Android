package com.ziopam.kollocol.core.ui.avatar

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.ui.uiText.UiText

private const val DEFAULT_MAX_AVATAR_BYTES: Long = 1_048_576L // 1MB

@Composable
fun rememberAvatarPicker(
    mimeFilter: String = "image/*",
    onAvatarSelected: (Uri) -> Unit,
    onError: (UiText) -> Unit,
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
): UiText? {
    val mime = resolver.getType(uri)
    if (mime == null || !mime.startsWith("image/")) {
        return UiText.StringRes(R.string.can_select_only_photos)
    }

    val size = runCatching {
        resolver.openAssetFileDescriptor(uri, "r")?.use { afd -> afd.length } ?: -1L
    }.getOrElse { -1L }

    if (size != -1L && size > maxBytes) {
        return UiText.StringRes(R.string.file_too_big)
    }

    return null
}