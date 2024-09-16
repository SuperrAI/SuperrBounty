package com.superr.bounty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.compose.rememberNavController
import com.superr.bounty.data.AppModule
import com.superr.bounty.domain.model.UserRole
import com.superr.bounty.ui.navigation.SideNavigation
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp

private const val TAG = "Superr.MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())

        setContent {

            val encryptedPreferencesHelper = AppModule.provideEncryptedPreferencesHelper(this)

            var isLoggedIn by remember { mutableStateOf(encryptedPreferencesHelper.getBooleanData("isLogin")) }
            var userId by remember { mutableStateOf("") }
            var isTeacher by remember { mutableStateOf(if (isLoggedIn) encryptedPreferencesHelper.getUser().role == UserRole.TEACHER else false) }
            val navController = rememberNavController()

            SuperrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = SuperrTheme.colorScheme.White
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        SideNavigation(
                            selectedRoute = "",
                            onRouteSelected = { },
                            isLoggedIn = isLoggedIn
                        )

                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(48.fdp)) {
                            Text(text = "Hello world!", style = SuperrTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}