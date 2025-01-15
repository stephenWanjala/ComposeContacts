package com.githu.stephenwanjala.composecontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.githu.stephenwanjala.composecontacts.ui.theme.ComposeContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeContactsTheme {
                val navHostController = rememberNavController()
               Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                   ComposeContactsNav(
                       navHostController = navHostController,
                   )
               }
            }
        }
    }
}

