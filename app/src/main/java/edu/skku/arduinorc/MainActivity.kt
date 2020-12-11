package edu.skku.arduinorc

import kotlinx.coroutines.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.RuntimeException
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    val REQUEST_ENABLE_BT = 100
    lateinit var myUuid: UUID
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var mConnectedThread: ConnectedThread
    lateinit var mSocket: BluetoothSocket

    var didFound = false

    val camera = Camera.open(0)
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

        camera.parameters.setPreviewSize(1, 1)
        val surfaceTexture = SurfaceTexture(10)
        camera.setPreviewTexture(surfaceTexture)
        camera.startPreview()

        timer(period= 1000L, initialDelay = 2000L) {
            //camera.stopPreview()
            //camera.setPreviewTexture(surfaceTexture)
            //camera.startPreview()
            try {
                camera.takePicture(null, null, Camera.PictureCallback { data, camera ->
                    // 촬영하자마자 미리 프리뷰 준비
                    camera.setPreviewTexture(surfaceTexture)
                    camera.startPreview()

                    // 압축을 위해 비트맵으로 변환
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    val matrix = Matrix()
                    matrix.postScale(0.175f, 0.175f)
                    matrix.postRotate(90.0f)
                    pic = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                    // 다시 바이트 어레이로 변환
                    val stream = ByteArrayOutputStream()
                    pic.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    picByteArray = stream.toByteArray()

                    runBlocking {
                        try {
                            val response = ServerApi.instance.sendPicture(PictureData("sj", picByteArray)).data

                            if (response != null) {
                                Log.d("asdf",response)
                                for (i in response.indices) mConnectedThread.write(response.substring(i, i + 1))
                            }
                        } catch (e: Exception) {
                            Log.e("asdf", "sendPicture API 호출 오류", e)
                        }
                    }
                })
            } catch (e: RuntimeException) {
                e.printStackTrace()
                camera.stopPreview()
                camera.setPreviewTexture(surfaceTexture)
                camera.startPreview()
            }

        }
    }

    inner class ConnectedThread(socket: BluetoothSocket) : Thread() {
        val mmOutputStream: OutputStream = socket.outputStream

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
    }
}