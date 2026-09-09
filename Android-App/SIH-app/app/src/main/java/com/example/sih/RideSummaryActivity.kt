package com.example.sih // Ensure this matches your actual package name

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max
import kotlin.math.roundToInt

class RideSummaryActivity : AppCompatActivity() {

    private lateinit var txtFinalScore: TextView
    private lateinit var txtFeedback: TextView
    private lateinit var btnDone: Button

    // Data class representing a single timestamped frame of telemetry
    data class TelemetryFrame(
        val rpm: Float,
        val gear: Int,
        val speed: Float,
        val throttle: Float,
        val leanAngle: Float
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_summary)

        txtFinalScore = findViewById(R.id.txtFinalScore)
        txtFeedback = findViewById(R.id.txtFeedback)
        btnDone = findViewById(R.id.btnDone)

        // Close activity when rider taps "FINISH RIDE"
        btnDone.setOnClickListener {
            finish()
        }

        // Simulate a collected trip array (In production, this is passed from MainActivity)
        val tripData = simulateTripLog()

        // Execute the scoring algorithm
        generateRideScore(tripData)
    }

    private fun generateRideScore(tripData: List<TelemetryFrame>) {
        val totalFrames = tripData.size
        if (totalFrames == 0) return

        var luggingEvents = 0
        var revvingEvents = 0
        var aggressiveCorneringEvents = 0

        // Iterate through the telemetry log to detect inefficiencies and hazards
        for (frame in tripData) {
            // 1. Engine Lugging: High gear at low speeds
            if (frame.gear >= 4 && frame.speed < 35.0f) {
                luggingEvents++
            }
            // 2. Excessive Revving: Low gear at high speeds
            if (frame.gear <= 2 && frame.rpm > 6500.0f) {
                revvingEvents++
            }
            // 3. Cornering Aggression: Heavy throttle at steep lean angles
            if (frame.leanAngle > 25.0f && frame.throttle > 70.0f) {
                aggressiveCorneringEvents++
            }
        }

        // Calculate Deductions (Weighted multipliers)
        val luggingDeduction = (luggingEvents.toFloat() / totalFrames) * 100 * 2.0f
        val revvingDeduction = (revvingEvents.toFloat() / totalFrames) * 100 * 1.5f
        val corneringDeduction = (aggressiveCorneringEvents.toFloat() / totalFrames) * 100 * 3.0f

        // Final Score Calculation
        val finalScore = max(0.0f, 100.0f - (luggingDeduction + revvingDeduction + corneringDeduction)).roundToInt()

        // Generate Actionable Feedback
        val feedbackList = mutableListOf<String>()
        if (luggingDeduction > 5) {
            feedbackList.add("⚠️ Frequent engine lugging detected. Downshift earlier in traffic to improve fuel economy.")
        }
        if (revvingDeduction > 5) {
            feedbackList.add("⚠️ Excessive revving in low gears. Follow the HUD upshift cues to minimize engine strain.")
        }
        if (corneringDeduction > 5) {
            feedbackList.add("⚠️ Aggressive throttle applied during steep cornering. Roll on smoother to maintain traction.")
        }

        if (feedbackList.isEmpty()) {
            feedbackList.add("🌟 Excellent ride! Perfect transmission efficiency and smooth cornering.")
        }

        // Update UI
        txtFinalScore.text = finalScore.toString()
        txtFeedback.text = feedbackList.joinToString("\n\n")

        // Dynamic Score Coloring
        when {
            finalScore >= 90 -> txtFinalScore.setTextColor(Color.parseColor("#00FF66")) // Green
            finalScore >= 70 -> txtFinalScore.setTextColor(Color.parseColor("#FFD700")) // Yellow
            else -> txtFinalScore.setTextColor(Color.parseColor("#FF3333")) // Red
        }
    }

    // Generates dummy data to verify the logic works before physical testing
    private fun simulateTripLog(): List<TelemetryFrame> {
        return listOf(
            TelemetryFrame(4000f, 3, 45f, 20f, 5f),  // Good
            TelemetryFrame(2000f, 5, 30f, 40f, 2f),  // Lugging
            TelemetryFrame(2100f, 5, 32f, 45f, 2f),  // Lugging
            TelemetryFrame(7000f, 2, 60f, 85f, 10f), // Revving
            TelemetryFrame(5500f, 3, 50f, 80f, 30f)  // Aggressive Cornering
        )
    }
}