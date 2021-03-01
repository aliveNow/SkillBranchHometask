package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    val result = mutableListOf<Int>()
    this?.let {
        if (substr.isNotEmpty()) {
            var startIndex = 0
            do {
                val index = indexOf(substr, startIndex, ignoreCase = ignoreCase)
                if (index >= 0) {
                    result.add(index)
                    startIndex = index + substr.length
                } else {
                    break
                }
            } while (true)
        }
        result
    }
    return result
}