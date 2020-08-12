package com.nkkuma.shogiban2kif_android.model

class shogibanState {
    var teban: String = "true"
    var sente_mochi: Map<String, Int> = emptyMap()
    var gote_mochi: Map<String, Int> = emptyMap()
    var ban_result: Map<Int, String> = emptyMap()
    var points: List<List<Int>> = emptyList()
}