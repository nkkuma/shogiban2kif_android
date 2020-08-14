package com.nkkuma.shogiban2kif_android

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.JobIntentService
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.BlobDataPart
import com.github.kittinunf.fuel.core.InlineDataPart
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import java.io.InputStream


class ImageRecoService : JobIntentService() {
    companion object {
        const val JOB_ID = 1001
        val BROADCAST_ACTION = " jp.co.casareal.genintentservice.broadcast"
        const val serverURL: String = "https://shogiban2kif.appspot.com/recognize"
        const val imageFieldname: String = "upfile"
        const val sengoFieldName: String = "hidden_sengo"
        const val rotateFiledName: String = "hidden_rotate"
        const val modeFieldName: String = "mode"
    }
    // 1
    fun enqueueWork(context: Context, work: Intent) {
        enqueueWork(context, ImageRecoService::class.java, JOB_ID, work)
    }
    // 3
    override fun onHandleWork(intent: Intent) {
        val file = intent.getParcelableExtra<Uri>("upfile")
        var sengo = intent.getBooleanExtra("sengo", false)
        var result = recognizeOnServer(contenturi2fileuri(file!!), sengo)

        val intent = Intent(BROADCAST_ACTION)
        intent.putExtra("response", result)
        intent.setPackage(applicationInfo.packageName)
        sendBroadcast(intent)
    }

    fun contenturi2fileuri(uri: Uri) : InputStream {
        val contentResolver: ContentResolver = this@ImageRecoService.contentResolver
        val someInputStream = contentResolver.openInputStream(uri)
        return someInputStream!!
    }

    fun recognizeOnServer(file: InputStream, sengo: Boolean, rotate: Int = 0, mode: String = "all"): String {
        val (request, response, result) =
            Fuel.upload(serverURL)
                .add(
                    BlobDataPart(file, imageFieldname, imageFieldname),
                    InlineDataPart(sengo.toString(), sengoFieldName),
                    InlineDataPart(rotate.toString(), rotateFiledName),
                    InlineDataPart(mode, modeFieldName)
                )
                .responseJson()
        print(request)
        print(response)
        return when (result) {
            is Result.Failure -> {
                // val ex = result.getException()
                print("error message :")
                print(result)
                result.get()
            }
            is Result.Success -> {
                print("success message :")
                print(result)
                result.get().content.replace("\"","'")
            }
        }
    }
}
