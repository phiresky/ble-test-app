package io.github.phiresky.rrbletest

import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import io.github.phiresky.rrbletest.MainActivity
import java.lang.Compiler.disable
import org.altbeacon.beacon.startup.RegionBootstrap
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.startup.BootstrapNotifier
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.support.v4.app.ActivityCompat.startActivityForResult

import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.logging.LogManager
import java.lang.Compiler.disable
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import android.view.WindowManager
import android.os.PowerManager
import android.app.Activity
import android.app.KeyguardManager
import android.content.BroadcastReceiver




class BGApplication : Application(), BootstrapNotifier {
    private var regionBootstrap: RegionBootstrap? = null
    private var backgroundPowerSaver: BackgroundPowerSaver? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "RR App started up")

        val filter = ScanFilter.Builder().setServiceUuid(
            ParcelUuid.fromString("0000aa20-0000-1000-8000-00805f9b34fb"),
            ParcelUuid.fromString("FFFFFFFF-0000-0000-0000-000000000000")
        ).build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        val ctx = this
        bluetoothAdapter!!.bluetoothLeScanner!!.startScan(listOf(filter), settings, object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                // 2019-05-24 15:18:20.555 12663-12663/io.github.phiresky.rrbletest I/.BGApplication: SCANRESULT 1 ScanResult{device=D0:1B:0E:C3:6A:64, scanRecord=ScanRecord [mAdvertiseFlags=6, mServiceUuids=[0000aa20-0000-1000-8000-00805f9b34fb], mServiceSolicitationUuids=null, mManufacturerSpecificData={6400=[8, 0, 42, 6, 83, -48, 27, 14, -61, 106, 100]}, mServiceData={}, mTxPowerLevel=-2147483648, mDeviceName=Jinou_Sensor_HumiTemp], rssi=-90, timestampNanos=3712250754077, eventType=27, primaryPhy=1, secondaryPhy=0, advertisingSid=255, txPower=127, periodicAdvertisingInterval=0}
                Log.i(TAG, "SCANRESULT $callbackType $result")

                val intent = Intent(ctx, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ctx.startActivity(intent)

                val lock = (getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager).newKeyguardLock(Context.KEYGUARD_SERVICE)
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
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
            }


        })


        /*val beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)
        beaconManager.beaconParsers.clear()
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-4=040027,i:5-20,p:15-15"))
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        val region = Region("io.github.phiresky.ble-test", null, null, null)
        regionBootstrap = RegionBootstrap(this, region)
        backgroundPowerSaver = BackgroundPowerSaver(this)*/

    }


    override fun didDetermineStateForRegion(arg0: Int, arg1: Region) {
        // Don't care
    }

    override fun didEnterRegion(arg0: Region) {
        Log.d(TAG, "Got a didEnterRegion call")
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        // regionBootstrap!!.disable()
        val intent = Intent(this, MainActivity::class.java)
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    }

    override fun didExitRegion(arg0: Region) {
        // Don't care
    }

    companion object {
        private val TAG = ".BGApplication"
    }
}