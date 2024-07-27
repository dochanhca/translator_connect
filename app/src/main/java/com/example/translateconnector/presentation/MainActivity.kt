package com.example.translateconnector.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.translateconnector.domain.viewmodel.UserViewModel
import com.example.translateconnector.presentation.screen.LoginScreen
import com.example.translateconnector.presentation.theme.ui.TranslateConnectorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslateConnectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val viewModel = hiltViewModel<UserViewModel>()
    val user by viewModel.userEntity.observeAsState()
    Text(text = "Hello ${user?.name}!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TranslateConnectorTheme {
        Greeting("Android")
    }
}