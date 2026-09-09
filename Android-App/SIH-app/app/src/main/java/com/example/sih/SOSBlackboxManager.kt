package com.example.yourprojectname

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.telephony.SmsManager
import android.util.Log

// 1. We now pass 'context' so the class can access GPS and SMS services
class SOSBlackboxManager(private val context: Context) {

    private val maxBufferSize = 600
    private val telemetryBuffer = ArrayDeque<RideSummaryActivity.TelemetryFrame>()
    private var isCrashLocked = false

    private val CRASH_LEAN_ANGLE_DEG = 85.0f
    private val CRASH_DECELERATION_G = -3.0f

    fun updateBlackbox(frame: RideSummaryActivity.TelemetryFrame) {
        if (isCrashLocked) return

        if (telemetryBuffer.size >= maxBufferSize) {
            telemetryBuffer.removeFirst()
        }
        telemetryBuffer.addLast(frame)

        if (detectCrash(frame)) {
            triggerSOSProtocol()
        }
    }

    private fun detectCrash(frame: RideSummaryActivity.TelemetryFrame): Boolean {
        return frame.leanAngle >= CRASH_LEAN_ANGLE_DEG ||
                frame.throttle < CRASH_DECELERATION_G
    }

    private fun triggerSOSProtocol() {
        isCrashLocked = true
        Log.e("SOS_SYSTEM", "💥 CRASH DETECTED! Blackbox locked.")
        exportBlackboxData()
    }

    @SuppressLint("MissingPermission")
    private fun exportBlackboxData() {
        val crashLog = telemetryBuffer.toList()
        Log.d("SOS_SYSTEM", "Locked ${crashLog.size} frames for forensic analysis.")

        // 2. Access the phone's native GPS hardware
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        // Defaulting to Pimpri-Chinchwad coordinates if the GPS is still acquiring satellites
        val lat = location?.latitude ?: 18.6298
        val lon = location?.longitude ?: 73.7997

        // 3. Draft and send the emergency SMS
        val emergencyContact = "+919876543210" // TODO: Replace with Mohan's actual mobile number
        val message = "🚨 CEP SOS: Motorcycle crash detected! Location: http://maps.google.com/maps?q=$lat,$lon"

        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(emergencyContact, null, message, null, null)
            Log.e("SOS_SYSTEM", "Emergency SMS dispatched successfully.")
        } catch (e: Exception) {
            Log.e("SOS_SYSTEM", "Failed to send SMS: ${e.message}")
        }
    }

    fun resetBlackbox() {
        isCrashLocked = false
        telemetryBuffer.clear()
    }
}