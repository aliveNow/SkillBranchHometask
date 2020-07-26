package ru.skillbranch.kotlinexample

class User private constructor(
    val firstName: String,
    val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
) {

    val userInfo: String
    val fullName: String
    val initials: String
    var phone: String?
    var login: String
    val salt: String
    var passwordHash: String
    var accessCode: String? //@VisibleForTesting(otherwise = VisibleForTesting.NONE)

}