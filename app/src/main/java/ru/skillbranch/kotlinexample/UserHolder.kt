package ru.skillbranch.kotlinexample

object UserHolder {

    fun registerUser(fullName: String, email: String, password: String): User {
        TODO()
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        TODO()
    }

    fun loginUser(login: String, password: String): String {
        TODO()
    }

    fun requestAccessCode(login: String): Unit {

    }

    fun clearHolder() {

    }

    fun importUsers(list: List<String>): List<User> {
        TODO()
    }

}