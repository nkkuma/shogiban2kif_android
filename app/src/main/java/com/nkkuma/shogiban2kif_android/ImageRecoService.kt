package com.nkkuma.shogiban2kif_android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.BlobDataPart
import com.github.kittinunf.fuel.core.InlineDataPart
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import java.io.InputStream


class ImageRecoService : Service() {
    private val serverURL: String = "https://shogiban2kif.appspot.com/recognize"
    private val imageFieldname: String = "upfile"
    private val sengoFieldName: String = "hidden_sengo"
    private val rotateFiledName: String = "hidden_rotate"
    private val modeFieldName: String = "mode"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
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
