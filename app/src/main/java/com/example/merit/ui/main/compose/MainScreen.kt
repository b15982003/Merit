package com.example.merit.ui.main.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merit.R
import com.example.merit.ui.theme.backgroundGray
import com.example.merit.utils.AudioPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    var expanded by remember { mutableStateOf(false) }
    var showNum by remember { mutableIntStateOf(0) }
    val showList = remember { mutableStateListOf(false, false, false, false, false) }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }

    LaunchedEffect(showNum) {
        if (showNum in 1..5) {
            showList[showNum - 1] = true
        }
    }

    showList.forEachIndexed { index, show ->
        LaunchedEffect(show) {
            if (show) {
                scope.launch {
                    delay(500)
                    showList[index] = false
                }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    showList.forEach { show ->
                        MeritItem(show = show)
                    }
                }

                Image(
                    modifier = Modifier
                        .height(if (expanded) 140.dp else 150.dp)
                        .width(if (expanded) 140.dp else 150.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    expanded = true
                                    awaitRelease()
                                    expanded = false
                                    scope.launch {
                                        showNum = (showNum % 6) + 1
                                        audioPlayer.playSound()
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
private fun MeritItem(show: Boolean) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearOutSlowInEasing
            )
        )
    ) {
        Image(
            modifier = Modifier.size(100.dp),
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