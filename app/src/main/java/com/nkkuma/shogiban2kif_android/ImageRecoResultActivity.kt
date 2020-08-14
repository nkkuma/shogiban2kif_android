package com.nkkuma.shogiban2kif_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nkkuma.shogiban2kif_android.model.shogibanState
import java.net.URLEncoder
import java.util.*
import kotlin.Comparator


class ImageRecoResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_reco_result)
        val response = intent.getStringExtra("response")
        if (response != null) {
            val result = string2Class(response.replace("'","\""))
            setBanImage(result)
            setMochigomaSenteNumber(result)
            setMochigomaGoteNumber(result)
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

    fun string2MochigomaSpinnerId(koma: String, teban: String): Int {
        return resources.getIdentifier("spinner_mochigoma${teban}_${koma}","id", packageName)
    }

    fun string2ImageSource(komaString: String): Int {
        val komaImgString = "koma_"+komaString.trim().replace("*","",true)
        return resources.getIdentifier(komaImgString,"drawable", packageName)
    }

    fun initBanImage() {
        // init image
        for (i in 1..9) {
            for (j in 1..9) {
                val imageView = findViewById<ImageButton>(string2ImageButtonId((i*10+j).toString()))
                imageView.setImageResource(string2ImageSource(" * "))
                imageView.tag = " * "
            }
        }
    }

    fun setBanImage(result: shogibanState) {
        // init image
        initBanImage()
        // set image
        for ((place,koma) in result.ban_result) {
            val imageView = findViewById<ImageButton>(string2ImageButtonId(place.toString()))
            imageView.setImageResource(string2ImageSource(koma))
            imageView.tag = koma
        }
    }

    fun initMochigomaSenteNumber() {
        // mochigoma_name
        val mochigoma_name = resources.getStringArray(R.array.komaname_shogibanState).slice(1..7)
        // init mochigoma_number
        for (koma in mochigoma_name) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "sente"))
            imageView.setSelection(0)
        }
    }

    fun setMochigomaSenteNumber(result: shogibanState) {
        // init mochigoma sente
        initMochigomaSenteNumber()
        // init mochigoma_number
        for ((koma,num) in result.sente_mochi) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "sente"))
            imageView.setSelection(num)
        }
    }

    fun initMochigomaGoteNumber() {
        // mochigoma_name
        val mochigoma_name = resources.getStringArray(R.array.komaname_shogibanState).slice(1..7)
        // init mochigoma_number
        for (koma in mochigoma_name) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "gote"))
            imageView.setSelection(0)
        }
    }

    fun setMochigomaGoteNumber(result: shogibanState) {
        // init mochigoma sgote
        initMochigomaGoteNumber()
        // init mochigoma_number
        for ((koma,num) in result.gote_mochi) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "gote"))
            imageView.setSelection(num)
        }
    }

    fun getFixedState(): shogibanState {
        var shogibanState = shogibanState()
        // get ban
        var ban_map = mutableMapOf<Int, String>()
        for (i in 1..9) {
            for (j in 1..9) {
                val imageView = findViewById<ImageButton>(string2ImageButtonId((i*10+j).toString()))
                ban_map[i*10+j] = imageView.tag.toString()
            }
        }
        shogibanState.ban_result = ban_map
        // mochigoma_name
        val mochigoma_name = resources.getStringArray(R.array.komaname_shogibanState).slice(1..7)
        // get sente
        var mochigomasente_map = mutableMapOf<String, Int>()
        for (koma in mochigoma_name) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "sente"))
            val size = imageView.selectedItem.toString().toInt()
            if (size > 0) { mochigomasente_map[koma] = size }
        }
        shogibanState.sente_mochi = mochigomasente_map
        // get gote
        var mochigomagote_map = mutableMapOf<String, Int>()
        for (koma in mochigoma_name) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "gote"))
            val size = imageView.selectedItem.toString().toInt()
            if (size > 0) { mochigomagote_map[koma] = size }
        }
        shogibanState.gote_mochi = mochigomagote_map
        // get teban
        // set empty-point (=already setted as default)
        return shogibanState
    }

    fun sort_mochigoma(map: Map<String, Int>, array: Array<String>): SortedMap<String, Int> {
        val comparator: Comparator<String> =
            Comparator<String> { a, b -> array.indexOf(a).compareTo(array.indexOf(b)) }
        return map.toSortedMap(comparator)
    }

    fun shogibanState2Sfen(shogibanState: shogibanState): String {
        val tebans = mapOf<String,String>("sente" to "b", "gote" to "w")
        var komas = mapOf<String,String>()
        val kifkomas = resources.getStringArray(R.array.komaname_shogibanState)
        val sfenkomas = resources.getStringArray(R.array.komaname_sfen)
        for (i in 0..28) { komas.plus(Pair(kifkomas[i],sfenkomas[i])) }
        var kif_text    = ""
        val ban_result  = shogibanState.ban_result
        val sente_mochi = sort_mochigoma(shogibanState.sente_mochi, kifkomas)
        val gote_mochi  = sort_mochigoma(shogibanState.gote_mochi, kifkomas)
        val teban       = shogibanState.teban;

        // i = 行番号 // j = 列番号
        for (i in 1..9){
            var before_koma = ""
            for (j in 9..1){
                val tmpkoma = ban_result[j*10+i]
                val koma = komas[tmpkoma]
                if ((before_koma == "1") && (koma == "1")){
                    val space = (kif_text.takeLast(1).toInt() + 1).toString()
                    kif_text = kif_text.dropLast(1) + space
                }else{
                    kif_text += koma
                }
                before_koma = koma!!
            }
            kif_text += "/"
        }
        // 最後のスライスを消す
        kif_text = kif_text.dropLast(1)

        // 手番
        kif_text += " " + tebans[teban]

        // 持ち駒
        kif_text += " ";
        for ((koma, kazu) in sente_mochi) {
            var kazuString = kazu.toString()
            if (kazuString == "1"){ kazuString = "" }
            kif_text = kif_text + kazuString + komas[" $koma"]
        }
        for ((koma, kazu) in gote_mochi) {
            var kazuString = kazu.toString()
            if (kazuString == "1"){ kazuString = "" }
            kif_text = kif_text + kazuString + komas[" $koma"]
        }
        if (kif_text.takeLast(1) == " ")  {kif_text+="-"}

        // 何手目
        kif_text += " 1"

        return kif_text
    }

    fun jump2Kento(view: View) {
        val sfen4Kento = URLEncoder.encode(shogibanState2Sfen(getFixedState()), "UTF-8")
        val linkUri = Uri.parse("https://www.kento-shogi.com/?initpos=$sfen4Kento")
        val kentoIntent = Intent(Intent.ACTION_VIEW, linkUri)
        startActivity(kentoIntent)
    }

}