package com.morefun.merit.ui.main.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.morefun.merit.MainActivity.Companion.COMBO_INTERVAL
import com.morefun.merit.MainActivity.Companion.COMBO_TRIGGER_TIME
import com.morefun.merit.MainActivity.Companion.MERIT_IMAGE_BIG_SIZE
import com.morefun.merit.MainActivity.Companion.MERIT_IMAGE_SMALL_SIZE
import com.morefun.merit.MainActivity.Companion.SHAPE_VOICE_ENTER_TIME
import com.morefun.merit.MainActivity.Companion.SHAPE_VOICE_EXIT_TIME
import com.morefun.merit.ui.component.AdView
import com.morefun.merit.ui.theme.backgroundGray
import com.morefun.merit.utils.AudioPlayer
import com.morefun.merit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
        bottomBar = {
            AdView()
        },
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
                            detectComboPress(
                                scope = scope,
                                onPressChange = { expanded = it },
                                onHit = { showNum = (showNum % 10) + 1 },
                            )
                        },
                    painter = painterResource(R.drawable.ic_wooden_fish),
                    contentDescription = "wooden fish"
                )
            }
        }
    )
}

/**
 * 處理木魚的按壓手勢：
 * - 短按放開：敲一次（[onHit]）。
 * - 長按超過 [COMBO_TRIGGER_TIME]：進入連擊模式，每隔 [COMBO_INTERVAL] 自動敲一次。
 * - 放開手指、或手指滑出圖片範圍，都會立即停止連擊。
 *
 * 註：不使用 detectTapGestures，因為它的 awaitRelease() 只在「手指真正離開螢幕」
 * 或手勢被其他元件攔截時才會回傳；手指滑出範圍但仍按著螢幕時不會觸發，
 * 導致連擊停不下來。這裡改用自訂手勢迴圈，額外偵測手指是否移出邊界。
 */
private suspend fun PointerInputScope.detectComboPress(
    scope: CoroutineScope,
    onPressChange: (Boolean) -> Unit,
    onHit: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown()
        onPressChange(true)

        var comboMode = false
        // 撐過門檻時間就開始連擊，結束時再取消這個協程
        val comboJob = scope.launch {
            delay(COMBO_TRIGGER_TIME.milliseconds)
            comboMode = true
            while (isActive) {
                onHit()
                delay(COMBO_INTERVAL.milliseconds)
            }
        }

        // 在範圍內正常放開才算單擊；滑出範圍則視為取消
        var releasedInBounds = false
        try {
            while (true) {
                val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id }
                    ?: break
                if (!change.pressed) {
                    releasedInBounds = true
                    break
                }
                val position = change.position
                val outOfBounds = position.x < 0f || position.y < 0f ||
                    position.x > size.width || position.y > size.height
                if (outOfBounds) break
            }
        } finally {
            comboJob.cancel()
            onPressChange(false)
        }

        // 沒進入連擊、且是在範圍內放開的單純短按才補一次，避免和連擊重複計算
        if (!comboMode && releasedInBounds) onHit()
    }
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