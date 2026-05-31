package com.fortune.paper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fortune.paper.auth.KakaoAuthHolder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        KakaoAuthHolder.activity = this
        setContent { App() }
    }

    override fun onDestroy() {
        super.onDestroy()
        KakaoAuthHolder.activity = null
    }
}