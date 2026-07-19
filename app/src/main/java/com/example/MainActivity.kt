package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ElysianLoginScreen
import com.example.ui.screens.WeddingHallAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ElysianViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          val viewModel: ElysianViewModel = viewModel()
          val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
          if (isLoggedIn) {
            WeddingHallAppScreen(viewModel = viewModel)
          } else {
            ElysianLoginScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}

