package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

enum class TimeUnits {
    SECOND, MINUTE, HOUR, DAY;
    public fun plural(value: Int) : String {
        return value.toString() + " " +
                when{
                    (this == SECOND) -> {
                        when {
                            (value != 11) && (value % 10 == 1)  -> "секунду"
                            value > 4 && ((value in 5..20) || (value % 10 in 5..9) || (value % 10 == 0)) -> "секунд"
                            else -> "секунды"
                        }
                    }
                    (this == MINUTE) -> {
                        when {
                            (value != 11) && (value % 10 == 1)  -> "минуту"
                            value > 4 && ((value in 5..20) || (value % 10 in 5..9) || (value % 10 == 0)) -> "минут"
                            else -> "минуты"
                        }
                    }
                    (this == HOUR) -> {
                        when {
                            (value != 11) && (value % 10 == 1)  -> "час"
                            value > 4 && ((value in 5..20) || (value % 10 in 5..9) || (value % 10 == 0)) -> "часов"
                            else -> "часа"
                        }
                    }
                    (this == DAY) -> {
                        when {
                            (value != 11) && (value % 10 == 1)  -> "день"
                            value > 4 && ((value in 5..20) || (value % 10 in 5..9) || (value % 10 == 0)) -> "дней"
                            else -> "дня"
                        }
                    }
                    else -> ""
                }
    }
}

fun Date.format(pattern:String ="HH:mm:ss dd.MM.yy"): String  {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time += when (units) {
        TimeUnits.SECOND -> value * 1000L
        TimeUnits.MINUTE -> value * 60_000L
        TimeUnits.HOUR -> value * 3_600_000L
        TimeUnits.DAY -> value * 86_400_000L
    }

    this.time = time
    return this
}

val minutS = listOf<Long>(2,3,4,22,23,24,32,33,34)
val minutU = listOf<Long>(21,31,41)
val hourA = listOf<Long>(2,3,4,22)
val hourOV = listOf<Long>(5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)

fun Date.humanizeDiff(): String? {
    var delta_: Long = Math.round((this.time - Date().time) / 1000.0)
    var timeUnit_ = TimeUnits.SECOND

    if (timeUnit_ == TimeUnits.SECOND && delta_.absoluteValue > 59) {
        timeUnit_ = TimeUnits.MINUTE
        delta_ /= 60
    }

    if (timeUnit_ == TimeUnits.MINUTE && delta_.absoluteValue > 59) {
        timeUnit_ = TimeUnits.HOUR
        delta_ /= 60
    }

    if (timeUnit_ == TimeUnits.HOUR && delta_.absoluteValue > 23) {
        timeUnit_ = TimeUnits.DAY
        delta_ /= 24
    }

    val deltaAbs = delta_.absoluteValue

    return when (timeUnit_) {
        TimeUnits.SECOND -> when(delta_) {
            in -1..1 -> "только что"
            in 2..45 -> "через несколько секунд"
            in -45..-2 -> "несколько секунд назад"
            in -60..-46 -> "минуту назад"
            in 46..60 -> "через минуту"
            else -> null
        }

        TimeUnits.MINUTE -> when {
            delta_ == -1L -> "минуту назад"
            delta_ == 1L -> "через минуту"
            minutS.contains(deltaAbs) -> when (delta_ < 0) {
                true -> "${deltaAbs} минуты назад"
                false -> "через ${deltaAbs} минуты"
            }
            minutU.contains(deltaAbs) -> when (delta_ < 0) {
                true -> "${deltaAbs} минуту назад"
                false -> "через ${deltaAbs} минуту"
            }
            delta_ in -60..-46 -> "час назад"
            delta_ in 46..60 -> "через час"
            delta_ in -45..-5 -> "${deltaAbs} минут назад"
            delta_ in 5..45 -> "через ${deltaAbs} минут"
            else -> null
        }

        TimeUnits.HOUR -> when {
            delta_ == -1L -> "час назад"
            delta_ == 1L -> "через час"
            delta_ == -21L -> "21 час назад"
            delta_ == 21L -> "через 21 час"
            hourA.contains(deltaAbs) -> when (delta_ < 0) {
                true -> "${deltaAbs} часа назад"
                false -> "через ${deltaAbs} часа"
            }
            hourOV.contains(deltaAbs) -> when (delta_ < 0) {
                true -> "${deltaAbs} часов назад"
                false -> "через ${deltaAbs} часов"
            }
            delta_ == -23L -> "день назад"
            delta_ ==  23L -> "через день"
            else -> null
        }

        TimeUnits.DAY -> when {
            delta_ == -1L -> "день назад"
            delta_ == 1L -> "через день"
            delta_ <-360 -> "более года назад"
            delta_ > 360 -> "более чем через год"
            deltaAbs % 10 in 2..4 ->  when (delta_ < 0) {
                true -> "${deltaAbs} дня назад"
                false -> "через ${deltaAbs} дня"
            }
            deltaAbs % 10 in 5..9 || deltaAbs % 10 == 0L ->  when (delta_ < 0) {
                true -> "${deltaAbs} дней назад"
                false -> "через ${deltaAbs} дней"
            }
            else -> null
        }
    }
}