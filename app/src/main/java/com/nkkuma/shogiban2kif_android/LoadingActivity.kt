package com.nkkuma.shogiban2kif_android

import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.core.Response
import kotlinx.android.synthetic.main.activity_loading.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileDescriptor
import java.io.InputStream


class LoadingActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val sengo = intent.getBooleanExtra("sengo", false)
        val upfile = intent.getParcelableExtra<Uri>("upfile")
        imageView.setImageURI(upfile)
        textView_sengo.text = sengo.toString()

        // Create a new coroutine to move the execution off the UI thread
        runBlocking { recognizeOnServerCall(sengo, contenturi2fileuri(upfile!!)) }
    }

    fun contenturi2fileuri(uri: Uri) : InputStream {
        val path: String = ""
        val contentResolver: ContentResolver = this@LoadingActivity.contentResolver
        val someInputStream = contentResolver.openInputStream(uri)
        return someInputStream!!
    }

    suspend fun recognizeOnServerCall(sengo: Boolean, upfile: InputStream){
        var response: String = ""
        GlobalScope.launch(Dispatchers.IO) {
            val imageRecoService = ImageRecoService()
            response = imageRecoService.recognizeOnServer(upfile, sengo)
        }.join()
        val intent = Intent(this@LoadingActivity, ImageRecoResultActivity::class.java)
        intent.putExtra("response", response)
        startActivity(intent)
    }
}
