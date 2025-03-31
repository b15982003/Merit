package com.example.merit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.merit.ui.main.compose.MainScreen
import com.example.merit.ui.theme.MeritTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 廣告
        MobileAds.initialize(this)
        enableEdgeToEdge()
        setContent {
            MeritTheme {
               MainScreen()
            }
        }
    }

    companion object{
        const val MERIT_IMAGE_SMALL_SIZE = 140
        const val MERIT_IMAGE_BIG_SIZE = 150
        const val SHAPE_VOICE_ENTER_TIME = 2000
        const val SHAPE_VOICE_EXIT_TIME = 1000
    }
}