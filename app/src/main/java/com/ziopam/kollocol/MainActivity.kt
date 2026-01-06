package com.ziopam.kollocol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ziopam.kollocol.core.session.SessionRepository
import com.ziopam.kollocol.navigation.Graph
import com.ziopam.kollocol.ui.theme.AppTheme
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

            enableEdgeToEdge()
            setContent {
                AppTheme { AppRoot(startGraph) }
            }
        }
    }
}