package com.example.dashdecide

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class DemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        findViewById<TextView>(R.id.btnOffer1).setOnClickListener {
            val intent = Intent(this, FakeOfferActivity::class.java)
            intent.putExtra("offerIndex", 1)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btnOffer2).setOnClickListener {
            val intent = Intent(this, FakeOfferActivity::class.java)
            intent.putExtra("offerIndex", 2)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        OverlayManager.killOverlay()
    }
}