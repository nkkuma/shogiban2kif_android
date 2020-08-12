package com.nkkuma.shogiban2kif_android

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ImageRecoActivity : AppCompatActivity() {

    private val RESULT_CAMERA = 1001
    private val REQUEST_PERMISSION = 1002
    private var imageView: ImageView? = null
    private var cameraUri: Uri? = null
    private var cameraFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_reco)
        imageView = findViewById(R.id.imageView_imageReco);
    }
    // 戻るボタン
    fun onButtonTapped(view: View?){
        finish()
    }
    // カメラボタンタップ時
    fun onCameraButtonTapped(view: View?) {
        // Android 6, API 23以上でパーミッシンの確認
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission()
        } else {
            cameraIntent()
        }
    }
    // 写真ボタンタップ時
    fun onGalleryButtonTapped(view: View?){
        val intent = Intent(this, ImageRecoActivity::class.java)
        startActivity(intent)
    }
    // 実行ボタンタップ時
    fun onRecognizeButtonTapped(view: View?){
        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra("sengo", findViewById<Switch>(R.id.switch_sengo).isChecked)
        intent.putExtra("upfile", cameraUri)
        startActivity(intent)
    }

    // ここからはボタン以外のUtil的なActivity

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == RESULT_CAMERA) {
            if (cameraUri != null) {
                imageView!!.setImageURI(cameraUri)
                cameraFile?.let { registerDatabase(it) }
            } else {
                Log.d("debug", "cameraUri == null")
            }
        }
    }

    // アンドロイドのデータベースへ登録する
    private fun registerDatabase(file: File) {
        val contentValues = ContentValues()
        val contentResolver: ContentResolver = this@ImageRecoActivity.contentResolver
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        contentValues.put("_data", file.absolutePath)
        contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }

    private fun cameraIntent() {
        // 保存先のフォルダー
        val cFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val fileDate = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        // ファイル名
        val fileName = String.format("CameraIntent_%s.jpg", fileDate)
        cameraFile = File(cFolder, fileName)
        cameraUri = FileProvider.getUriForFile(
            this@ImageRecoActivity,
            applicationContext.packageName + ".fileprovider",
            this.cameraFile!!
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        startActivityForResult(intent, RESULT_CAMERA)
        Log.d("debug", "startActivityForResult()")
    }

    // Runtime Permission check
    private fun checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraIntent()
                    }
                    else {
                        requestPermission(Manifest.permission.CAMERA)
                    }
                }
                else {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            requestPermission(Manifest.permission.INTERNET)
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
                this@ImageRecoActivity,
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
                checkPermission()
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
}