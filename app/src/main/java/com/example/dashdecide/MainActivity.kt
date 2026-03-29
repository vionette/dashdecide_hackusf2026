package com.example.dashdecide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.widget.EditText
import android.widget.Switch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val autoSwitch = findViewById<Switch>(R.id.autoSwitch)
        val minDpm = findViewById<EditText>(R.id.minDpm)
        val minDph = findViewById<EditText>(R.id.minDph)
        val minMoney = findViewById<EditText>(R.id.minMoney)
        val maxMiles = findViewById<EditText>(R.id.maxMiles)
        val maxTime = findViewById<EditText>(R.id.maxTime)
        val maxDropoffs = findViewById<EditText>(R.id.maxDropoffs)

        autoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                minDpm.setText("1.00")
                minDph.setText("15.00")
                minMoney.setText("4.00")
                maxMiles.setText("8")
                maxTime.setText("45")
                maxDropoffs.setText("2")
            } else {
                minDpm.setText("")
                minDph.setText("")
                minMoney.setText("")
                maxMiles.setText("")
                maxTime.setText("")
                maxDropoffs.setText("")
            }
        }
        startService(Intent(this, DashDecideForegroundService::class.java))

        // Request overlay permission if not granted
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnTryDemo).setOnClickListener {
            startActivity(Intent(this, DemoActivity::class.java))
        }
    }
}