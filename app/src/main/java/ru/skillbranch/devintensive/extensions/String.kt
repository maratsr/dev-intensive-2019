package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16) : String {
    return  when{
        this.trimEnd().length > len  -> this.substring(0, len).trimEnd() + "..."
        else -> this.substring(0, this.length).trimEnd()
    }
}

fun String.stripHtml() : String {
    return this.replace(Regex("<[^>]*>"), "")
        .replace("&lt;", "")
        .replace("&gt;", "")
        .replace("&nbsp;", "")
        .replace("&quot;", "")
        .replace("&nbsp;", "")
        .replace("&lsquo;", "")
        .replace("&rsquo;", "")
        .trim().replace(Regex(" +"), " ");


}