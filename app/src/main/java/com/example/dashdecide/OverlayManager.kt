package com.example.dashdecide

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView

class OverlayManager(private val context: Context) {

    companion object {
        private var activeInstance: OverlayManager? = null
        fun killOverlay() {
            activeInstance?.removeOverlay()
            activeInstance = null
        }
    }

    init {
        activeInstance = this
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var closeBtnView: View? = null
    private var pillRunnable: Runnable? = null
    private val pillHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var isExpanded = true

    private var currentVerdict = ""
    private var currentDpm = ""
    private var currentDph = ""
    private var currentPayout = ""
    private var currentTime = ""
    private var currentDist = ""
    private var currentDropoffs = ""

    fun showOverlay(verdict: String, dpm: String, dph: String, payout: String, time: String, dist: String, dropoffs: String) {
        pillRunnable?.let { pillHandler.removeCallbacks(it) }
        pillRunnable = null
        currentVerdict = verdict
        currentDpm = dpm
        currentDph = dph
        currentPayout = payout
        currentTime = time
        currentDist = dist
        currentDropoffs = dropoffs
        removeOverlay()
        showBigCard(autoShrink = true)
    }

    private fun verdictColor(): Int = when (currentVerdict) {
        "ACCEPT" -> 0xFF00C96B.toInt()
        "DECLINE" -> 0xFFE8263A.toInt()
        else -> 0xFFF5C842.toInt()
    }

    private fun bgColor(): Int = when (currentVerdict) {
        "ACCEPT" -> 0xF0001400.toInt()
        "DECLINE" -> 0xF0190404.toInt()
        else -> 0xF0191200.toInt()
    }

    private fun borderColor(): Int = when (currentVerdict) {
        "ACCEPT" -> 0x8000C96B.toInt()
        "DECLINE" -> 0x80E8263A.toInt()
        else -> 0x80F5C842.toInt()
    }

    private fun showBigCard(autoShrink: Boolean = true) {
        isExpanded = true
        removeOverlay()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.overlay_layout, null)

        val bg = GradientDrawable()
        bg.setColor(bgColor())
        bg.cornerRadius = 28f
        bg.setStroke(3, borderColor())
        view.background = bg

        val color = verdictColor()
        view.findViewById<TextView>(R.id.overlayVerdict).apply { text = currentVerdict; setTextColor(color) }
        view.findViewById<TextView>(R.id.overlayDpm).apply { text = currentDpm; setTextColor(color) }
        view.findViewById<TextView>(R.id.overlayDph).apply { text = currentDph; setTextColor(color) }
        view.findViewById<TextView>(R.id.overlayPayout).apply { text = currentPayout; setTextColor(color) }
        view.findViewById<TextView>(R.id.overlayTime).apply { text = currentTime; setTextColor(color) }
        view.findViewById<TextView>(R.id.overlayDist).apply { text = currentDist; setTextColor(color) }
        view.findViewById<TextView>(R.id.overlayDropoffs).apply { text = currentDropoffs; setTextColor(color) }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.x = 0
        params.y = 400

        view.setOnClickListener { shrinkCard() }
        overlayView = view
        windowManager.addView(view, params)

        val delay = if (autoShrink) 5000L else 30000L
        view.postDelayed({ if (isExpanded) shrinkCard() }, delay)
    }

    private fun shrinkCard() {
        isExpanded = false
        removeOverlay()

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.overlay_pill, null)

        val color = verdictColor()
        val bg = GradientDrawable()
        bg.setColor(bgColor())
        bg.cornerRadius = 50f
        bg.setStroke(2, borderColor())
        view.background = bg

        view.findViewById<TextView>(R.id.pillVerdict).apply {
            text = "$currentVerdict $currentDpm/mi"
            setTextColor(color)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.x = 0
        params.y = 800

        view.setOnClickListener { showBigCard(autoShrink = false) }
        overlayView = view
        windowManager.addView(view, params)

        // Red close button over Accept area
        val closeBtn = View(context)
        closeBtn.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        val closeBtnParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            400,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        closeBtnParams.gravity = Gravity.BOTTOM
        closeBtn.setOnClickListener { removeOverlay() }
        closeBtnView = closeBtn
        windowManager.addView(closeBtn, closeBtnParams)

        pillRunnable = Runnable { removeOverlay() }
        pillHandler.postDelayed(pillRunnable!!, 15000)
    }

    fun removeOverlay() {
        pillRunnable?.let { pillHandler.removeCallbacks(it) }
        pillRunnable = null
        try {
            overlayView?.let {
                try { windowManager.removeView(it) } catch (e: Exception) { }
                overlayView = null
            }
            closeBtnView?.let {
                try { windowManager.removeView(it) } catch (e: Exception) { }
                closeBtnView = null
            }
            isExpanded = false
        } catch (e: Exception) { }
    }
}