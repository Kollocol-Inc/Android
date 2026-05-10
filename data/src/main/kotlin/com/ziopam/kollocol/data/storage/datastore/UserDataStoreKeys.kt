package com.ziopam.kollocol.data.storage.datastore

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserDataStoreKeys {
    val AVATAR_URL = stringPreferencesKey("user_avatar_url")
    val AVATAR_VERSION = longPreferencesKey("user_avatar_version")
    val FIRST_NAME = stringPreferencesKey("user_first_name")
    val LAST_NAME = stringPreferencesKey("user_last_name")
    val EMAIL = stringPreferencesKey("user_email")
    val THEME_MODE = stringPreferencesKey("theme_mode")
}
