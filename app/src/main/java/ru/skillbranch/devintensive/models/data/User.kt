package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.humanizeDiff
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(val id : String,
                var firstName : String?,
                var lastName : String?,
                var avatar : String?,
                var rating : Int = 0,
                var respect : Int = 0,
                var lastVisit : Date? = null,
                var isOnline : Boolean = false) {
    fun toUserItem(): UserItem {
        val lastActivity = when {
            lastVisit == null -> "Еще ни разу не заходил"
            isOnline -> "online"
            else -> "Последний раз был ${lastVisit!!.humanizeDiff()}"
        }
        return UserItem(
            id,
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            Utils.toInitials(firstName, lastName),
            avatar,
            lastActivity,
            false,
            isOnline
        )
    }

    constructor(id: String) : this(id, "John", "Doe")

    constructor(id : String, firstName : String?, lastName : String?) : this(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatar = null
    )

    private constructor(builder: Builder) : this(builder.id, builder.firstName, builder.lastName, builder.avatar,
        builder.rating, builder.respect, builder.lastVisit, builder.isOnline)

    companion object Factory{
        private var lastId: Int = -1
        fun makeUser(fullName: String?) : User {
            lastId++
            val names = Utils.parseFullName(fullName)
            return User(
                id = lastId.toString(),
                firstName = names.first,
                lastName = names.second
            )
        }
    }

    class Builder{
        var id : String = ""
        var firstName : String? = null
        var lastName : String? = null
        var avatar : String? = null
        var rating : Int = 0
        var respect : Int = 0
        var lastVisit : Date? = Date()
        var isOnline : Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun firstName(firstName: String?) = apply { this.firstName = firstName }
        fun lastName(lastName: String?) = apply { this.lastName = lastName }
        fun avatar(avatar: String?) = apply { this.avatar = avatar }
        fun rating(rating: Int) = apply { this.rating = rating }
        fun respect(respect: Int) = apply { this.respect = respect }
        fun lastVisit(lastVisit: Date?) = apply { this.lastVisit = lastVisit }
        fun isOnline(isOnline: Boolean) = apply { this.isOnline = isOnline }
        fun build() = User(this)
    }
}