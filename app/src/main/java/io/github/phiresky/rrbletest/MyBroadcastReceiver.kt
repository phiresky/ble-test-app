package io.github.phiresky.rrbletest

import android.app.Activity
import android.app.KeyguardManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Parcelable
import android.os.PowerManager
import android.util.Log


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, iintent: Intent) {
        Log.i(TAG, "GOT RECEIVEEEEE")
        val bleCallbackType = iintent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1)
        if (bleCallbackType != -1) {
            Log.d(TAG, "Passive background scan callback type: $bleCallbackType")
            val callbackType = iintent.getIntExtra(
                BluetoothLeScanner.EXTRA_CALLBACK_TYPE,
                -1
            )
            val errorCode = iintent.getIntExtra(
                BluetoothLeScanner.EXTRA_ERROR_CODE,
                -1
            )
            val scanResults = iintent.getParcelableArrayListExtra<ScanResult>(
                BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT
            )



            // 2019-05-24 15:18:20.555 12663-12663/io.github.phiresky.rrbletest I/.BGApplication: SCANRESULT 1 ScanResult{device=D0:1B:0E:C3:6A:64, scanRecord=ScanRecord [mAdvertiseFlags=6, mServiceUuids=[0000aa20-0000-1000-8000-00805f9b34fb], mServiceSolicitationUuids=null, mManufacturerSpecificData={6400=[8, 0, 42, 6, 83, -48, 27, 14, -61, 106, 100]}, mServiceData={}, mTxPowerLevel=-2147483648, mDeviceName=Jinou_Sensor_HumiTemp], rssi=-90, timestampNanos=3712250754077, eventType=27, primaryPhy=1, secondaryPhy=0, advertisingSid=255, txPower=127, periodicAdvertisingInterval=0}
            Log.i(TAG, "SCANRESULT $callbackType $errorCode $scanResults")

            val intent = Intent(ctx, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)

            val lock = (ctx.getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager).newKeyguardLock(Context.KEYGUARD_SERVICE)
            val powerManager = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wake = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "rr_test:wakeupthing")

            lock.disableKeyguard()
            wake.acquire(10000)

            /* getWindow().addFlags(
                 WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                         or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                         or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                         or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                         or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
             )*/
            // Do something with your ScanResult list here.
            // These contain the data of your matching BLE advertising packets
        }
    }

    companion object {
        private val TAG = ".RRScanBroadcastReceiver"
    }
}