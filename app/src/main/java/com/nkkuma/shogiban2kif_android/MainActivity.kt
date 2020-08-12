package com.nkkuma.shogiban2kif_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    // 実行ボタンタップ時
    fun onButtonTapped(view: View?){
        val intent = Intent(this, ImageRecoActivity::class.java)
        startActivity(intent)
    }
}