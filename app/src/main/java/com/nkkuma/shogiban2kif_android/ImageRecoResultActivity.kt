package com.nkkuma.shogiban2kif_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
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

    fun onBackButtonClick(view: View) {
        val intent = Intent(this, ImageRecoActivity::class.java)
        startActivity(intent)
    }

    fun string2Class(response: String): shogibanState {
        // mapperオブジェクトを作成
        val mapper = jacksonObjectMapper()
        return mapper.readValue(response)
    }

    fun string2ImageButtonId(place: String): Int {
        return resources.getIdentifier("imageRecoResultBtn_$place","id", packageName)
    }

    fun string2FixButtonId(koma: String): Int {
        return resources.getIdentifier("fixKoma_$koma","id", packageName)
    }

    fun string2MochigomaSpinnerId(koma: String, teban: String): Int {
        return resources.getIdentifier("spinner_mochigoma${teban}_${koma}","id", packageName)
    }

    fun string2ImageSource(komaString: String): Int {
        val komaImgString = "koma_"+komaString.trim().replace("*","",true)
        return resources.getIdentifier(komaImgString,"drawable", packageName)
    }

    fun replaceBanKoma(place: String, koma: String) {
        val imageView = findViewById<ImageButton>(string2ImageButtonId(place))
        imageView.setImageResource(string2ImageSource(koma))
        imageView.tag = koma
    }

    fun initBanImage() {
        // init image
        for (i in 1..9) {
            for (j in 1..9) {
                replaceBanKoma((i*10+j).toString(), " * ")
            }
        }
    }

    fun setBanImage(result: shogibanState) {
        // init image
        initBanImage()
        // set image
        for ((place,koma) in result.ban_result) {
            replaceBanKoma(place.toString(), koma)
        }
    }

    fun initMochigomaSenteNumber() {
        // mochigoma_name
        val mochigoma_name = resources.getStringArray(R.array.komaname_shogibanState).slice(1..7)
        // init mochigoma_number
        for (koma in mochigoma_name) {
            val imageView = findViewById<Spinner>(string2MochigomaSpinnerId(koma, "sente"))
            imageView.setSelection(0)
            imageView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                // アイテムが選択された時の動作
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    // Spinner を取得
                    val spinner = parent as Spinner
                    onMochigomaChange(spinner)
                }

                // 何も選択されなかった時の動作
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
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
            imageView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                // アイテムが選択された時の動作
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    // Spinner を取得
                    val spinner = parent as Spinner
                    onMochigomaChange(spinner)
                }

                // 何も選択されなかった時の動作
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
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

    fun onMochigomaChange(spinner: Spinner) {
        if (spinner.selectedItem.toString() == "0") {
            spinner.alpha = 0.5F
        }
        else {
            spinner.alpha = 1.0F
        }
    }

    fun fixBanKoma_11(view: View) { fixBanKoma(view, "11")}
    fun fixBanKoma_12(view: View) { fixBanKoma(view, "12")}
    fun fixBanKoma_13(view: View) { fixBanKoma(view, "13")}
    fun fixBanKoma_14(view: View) { fixBanKoma(view, "14")}
    fun fixBanKoma_15(view: View) { fixBanKoma(view, "15")}
    fun fixBanKoma_16(view: View) { fixBanKoma(view, "16")}
    fun fixBanKoma_17(view: View) { fixBanKoma(view, "17")}
    fun fixBanKoma_18(view: View) { fixBanKoma(view, "18")}
    fun fixBanKoma_19(view: View) { fixBanKoma(view, "19")}
    fun fixBanKoma_21(view: View) { fixBanKoma(view, "21")}
    fun fixBanKoma_22(view: View) { fixBanKoma(view, "22")}
    fun fixBanKoma_23(view: View) { fixBanKoma(view, "23")}
    fun fixBanKoma_24(view: View) { fixBanKoma(view, "24")}
    fun fixBanKoma_25(view: View) { fixBanKoma(view, "25")}
    fun fixBanKoma_26(view: View) { fixBanKoma(view, "26")}
    fun fixBanKoma_27(view: View) { fixBanKoma(view, "27")}
    fun fixBanKoma_28(view: View) { fixBanKoma(view, "28")}
    fun fixBanKoma_29(view: View) { fixBanKoma(view, "29")}
    fun fixBanKoma_31(view: View) { fixBanKoma(view, "31")}
    fun fixBanKoma_32(view: View) { fixBanKoma(view, "32")}
    fun fixBanKoma_33(view: View) { fixBanKoma(view, "33")}
    fun fixBanKoma_34(view: View) { fixBanKoma(view, "34")}
    fun fixBanKoma_35(view: View) { fixBanKoma(view, "35")}
    fun fixBanKoma_36(view: View) { fixBanKoma(view, "36")}
    fun fixBanKoma_37(view: View) { fixBanKoma(view, "37")}
    fun fixBanKoma_38(view: View) { fixBanKoma(view, "38")}
    fun fixBanKoma_39(view: View) { fixBanKoma(view, "39")}
    fun fixBanKoma_41(view: View) { fixBanKoma(view, "41")}
    fun fixBanKoma_42(view: View) { fixBanKoma(view, "42")}
    fun fixBanKoma_43(view: View) { fixBanKoma(view, "43")}
    fun fixBanKoma_44(view: View) { fixBanKoma(view, "44")}
    fun fixBanKoma_45(view: View) { fixBanKoma(view, "45")}
    fun fixBanKoma_46(view: View) { fixBanKoma(view, "46")}
    fun fixBanKoma_47(view: View) { fixBanKoma(view, "47")}
    fun fixBanKoma_48(view: View) { fixBanKoma(view, "48")}
    fun fixBanKoma_49(view: View) { fixBanKoma(view, "49")}
    fun fixBanKoma_51(view: View) { fixBanKoma(view, "51")}
    fun fixBanKoma_52(view: View) { fixBanKoma(view, "52")}
    fun fixBanKoma_53(view: View) { fixBanKoma(view, "53")}
    fun fixBanKoma_54(view: View) { fixBanKoma(view, "54")}
    fun fixBanKoma_55(view: View) { fixBanKoma(view, "55")}
    fun fixBanKoma_56(view: View) { fixBanKoma(view, "56")}
    fun fixBanKoma_57(view: View) { fixBanKoma(view, "57")}
    fun fixBanKoma_58(view: View) { fixBanKoma(view, "58")}
    fun fixBanKoma_59(view: View) { fixBanKoma(view, "59")}
    fun fixBanKoma_61(view: View) { fixBanKoma(view, "61")}
    fun fixBanKoma_62(view: View) { fixBanKoma(view, "62")}
    fun fixBanKoma_63(view: View) { fixBanKoma(view, "63")}
    fun fixBanKoma_64(view: View) { fixBanKoma(view, "64")}
    fun fixBanKoma_65(view: View) { fixBanKoma(view, "65")}
    fun fixBanKoma_66(view: View) { fixBanKoma(view, "66")}
    fun fixBanKoma_67(view: View) { fixBanKoma(view, "67")}
    fun fixBanKoma_68(view: View) { fixBanKoma(view, "68")}
    fun fixBanKoma_69(view: View) { fixBanKoma(view, "69")}
    fun fixBanKoma_71(view: View) { fixBanKoma(view, "71")}
    fun fixBanKoma_72(view: View) { fixBanKoma(view, "72")}
    fun fixBanKoma_73(view: View) { fixBanKoma(view, "73")}
    fun fixBanKoma_74(view: View) { fixBanKoma(view, "74")}
    fun fixBanKoma_75(view: View) { fixBanKoma(view, "75")}
    fun fixBanKoma_76(view: View) { fixBanKoma(view, "76")}
    fun fixBanKoma_77(view: View) { fixBanKoma(view, "77")}
    fun fixBanKoma_78(view: View) { fixBanKoma(view, "78")}
    fun fixBanKoma_79(view: View) { fixBanKoma(view, "79")}
    fun fixBanKoma_81(view: View) { fixBanKoma(view, "81")}
    fun fixBanKoma_82(view: View) { fixBanKoma(view, "82")}
    fun fixBanKoma_83(view: View) { fixBanKoma(view, "83")}
    fun fixBanKoma_84(view: View) { fixBanKoma(view, "84")}
    fun fixBanKoma_85(view: View) { fixBanKoma(view, "85")}
    fun fixBanKoma_86(view: View) { fixBanKoma(view, "86")}
    fun fixBanKoma_87(view: View) { fixBanKoma(view, "87")}
    fun fixBanKoma_88(view: View) { fixBanKoma(view, "88")}
    fun fixBanKoma_89(view: View) { fixBanKoma(view, "89")}
    fun fixBanKoma_91(view: View) { fixBanKoma(view, "91")}
    fun fixBanKoma_92(view: View) { fixBanKoma(view, "92")}
    fun fixBanKoma_93(view: View) { fixBanKoma(view, "93")}
    fun fixBanKoma_94(view: View) { fixBanKoma(view, "94")}
    fun fixBanKoma_95(view: View) { fixBanKoma(view, "95")}
    fun fixBanKoma_96(view: View) { fixBanKoma(view, "96")}
    fun fixBanKoma_97(view: View) { fixBanKoma(view, "97")}
    fun fixBanKoma_98(view: View) { fixBanKoma(view, "98")}
    fun fixBanKoma_99(view: View) { fixBanKoma(view, "99")}

    fun fixBanKoma(view: View, place: String) {
        var alert = AlertDialog.Builder(this)
        alert.setTitle("修正ダイアログ")
        alert.setView(R.layout.alert_layout)
        var dialog = alert.create();
        dialog.show()

        val koma_: TextView? = dialog.findViewById(R.id.fixKoma_)
        koma_!!.setOnClickListener {
            // 駒を変える
            replaceBanKoma(place, " * ")
            // Dialogを消す
            dialog.dismiss();
        }

        val komaNames = resources.getStringArray(R.array.komaname_shogibanState).drop(1)
        for (komaName in komaNames) {
            val id = string2FixButtonId(komaName)
            val komaImageView: ImageView? = dialog.findViewById(id)
            komaImageView!!.setOnClickListener {
                // 駒を変える
                replaceBanKoma(place, komaName)
                // Dialogを消す
                dialog.dismiss();
            }
        }

        // alert.setView(alertView);
        // var dialog = alert.create();
        // dialog.show()
        // val komas: List<Int> = (11..99).toList().filter { it%10 > 0 }.map { string2ImageButtonId(it.toString()) }
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
        shogibanState.teban = "sente"
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
        var komas = mutableMapOf<String,String>()
        val kifkomas = resources.getStringArray(R.array.komaname_shogibanState)
        val sfenkomas = resources.getStringArray(R.array.komaname_sfen)
        for (i in 0..28) { komas[kifkomas[i]] = sfenkomas[i] }
        var kif_text    = ""
        val ban_result  = shogibanState.ban_result
        val sente_mochi = sort_mochigoma(shogibanState.sente_mochi, kifkomas)
        val gote_mochi  = sort_mochigoma(shogibanState.gote_mochi, kifkomas)
        val teban       = shogibanState.teban;

        // i = 行番号 // j = 列番号
        for (i in 1..9){
            var before_koma = ""
            for (j in 9 downTo 1){
                val tmpkoma = ban_result[j*10+i]!!.trim()
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

    fun jump2Piyo(view: View) {
        val sfen4Kento = URLEncoder.encode(shogibanState2Sfen(getFixedState()), "UTF-8")
        val linkUri = Uri.parse("piyoshogi://?sfen=position sfen $sfen4Kento moves ")
        val piyoIntent = Intent(Intent.ACTION_VIEW, linkUri)
        startActivity(piyoIntent)
    }

    fun jump2Twitter(view: View) {
        val sfen = shogibanState2Sfen(getFixedState())
        val imageURLCreateURL = "https://sfenreader-dot-shogiban2kif.appspot.com/twiimg?sfen="+Uri.parse(sfen)
        imageURLCreateURL.httpGet().response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    // レスポンスボディを表示
                    println("非同期処理の結果：" + String(response.data))
                    val imageURL = String(response.data)
                    val text = URLEncoder.encode("将棋盤局面認識結果:", "UTF-8")
                    val shareURL = URLEncoder.encode(imageURL, "UTF-8")
                    val hashtags = URLEncoder.encode("えぬっくま", "UTF-8")
                    val via = URLEncoder.encode("nkkuma_service", "UTF-8")
                    val linkUri = Uri.parse("http://twitter.com/share?url=${shareURL}&text=${text}&hashtags=${hashtags}&via=${via}")
                    val twitterIntent = Intent(Intent.ACTION_VIEW, linkUri)
                    startActivity(twitterIntent)
                }
                is Result.Failure -> {
                    println("通信に失敗しました。")
                }
            }
        }
    }
}