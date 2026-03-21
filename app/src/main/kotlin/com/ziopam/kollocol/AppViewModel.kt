package com.ziopam.kollocol

import androidx.lifecycle.ViewModel
import com.ziopam.kollocol.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : ViewModel()
