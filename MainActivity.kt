package com.najmul.wifimonitor

import android.app.Activity
import android.net.TrafficStats
import android.os.Bundle
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private var bangla = true
    private lateinit var root: LinearLayout
    private lateinit var stats: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    private fun render() {
        val prefs = getSharedPreferences("usage", MODE_PRIVATE)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val rx = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
        val tx = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)
        val oldDay = prefs.getString("day", "")
        if (oldDay != today) {
            prefs.edit().putString("day", today).putLong("rx_start", rx)
                .putLong("tx_start", tx).apply()
        }
        val rxStart = prefs.getLong("rx_start", rx)
        val txStart = prefs.getLong("tx_start", tx)
        val down = (rx - rxStart).coerceAtLeast(0L)
        val up = (tx - txStart).coerceAtLeast(0L)
        val total = down + up

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 40, 36, 30)
            setBackgroundColor(0xFFF5F7FB.toInt())
        }
        val title = TextView(this).apply {
            text = "Najmul WiFi Monitor"
            textSize = 24f
            setTextColor(0xFF14213D.toInt())
        }
        root.addView(title)
        val lang = Button(this).apply {
            text = if (bangla) "English" else "বাংলা"
            setOnClickListener { bangla = !bangla; render() }
        }
        root.addView(lang)
        val device = TextView(this).apply {
            text = if (bangla) "এই ডিভাইসের ব্যবহার" else "This device usage"
            textSize = 18f
            setPadding(0, 28, 0, 8)
        }
        root.addView(device)
        stats = TextView(this).apply {
            textSize = 17f
            setTextColor(0xFF243B53.toInt())
            text = if (bangla)
                "আজকের মোট: ${formatBytes(total)}\nডাউনলোড: ${formatBytes(down)}\nআপলোড: ${formatBytes(up)}"
            else
                "Today total: ${formatBytes(total)}\nDownload: ${formatBytes(down)}\nUpload: ${formatBytes(up)}"
        }
        root.addView(stats)
        val note = TextView(this).apply {
            textSize = 13f
            setPadding(0, 24, 0, 0)
            setTextColor(0xFF6B7280.toInt())
            text = if (bangla)
                "নোট: Android-এর এই কাউন্টার Wi-Fi ও মোবাইল ডেটা আলাদা করে না। ফোন রিস্টার্ট হলে কাউন্টারও রিসেট হতে পারে।"
            else
                "Note: Android's public counter does not separate Wi-Fi from mobile data and may reset after reboot."
        }
        root.addView(note)
        val refresh = Button(this).apply {
            text = if (bangla) "রিফ্রেশ" else "Refresh"
            setOnClickListener { render() }
        }
        root.addView(refresh)
        setContentView(root)
    }

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1024) String.format(Locale.US, "%.2f GB", mb / 1024)
        else String.format(Locale.US, "%.1f MB", mb)
    }
}
