package com.ziopam.kollocol

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ziopam.kollocol.core.navigation.Graph
import com.ziopam.kollocol.domain.repository.SessionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionRepository: SessionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        var startGraph: String? = null
        splashScreen.setKeepOnScreenCondition {
            startGraph == null
        }

        lifecycleScope.launch {
            val isAuthorized = sessionRepository.hasValidSession()

            startGraph = if (isAuthorized) {
                Graph.MAIN
            } else {
                Graph.AUTH
            }

            setContent {
                AppRoot(startGraph)
            }
        }
    }
}