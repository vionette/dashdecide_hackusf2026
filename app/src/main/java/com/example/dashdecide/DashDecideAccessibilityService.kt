package com.example.dashdecide

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class DashDecideAccessibilityService : AccessibilityService() {

    private var overlayManager: OverlayManager? = null
    private var lastShownPackage = ""

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        serviceInfo = info
        overlayManager = OverlayManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        if (packageName != "com.example.dashdecide") return

        val root = rootInActiveWindow ?: return
        val allText = getAllText(root)

        if (!allText.contains("Guaranteed") && !allText.contains("Deliver by")) return

        val payout = extractPayout(allText)
        val miles = extractMiles(allText)

        if (payout <= 0 || miles <= 0) return

        val dpm = payout / miles
        val pickups = extractPickups(allText)
        val estMinutes = miles * 3.5 + pickups * 5 + 5
        val dph = (payout / estMinutes) * 60
        val dropoffs = extractDropoffs(allText)
        val timeWindow = extractTimeWindow(allText)

        val verdict = when {
            dpm >= 1.0 && dph >= 15.0 && miles <= 8.0 -> "ACCEPT"
            dpm >= 0.7 || dph >= 10.0 -> "MAYBE"
            else -> "DECLINE"
        }

        overlayManager?.showOverlay(
            verdict = verdict,
            dpm = "$${String.format("%.2f", dpm)}",
            dph = "~$${Math.round(dph)}",
            payout = "$${String.format("%.2f", payout)}",
            time = "$timeWindow min",
            dist = "$miles mi",
            dropoffs = "$dropoffs"
        )
    }

    private fun getAllText(node: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        collectText(node, sb)
        return sb.toString()
    }

    private fun collectText(node: AccessibilityNodeInfo?, sb: StringBuilder) {
        if (node == null) return
        node.text?.let { sb.append(it).append("\n") }
        for (i in 0 until node.childCount) {
            collectText(node.getChild(i), sb)
        }
    }

    private fun extractPayout(text: String): Double {
        val match = Regex("""\$(\d+\.?\d*)""").find(text)
        return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
    }

    private fun extractMiles(text: String): Double {
        val match = Regex("""(\d+\.?\d*)\s*mi""").find(text)
        return match?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
    }

    private fun extractPickups(text: String): Int {
        return Regex("Pickup", RegexOption.IGNORE_CASE).findAll(text).count()
    }

    private fun extractDropoffs(text: String): Int {
        return Regex("dropoff", RegexOption.IGNORE_CASE).findAll(text).count().coerceAtLeast(1)
    }

    private fun extractTimeWindow(text: String): Int {
        val match = Regex("""(\d+):(\d+)\s*([AP]M)""").find(text)
        return if (match != null) {
            val hour = match.groupValues[1].toInt()
            val min = match.groupValues[2].toInt()
            val ampm = match.groupValues[3]
            val deadlineMinutes = (if (ampm == "PM" && hour != 12) hour + 12 else hour) * 60 + min
            val now = java.util.Calendar.getInstance()
            val nowMinutes = now.get(java.util.Calendar.HOUR_OF_DAY) * 60 + now.get(java.util.Calendar.MINUTE)
            (deadlineMinutes - nowMinutes).coerceAtLeast(0)
        } else 45
    }

    override fun onInterrupt() {
        overlayManager?.removeOverlay()
    }
}