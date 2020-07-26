package ru.skillbranch.kotlinexample

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(fullName: String, email: String, password: String): User {
        return User.makeUser(fullName, email = email, password = password)
            .also { addUser(it) }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        return User.makeUser(fullName, phone = rawPhone)
            .also { addUser(it) }
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

    fun requestAccessCode(login: String) {
        map[login.trimPhone()]?.requestAccessCode()
    }

    fun importUsers(list: List<String>): List<User> {
        val users = mutableListOf<User>()
        list.forEach { row ->
            val values = row.split(";", ":")
            if (values.size < 5) {
                throw IllegalArgumentException("Not valid csv format")
            }
            val fullName = values.first()
            val email = values[1].trim().takeIf { it.isNotBlank() }
            val salt = values[2]
            val passwordHash = values[3]
            val phone = values[4].trim().takeIf { it.isNotBlank() }
            users.add(
                User.importUser(fullName, email, phone, salt, passwordHash)
                    .also { addUser(it) }
            )
        }
        return users
    }

    fun clearHolder() {
        map.clear()
    }

    private fun addUser(user: User, email: String? = null, phone: String? = null) {
        if (map.containsKey(user.login)) {
            throw IllegalArgumentException(
                when {
                    email != null -> "A user with this email already exists: $email"
                    phone != null -> "A user with this phone already exists: $phone"
                    else -> "A user with this login already exists: ${user.login}"
                }
            )
        }
        map[user.login] = user
    }
}