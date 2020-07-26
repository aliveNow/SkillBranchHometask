package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    var elementWasFound = false
    return this.asReversed().filter {
        if (elementWasFound) {
            true
        } else {
            if (predicate(it)) {
                elementWasFound = true
            }
            false
        }
    }.asReversed()
}