package com.nkkuma.shogiban2kif_android

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.Response
import com.nkkuma.shogiban2kif_android.model.shogibanState

class ImageRecoResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_reco_result)
        val response = intent.getStringExtra("response")
        if (response != null) {
            val result = string2Class(response.replace("'","\""))
            setImage(result)
        }
        // TODO: illegal pattern Error output
    }

    fun string2Class(response: String): shogibanState {
        // mapperオブジェクトを作成
        val mapper = jacksonObjectMapper()
        return mapper.readValue(response)
    }

    fun string2ImageButtonId(place: String): Int {
        return resources.getIdentifier("imageRecoResultBtn_$place","id", packageName)
    }

    fun string2ImageSource(komaString: String): Int {
        val komaImgString = "koma_"+komaString.trim().replace("*","",true)
        return resources.getIdentifier(komaImgString,"drawable", packageName)
    }

    fun initImage() {
        // init image
        for (i in 1..9) {
            for (j in 1..9) {
                val imageView = findViewById<ImageButton>(string2ImageButtonId((i*10+j).toString()))
                imageView.setImageResource(string2ImageSource(" * "))
            }
        }
    }

    fun setImage(result: shogibanState) {
        // init image
        initImage()
        // set image
        for ((place,koma) in result.ban_result) {
            val imageView = findViewById<ImageButton>(string2ImageButtonId(place.toString()))
            imageView.setImageResource(string2ImageSource(koma))
            imageView.alpha = 1.0F
        }
    }
}