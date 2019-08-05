package ru.skillbranch.devintensive.utils

object Utils {
    val exlude_list =  listOf("enterprise", "features", "topics", "collections", "trending",
        "events", "marketplace", "pricing", "nonprofit", "customer-stories", "security", "login", "join")

    public fun parseFullName(fullName: String? ): Pair<String?, String?> {
        val list_ = fullName?.split(" ")
        return Pair(if (list_?.getOrNull(0).isNullOrBlank()) null else list_?.getOrNull(0),
            if (list_?.getOrNull(1).isNullOrBlank()) null else list_?.getOrNull(1))
    }

    public fun toInitials(firstName: String?, lastName: String?): String? {
        return when {
            firstName.isNullOrBlank() && lastName.isNullOrBlank() -> null
            firstName.isNullOrBlank() -> lastName?.toUpperCase()?.get(0).toString()
            lastName.isNullOrBlank() -> firstName.toUpperCase().get(0).toString()
            else -> firstName.toUpperCase().get(0).toString() + lastName.toUpperCase().get(0).toString()
        }

    }

    public fun validateRepoName(name: String?) : Boolean {

        if (name.isNullOrBlank())
            return true

        var name_ = name.trim();
        if (name.takeLast(1) == "/")
            name_ = name_.substring(0,name_.length-1)

        val prefix = when {
            name_.startsWith("https://www.github.com/", ignoreCase = true) -> "https://www.github.com/"
            name_.startsWith("https://github.com/", ignoreCase = true) -> "https://github.com/"
            name_.startsWith("http://www.github.com/", ignoreCase = true) -> "http://www.github.com/"
            name_.startsWith("http://github.com/", ignoreCase = true) -> "http://github.com/"
            name_.startsWith("www.github.com/", ignoreCase = true) -> "www.github.com/"
            name_.startsWith("github.com/", ignoreCase = true) -> "github.com/"
            else -> "-"
        }

        if (prefix.length > 5) {
            val user = name_.substring(prefix.length)
            if (user.contains("/", ignoreCase = true) || user.contains(" ", ignoreCase = true))
                return false;
            if (user !in exlude_list)
                return true
        }
        return false
    }

    public fun transliteration(payload: String?, divider: String=" "): String? {
        val translit = hashMapOf(
            "а" to "a", "б" to "b", "в" to "v", "г" to "g", "д" to "d", "е" to "e",
            "ё" to "e", "ж" to "zh", "з" to "z", "и" to "i", "й" to "i", "к" to "k",
            "л" to "l", "м" to "m", "н" to "n", "о" to "o", "п" to "p", "р" to "r",
            "с" to "s", "т" to "t", "у" to "u", "ф" to "f", "х" to "h", "ц" to "c",
            "ч" to "ch", "ш" to "sh", "щ" to "sh'", "ъ" to "", "ы" to "i", "ь" to "",
            "э" to "e", "ю" to "yu", "я" to "ya",
            "А" to "A", "Б" to "B", "В" to "V", "Г" to "G", "Д" to "D", "Е" to "E",
            "Ё" to "E", "Ж" to "Zh", "З" to "Z", "И" to "I", "Й" to "I", "К" to "K",
            "Л" to "L", "М" to "M", "Н" to "N", "О" to "O", "П" to "P", "Р" to "R",
            "С" to "S", "Т" to "T", "У" to "U", "Ф" to "F", "Х" to "H", "Ц" to "C",
            "Ч" to "Ch", "Ш" to "SH", "Щ" to "Sh'", "Ъ" to "", "Ы" to "I", "Ь" to "",
            "Э" to "E", "Ю" to "YU", "Я" to "Ya"
            )
        if (payload == null)
            return null
        var result = ""
        payload.replace(Regex("\\s"), divider).toCharArray().forEach{
            val z = it.toString()
            result += when (translit.containsKey(z)) {
                true -> translit.get(z)
                false -> z
            }
        }
        return result
    }
}