package com.example.sih // Ensure this matches your actual package name

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class OBDConnectionManager(private val deviceMacAddress: String) {
    // Standard Serial Port Profile (SPP) UUID for ELM327 modules
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    @SuppressLint("MissingPermission")
    fun connect(): Boolean {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceMacAddress)

            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()

            inputStream = socket?.inputStream
            outputStream = socket?.outputStream

            // Initialize ELM327 protocol
            sendCommand("AT Z\r") // Reset
            sendCommand("ATE0\r") // Echo Off
            sendCommand("ATSP0\r") // Auto Protocol

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun sendCommand(cmd: String): String {
        outputStream?.write(cmd.toByteArray())
        outputStream?.flush()

        Thread.sleep(100) // Brief pause for ELM327 to process

        val buffer = ByteArray(1024)
        val bytes = inputStream?.read(buffer) ?: 0
        return String(buffer, 0, bytes).trim()
    }

    // OBD-II PID 010C is Engine RPM
    fun getRPM(): Float {
        val rawResponse = sendCommand("010C\r")
        // Note: Real OBD parsing requires hex conversion (A * 256 + B) / 4.
        // This will be expanded in the next step.
        return 4500.0f
    }

    fun disconnect() {
        socket?.close()
    }
}