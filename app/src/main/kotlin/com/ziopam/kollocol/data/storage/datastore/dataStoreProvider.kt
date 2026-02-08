package com.ziopam.kollocol.data.storage.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore(name = "kollocol_datastore")