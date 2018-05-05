package jp.hotdrop.mds.repository.ex

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun String.toDateEpoch(): Long {
    val dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd")
    val ld = LocalDate.parse(this, dtf)
    return ld.toEpochDay()
}

fun String.toDateTimeEpoch(): Long {
    val dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
    val ldt = LocalDateTime.parse(this, dtf)
    return ldt.atZone(ZoneId.systemDefault()).toEpochSecond()
}

fun Long.toDateStr(): String {
    val dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd")
    return dtf.format(LocalDate.ofEpochDay(this))
}

fun Long.toDateTimeStr(): String {
    val dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
    val instant = Instant.ofEpochSecond(this)
    return dtf.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
}