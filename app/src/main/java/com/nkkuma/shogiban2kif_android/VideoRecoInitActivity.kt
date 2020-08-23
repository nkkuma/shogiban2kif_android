package com.nkkuma.shogiban2kif_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR
import android.media.ImageReader
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class VideoRecoInitActivity : AppCompatActivity() {

    private val RESULT_CAMERA = 1001
    private val REQUEST_PERMISSION = 1002
    private val RESULT_GALARRY = 1003
    private val previewSize: Size = Size(300, 300) // FIXME: for now.
    private val inputCameraImageMode = "CAMERA"
    private val inputGalleryImageMode = "GALALERY"
    private var inputImageMode = inputCameraImageMode
    private lateinit var imageReader: ImageReader
    private lateinit var textureView: TextureView
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_reco_init)
    }

    override fun onResume() {
        super.onResume()

        // カメラ権限要求 & カメラ開始
        textureView = findViewById<TextureView>(R.id.imageView_imageReco)
        textureView.surfaceTextureListener = surfaceTextureListener
        startBackgroundThread()
    }

    // 戻るボタン
    fun onBackButtonTapped(view: View?){
        finish()
    }
    // カメラオープンボタン
    fun onVideoInitButtonTapped(view: View?){
        // カメラ権限要求 & カメラ開始
        checkPermission4CameraInput()
    }
    // 初期認識ボタン
    fun onVideoInitRecoButtonTapped(view: View?){
        // カメラを一時停止
        captureSession.stopRepeating()
        // 写真撮影
        saveimg()
        // APIをたたく
        // 4点を変数にする
        // 盤の位置と持ち駒の位置を画像化
        // 結果表示
        // カメラを再開
        captureSession.setRepeatingRequest(previewRequest, null, null)
    }
    // 認識開始ボタン
    fun onVideoRecoStartButtonTapped(view: View?){
        // 4点の変数を取得
        // intentで開始
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            imageReader = ImageReader.newInstance(width, height,
                ImageFormat.JPEG, /*maxImages*/ 2)
        }
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) { }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture) { }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean { return false }
    }

    // Runtime Permission check
    private fun checkPermission4CameraInput() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.INTERNET)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA)
        }
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
            && (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            && (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            openCamera()
        }
    }

    // 許可を求める
    private fun requestPermission(requestPermission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                requestPermission
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(requestPermission),
                REQUEST_PERMISSION
            )
        } else {
            val toast = Toast.makeText(
                this,
                "許可されないとアプリが実行できません",
                Toast.LENGTH_SHORT
            )
            toast.show()
            ActivityCompat.requestPermissions(
                this, arrayOf(requestPermission),
                REQUEST_PERMISSION
            )
        }
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Log.d("debug", "onRequestPermissionsResult()")
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission4CameraInput()
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(
                    this,
                    "これ以上なにもできません", Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    // カメラOPEN　CLOSE系
    private var cameraDevice: CameraDevice? = null
    private var cameraId = "0"
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var previewRequest: CaptureRequest
    private lateinit var previewRequestBuilder: CaptureRequest.Builder

    private val cameraManager: CameraManager by lazy {
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // すでにチェック済みなので来ないはず
            return
        }

        val stateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(cameraDevice: CameraDevice) {
                this@VideoRecoInitActivity.cameraDevice = cameraDevice
                createCameraPreviewSession()
            }
            override fun onDisconnected(cameraDevice: CameraDevice) {
                cameraDevice.close()
                this@VideoRecoInitActivity.cameraDevice = null
            }
            override fun onError(cameraDevice: CameraDevice, error: Int) {
                onDisconnected(cameraDevice)
                finish()
            }
        }

        cameraManager.openCamera(cameraId, stateCallback, null)
    }

    @Suppress("DEPRECATION")
    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            texture!!.setDefaultBufferSize(previewSize.width, previewSize.height)
            val surface = Surface(texture)
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilder.addTarget(surface)
            cameraDevice?.createCaptureSession(
                Arrays.asList(surface, imageReader.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {

                        if (cameraDevice == null) return
                        captureSession = cameraCaptureSession
                        try {
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                            previewRequest = previewRequestBuilder.build()
                            captureSession.setRepeatingRequest(previewRequest,
                                null, Handler(backgroundThread?.looper!!)
                            )
                        } catch (e: CameraAccessException) {
                            Log.e("erfs", e.toString())
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        //Tools.makeToast(baseContext, "Failed")
                    }
                }, null)
        } catch (e: CameraAccessException) {
            Log.e("erf", e.toString())
        }

    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread?.looper!!)
    }

    private fun saveimg() {
        val cFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM)

        try {
            val filename = "test_picture.jpg"
            var savefile : File? = null

            if (textureView.isAvailable) {
                savefile = File(cFolder, filename)
                val fos = FileOutputStream(savefile)
                val bitmap: Bitmap? = textureView.bitmap
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            }

            if (savefile != null) {
                Log.d("edulog", "Image Saved On: $savefile")
                Toast.makeText(this, "Saved: $savefile", Toast.LENGTH_SHORT).show()
            }

        } catch (e: CameraAccessException) {
            Log.d("edulog", "CameraAccessException_Error: $e")
        } catch (e: FileNotFoundException) {
            Log.d("edulog", "FileNotFoundException_Error: $e")
        } catch (e: IOException) {
            Log.d("edulog", "IOException_Error: $e")
        }

    }
}