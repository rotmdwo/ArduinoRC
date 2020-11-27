package edu.skku.arduinorc

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    val REQUEST_ENABLE_BT = 100
    lateinit var myUuid: UUID
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var mConnectedThread: ConnectedThread
    lateinit var mSocket: BluetoothSocket

    var didFound = false

    lateinit var camera: android.hardware.Camera
    lateinit var picByteArray: ByteArray
    lateinit var pic: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonW.setOnClickListener {
            mConnectedThread.write("g")
        }
        buttonA.setOnClickListener {
            mConnectedThread.write("l")
        }
        buttonS.setOnClickListener {
            mConnectedThread.write("b")
        }
        buttonD.setOnClickListener {
            mConnectedThread.write("r")
        }
        buttonE.setOnClickListener {
            mConnectedThread.write("s")
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }


        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            //val deviceHardwareAddress = device.address // MAC address

            if (deviceName == "HC-06") {
                val uuids = device.uuids
                uuids.forEach {uuid ->
                    myUuid = uuid.uuid
                }
                //val connect = ConnectThread(device)
                //connect.run()

                val method = device.javaClass.getMethod(
                    "createInsecureRfcommSocketToServiceRecord", *arrayOf<Class<*>>(
                        UUID::class.java
                    )
                )

                mSocket = method.invoke(device, myUuid) as BluetoothSocket
                bluetoothAdapter.cancelDiscovery()

                try {
                    mSocket.connect()
                    Log.d("asdf", "connection succeeded")
                } catch (e: IOException) {
                    Log.d("asdf", "connection failed")
                }

                mConnectedThread = ConnectedThread(mSocket)
                mConnectedThread.start()
            }
        }

        timer(period= 5000L, initialDelay = 3000L) {
            val surfaceTexture = SurfaceTexture(10)
            camera = Camera.open(1)
            camera.parameters.setPreviewSize(1,1)
            camera.setPreviewTexture(surfaceTexture)
            camera.startPreview()
            camera.takePicture(null, null, Camera.PictureCallback { data, camera ->
                picByteArray = data
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                val matrix = Matrix()
                matrix.setRotate(270.0f, (bitmap.width / 4).toFloat(), (bitmap.height / 4).toFloat())
                pic = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            })
        }


        //val isDiscovered = bluetoothAdapter.startDiscovery()
        //Log.d("asdf",""+isDiscovered)

        //val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        //registerReceiver(receiver, filter)



/*
        val device = bluetoothAdapter.getRemoteDevice(address)
        val method = device.javaClass.getMethod(
            "createInsecureRfcommSocketToServiceRecord", *arrayOf<Class<*>>(
                UUID::class.java
            )
        )
        //val method = device.javaClass.getMethod("createInsecureRfcommSocketToServiceRecord", )
        val socket = method.invoke(device, uuid) as BluetoothSocket

        bluetoothAdapter.cancelDiscovery()

        socket.connect()
        Log.d("asdf","success")
        mConnectedThread = ConnectedThread(socket)
        mConnectedThread.start()

 */
    }

    inner class ConnectedThread(socket: BluetoothSocket) : Thread() {
        val mmOutputStream: OutputStream = socket.outputStream
        /*
        val mmInputStream: InputStream = socket.inputStream
        fun run() {
            val buffer = ByteArray(256)
            var bytes = 0

            while (true) {
                bytes = mmInputStream.read(buffer)

            }
        }

         */

        fun write(message: String) {
            val msgBuffer = message.toByteArray()
            mmOutputStream.write(msgBuffer)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address

                    if (deviceName == "HC-06" && !didFound) {
                        didFound = true
                        Log.d("asdf", deviceName)
                        val uuids = device.uuids
                        uuids.forEach { uuid ->
                            Log.d("asdf", "uuid: ${uuid.uuid}")
                            myUuid = uuid.uuid
                        }
                        val connect = ConnectThread(device)
                        connect.run()
                    }
                }
            }
        }
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            val MY_UUID = myUuid
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery()

            try {
                mmSocket?.connect()
            } catch (e: IOException) {
                Log.d("asdf", "connection failed")
            }

            Log.d("asdf", "connection succeeded")
            mConnectedThread = ConnectedThread(mmSocket!!)
            mConnectedThread.start()
            /*
            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                Log.d("asdf","3")
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket)
            }

             */
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("asdf", "Could not close the client socket", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.close()
        // Don't forget to unregister the ACTION_FOUND receiver.
        //unregisterReceiver(receiver)
    }
}