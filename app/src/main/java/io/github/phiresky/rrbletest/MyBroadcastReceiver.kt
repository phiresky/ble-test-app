package io.github.phiresky.rrbletest

import android.bluetooth.le.BluetoothLeScanner
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Parcelable
import android.util.Log


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bleCallbackType = intent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1)
        if (bleCallbackType != -1) {
            Log.d(TAG, "Passive background scan callback type: $bleCallbackType")
            val scanResults = intent.getParcelableArrayListExtra<Parcelable>(
                BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT
            )
            // Do something with your ScanResult list here.
            // These contain the data of your matching BLE advertising packets
        }
    }

    companion object {
        private val TAG = ".RRBroadcastReceiver"
    }
}