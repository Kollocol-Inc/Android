package com.ziopam.kollocol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val sessionRepository: SessionRepository,
    private val personalRepository: PersonalRepository
) : ViewModel() {
    val themeMode = personalRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.LIGHT)
}
