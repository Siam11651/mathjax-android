package com.siam11651.mathjaxandroid

import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.siam11651.mathjaxandroid.ui.theme.MathJaxAndroidTheme
import java.net.URLEncoder


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathJaxAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Home(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    private fun Home(modifier: Modifier = Modifier) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val inputState = remember {
                mutableStateOf("")
            }
            val inputStream = resources.openRawResource(R.raw.webview)
            val size = inputStream.available()
            val bytes = ByteArray(size)

            inputStream.read(bytes)

            AndroidView({ context ->
                val webview = WebView(this@MainActivity)
                webview.settings.javaScriptEnabled = true
                webview.settings.domStorageEnabled = true

                webview.loadDataWithBaseURL(
                    "http://localhost/?${URLEncoder.encode(inputState.value, "UTF-8")}",
                    String(bytes),
                    "text/html",
                    "UTF-8",
                    null
                )

                webview.setWebChromeClient(object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        Log.d("WebView", consoleMessage.message())
                        return true
                    }
                })

                webview
            }, update = { webview ->
                webview.loadDataWithBaseURL(
                    "http://localhost/?${URLEncoder.encode(inputState.value, "UTF-8")}",
                    String(bytes),
                    "text/html",
                    "UTF-8",
                    null
                )
            }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = inputState.value,
                onValueChange = { newValue ->
                    inputState.value = newValue
                }, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}