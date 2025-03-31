package com.example.merit.ui.main.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merit.MainActivity.Companion.MERIT_IMAGE_BIG_SIZE
import com.example.merit.MainActivity.Companion.MERIT_IMAGE_SMALL_SIZE
import com.example.merit.MainActivity.Companion.SHAPE_VOICE_ENTER_TIME
import com.example.merit.MainActivity.Companion.SHAPE_VOICE_EXIT_TIME
import com.example.merit.R
import com.example.merit.ui.theme.backgroundGray
import com.example.merit.utils.AudioPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp
    val shapeVoiceWidth = width - 150

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var showNum by remember { mutableIntStateOf(0) }

    val audioPlayers = remember { List(10) { AudioPlayer(context) } }

    val rememberX = remember { MutableList(10) { 0 } }
    val showItems = remember { MutableList(10) { mutableStateOf(false) } }

    LaunchedEffect(showNum) {
        if (showNum in 1..10) {
            val index = showNum - 1
            // X 軸位置
            rememberX[index] = (0..shapeVoiceWidth).random()
            // 哪一個要顯示
            showItems[index].value = true
            // 放出聲音
            audioPlayers[index].playSound()
            // 控制消失
            scope.launch {
                delay(500)
                showItems[index].value = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .background(backgroundGray)
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    showItems.forEachIndexed { index, show ->
                        Box(
                            modifier = Modifier.offset(x = rememberX[index].dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ShapeVoiceItem(show = show.value)
                        }
                    }
                }

                Image(
                    modifier = Modifier
                        .height(if (expanded) MERIT_IMAGE_SMALL_SIZE.dp else MERIT_IMAGE_BIG_SIZE.dp)
                        .width(if (expanded) MERIT_IMAGE_SMALL_SIZE.dp else MERIT_IMAGE_BIG_SIZE.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    expanded = true
                                    awaitRelease()
                                    expanded = false
                                    scope.launch {
                                        showNum = (showNum % 10) + 1
                                    }
                                }
                            )
                        },
                    painter = painterResource(R.drawable.ic_wooden_fish),
                    contentDescription = "wooden fish"
                )
            }
        }
    )
}

@Composable
private fun ShapeVoiceItem(show: Boolean) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight / 5 },
            animationSpec = tween(
                durationMillis = SHAPE_VOICE_ENTER_TIME,
                easing = LinearOutSlowInEasing
            )
        ) + fadeIn(
            initialAlpha = 0.1f
        ),
        exit = shrinkVertically(
            targetHeight = { fullHeight -> fullHeight - 60 },
            animationSpec = tween(
                durationMillis = SHAPE_VOICE_EXIT_TIME,
                easing = LinearOutSlowInEasing
            )
        ) + fadeOut(targetAlpha = 0f)
    ) {
        Image(
            modifier = Modifier
                .height(180.dp)
                .width(180.dp),
            painter = painterResource(R.drawable.ic_merit),
            contentDescription = "merit"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreen() {
    MainScreen()
}