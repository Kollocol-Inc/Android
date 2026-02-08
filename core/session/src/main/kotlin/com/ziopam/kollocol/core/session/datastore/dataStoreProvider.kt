package com.ziopam.kollocol.core.session.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore(name = "kollocol_datastore")