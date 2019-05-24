package io.github.phiresky.rrbletest

import android.Manifest
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files.size
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.RemoteException
import android.os.VibrationEffect
import android.support.v4.app.ActivityCompat
import android.util.Log
import org.altbeacon.beacon.*
import org.altbeacon.beacon.BeaconParser
import android.os.Vibrator



//020106030220aa0eff0018040027055ad01b0ec36a6416094a696e6f755f53656e736f725f48756d6954656d700000000000000000000000000000000000
class MainActivity : Activity(), BeaconConsumer {
    private var beaconManager: BeaconManager? = null
    private val PERMISSION_REQUEST_COARSE_LOCATION = 1
    private val REQUEST_ENABLE_BT = 2

   // private val lock?:


    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)
        beaconManager!!.getBeaconParsers().clear()
        // beaconManager!!.getBeaconParsers().add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        // Detect the telemetry Eddystone-TLM frame:
        beaconManager!!.getBeaconParsers().add(BeaconParser().setBeaconLayout("m:2-4=040027,i:5-20,p:15-15"))
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager!!.bind(this)

        */
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("This app needs location access")
            builder.setMessage("Please grant location access so this app can detect beacons.")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_COARSE_LOCATION
                )
            }
            builder.show()
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        bluetoothAdapter?.takeIf { !it.isEnabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }


        val vibe = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibe.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted")
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
        }
    }


    override fun onDestroy() {
        Log.i(TAG, "destroyed activity")
        super.onDestroy()
        /*beaconManager!!.unbind(this)*/
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.removeAllRangeNotifiers()
        beaconManager!!.addRangeNotifier(object : RangeNotifier {
            fun didEnterRegion(region: Region) {
                Log.i(TAG, "I just saw an beacon for the first time!")
            }

            fun didExitRegion(region: Region) {
                Log.i(TAG, "I no longer see an beacon")
            }
            override fun didRangeBeaconsInRegion(beacons: Collection<Beacon>, region: Region) {
                if (beacons.size > 0) {
                    Log.i(
                        TAG,
                        "The first beacon I see is about " + beacons.iterator().next().distance + " meters away."
                    )
                }
                for (beacon in beacons) {
                    Log.i(TAG, "beacon ${beacon.id1.toUuid()}: ${beacon.bluetoothName} + ${beacon.distance}m")
                }
            }
        })

        try {
            beaconManager!!.startRangingBeaconsInRegion(Region("io.github.phiresky.ble-test", null, null, null))
        } catch (e: RemoteException) {
            Log.e(TAG, "hi", e)
        }

    }

    companion object {
        protected val TAG = "RangingActivity"
    }
}
