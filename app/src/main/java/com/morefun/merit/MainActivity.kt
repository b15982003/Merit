package com.morefun.merit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.morefun.merit.ui.main.compose.MainScreen
import com.morefun.merit.ui.theme.MeritTheme

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // 長按超過此時間（毫秒）進入連擊模式
        const val COMBO_TRIGGER_TIME = 1500L
        // 連擊模式下每次敲擊的間隔（毫秒）
        const val COMBO_INTERVAL = 150L
    }
}