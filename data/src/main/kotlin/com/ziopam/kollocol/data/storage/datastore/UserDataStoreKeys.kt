package com.ziopam.kollocol.data.storage.datastore


import androidx.datastore.preferences.core.stringPreferencesKey

object UserDataStoreKeys {
    val AVATAR_URL = stringPreferencesKey("user_avatar_url")
    val FIRST_NAME = stringPreferencesKey("user_first_name")
    val LAST_NAME = stringPreferencesKey("user_last_name")
}
