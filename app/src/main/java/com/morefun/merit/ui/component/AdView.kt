package com.morefun.merit.ui.component

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdView() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-2041481484933612/3082591771"
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("AdBanner", "✅ Banner loaded")
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("AdBanner", "❌ Failed to load banner: ${adError.message}")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}