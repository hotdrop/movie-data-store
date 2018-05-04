package jp.hotdrop.mds.repository.ex

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.toEpoch(): String? =
     try {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val date = sdf.parse(this)
        date?.time.toString()
    } catch (e: ParseException) {
        null
    }

fun String.toDateStr(): String? {
    val sdf = SimpleDateFormat("yyyy/MM/dd")
    val date = Date(this.toLong())
    return sdf.format(date).toString()
}