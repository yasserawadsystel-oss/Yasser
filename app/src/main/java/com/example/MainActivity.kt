package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ClearanceDetailScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginAndRegisterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ClearanceViewModel

sealed class AppScreen {
    data object Dashboard : AppScreen()
    data class Detail(val sheetId: Long) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Arabic interface
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SystelApp()
                    }
                }
            }
        }
    }
}

@Composable
fun SystelApp(viewModel: ClearanceViewModel = viewModel()) {
    val userSession by viewModel.userSession.collectAsState()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Dashboard) }

    if (!userSession.isRegistered) {
        LoginAndRegisterScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                currentScreen = AppScreen.Dashboard
            }
        )
    } else {
        when (val screen = currentScreen) {
            is AppScreen.Dashboard -> {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { sheetId ->
                        currentScreen = AppScreen.Detail(sheetId)
                    }
                )
            }
            is AppScreen.Detail -> {
                BackHandler {
                    currentScreen = AppScreen.Dashboard
                }
                ClearanceDetailScreen(
                    viewModel = viewModel,
                    onBack = {
                        currentScreen = AppScreen.Dashboard
                    }
                )
            }
        }
    }
}
