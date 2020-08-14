package com.nkkuma.shogiban2kif_android

import android.content.*
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_loading.*


class LoadingActivity : AppCompatActivity(){
    var sengo: Boolean = false
    var upfile: Uri = Uri.EMPTY
    val BROADCAST_ACTION = " jp.co.casareal.genintentservice.broadcast"
    private val RSS_JOB_ID = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        sengo = intent.getBooleanExtra("sengo", false)
        upfile = intent.getParcelableExtra<Uri>("upfile")!!
        // Create a new coroutine to move the execution off the UI thread
        // runBlocking { recognizeOnServerCall(sengo, contenturi2fileuri(upfile))
        val serviceIntent = Intent().apply {
            putExtra("sengo", sengo)
            putExtra("upfile", upfile)
        }
        ImageRecoService().enqueueWork(this@LoadingActivity, serviceIntent)
    }

    /**
     * ネストクラスで定義したBroadcastReceiverを、（マニフェスト上にではなく）ここで登録する
     */
    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(BROADCAST_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
    /**
     * BroadcastReceiverでMyIntentServiceからの返答を受け取ろうと思います
     */
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val response = intent.extras!!.getString("response")
            var sendIntent = Intent(this@LoadingActivity, ImageRecoResultActivity::class.java)
            sendIntent.putExtra("response", response)
            startActivity(sendIntent)
        }
    }
}
