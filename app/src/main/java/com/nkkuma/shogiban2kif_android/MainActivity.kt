package com.nkkuma.shogiban2kif_android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkServer()
    }
    // 認識エンジン起動確認
    fun checkServer() {
        val imageButton = findViewById<Button>(R.id.buttonImage)
        val videoButton = findViewById<Button>(R.id.buttonVideo)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_serveraccess)
        val textView = findViewById<TextView>(R.id.textView_serveraccess)
        // ボタン非活性化
        imageButton.isEnabled = false
        videoButton.isEnabled = false

        val imageURLCreateURL = "https://shogiban2kif.appspot.com/warmup"
        imageURLCreateURL.httpGet().response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    // レスポンスボディを表示
                    println("非同期処理の結果：" + String(response.data))
                    // progress barを非表示
                    progressBar.visibility = View.INVISIBLE
                    textView.visibility = View.INVISIBLE
                    // ボタンを活性化
                    imageButton.isEnabled = true
                    videoButton.isEnabled = true
                }
                is Result.Failure -> {
                    println("通信に失敗しました。")
                    // progress barを非表示
                    progressBar.visibility = View.INVISIBLE
                    textView.visibility = View.INVISIBLE
                    // toast作成
                    Toast.makeText(this, "アクセス失敗。ネット環境を確認してアプリを再起動してください。", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    // 実行ボタンタップ時
    fun onButtonTapped(view: View?){
        val intent = Intent(this, ImageRecoActivity::class.java)
        startActivity(intent)
    }
    // 実行ボタンタップ時
    fun onVideoButtonTapped(view: View?){
        val intent = Intent(this, VideoRecoInitActivity::class.java)
        startActivity(intent)
    }
}