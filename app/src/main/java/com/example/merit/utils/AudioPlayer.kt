package com.example.merit.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.merit.R

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound() {
        mediaPlayer = MediaPlayer.create(
            context,
            R.raw.voice_wooden_fish
        )
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            release()
        }
    }

    private fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}