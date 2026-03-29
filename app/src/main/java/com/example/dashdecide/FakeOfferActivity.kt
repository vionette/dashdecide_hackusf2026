package com.example.dashdecide

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import java.util.Locale

class FakeOfferActivity : ComponentActivity() {

    data class Offer(
        val payout: Double,
        val miles: Double,
        val deadline: String,
        val restaurants: List<String>,
        val dropoffs: Int,
        val timer: Int,
        val mapImage: Int
    )

    private val offers = listOf(
        Offer(9.50, 10.0, "Deliver by 8:18 PM", listOf("3 Brothers Halal Food"), 1, 30, R.drawable.doordash_reject),
        Offer(8.40, 4.4, "Deliver by 10:17 PM", listOf("Papa John's Store 2674"), 1, 20, R.drawable.doordash_accept)
    )

    private var overlayManager: OverlayManager? = null
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_fake_offer)

        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
            return
        }

        val index = intent.getIntExtra("offerIndex", 1) - 1
        val offer = offers[index]

        findViewById<ImageView>(R.id.mapImage).setImageResource(offer.mapImage)

        val dpm = offer.payout / offer.miles
        val dph = (offer.payout / offer.timer) * 60  // NOW USES offer.timer

        val verdict = when {
            dpm >= 1.0 && dph >= 15.0 && offer.miles <= 8.0 -> "ACCEPT"
            else -> "DECLINE"
        }

        // Initialize TTS and speak the offer
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(0.9f)

                val restaurant = offer.restaurants[0].replace("Store \\d+".toRegex(), "").trim()
                val action = if (verdict == "ACCEPT") "accepting" else "declining"

                val offerText = """
                    New order from $restaurant. 
                    ${formatDollars(offer.payout)}, ${formatMiles(offer.miles)}. 
                    That's ${formatDollars(dpm)} per mile, about ${formatDollars(dph)} an hour. 
                    I would recommend $action this one.
                """.trimIndent()

                tts.speak(offerText, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        overlayManager = OverlayManager(applicationContext)

        findViewById<View>(android.R.id.content).postDelayed({
            overlayManager?.showOverlay(
                verdict = verdict,
                dpm = "$${String.format("%.2f", dpm)}",
                dph = "~$${Math.round(dph)}",
                payout = "$${offer.payout}",
                time = "${offer.timer} min",  // NOW USES offer.timer
                dist = "${offer.miles} mi",
                dropoffs = "${offer.dropoffs}"
            )
        }, 500)
    }

    private fun formatDollars(amount: Double): String {
        val dollars = amount.toInt()
        val cents = ((amount - dollars) * 100).toInt()
        return when {
            cents == 0 -> "$dollars dollars"
            cents < 10 -> "$dollars oh $cents"
            else -> "$dollars $cents"
        }
    }

    private fun formatMiles(m: Double): String {
        return if (m == m.toInt().toDouble()) {
            "${m.toInt()} miles"
        } else {
            "${String.format("%.1f", m).replace(".", " point ")} miles"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        overlayManager?.removeOverlay()
    }

    override fun onPause() {
        super.onPause()
        overlayManager?.removeOverlay()
    }

    override fun onStop() {
        super.onStop()
        overlayManager?.removeOverlay()
        overlayManager = null
    }

    @SuppressLint("GestureBackNavigation")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        overlayManager?.removeOverlay()
        super.onBackPressed()
    }
}