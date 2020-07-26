package ru.skillbranch.kotlinexample

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(fullName: String, email: String, password: String): User {
        return User.makeUser(fullName, email = email, password = password)
            .also {
                if (map.containsKey(it.login)) {
                    throw IllegalArgumentException("A user with this email already exists")
                }
                map[it.login] = it
            }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        return User.makeUser(fullName, phone = rawPhone)
            .also {
                if (map.containsKey(it.login)) {
                    throw IllegalArgumentException("A user with this phone already exists")
                }
                map[it.login] = it
            }
    }

    fun loginUser(login: String, password: String): String? {
        return (map[login.trim()] ?: map[login.trimPhone()])?.let {
            if (it.checkPassword(password)) {
                it.userInfo
            } else {
                null
            }
        }
    }

    fun requestAccessCode(login: String): Unit {

    }

    fun importUsers(list: List<String>): List<User> {
        TODO()
    }

    fun clearHolder() {
        map.clear()
    }

}