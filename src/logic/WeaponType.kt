package logic

import java.lang.Exception

enum class WeaponType {
    ONE_HANDED,
    ONE_HANDED_AND_SHIELD,
    DUAL,
    TWO_HANDED,
    BOW,
    STAFF;

    override fun toString() : String {
        return when (this) {
            ONE_HANDED -> "ONE_HANDED"
            ONE_HANDED_AND_SHIELD -> "ONE_HANDED_AND_SHIELD"
            DUAL -> "DUAL"
            TWO_HANDED -> "TWO_HANDED"
            BOW -> "BOW"
            STAFF -> "STAFF"
        }
    }

    fun fromString(str: String) : WeaponType {
        return when (str) {
            "ONE_HANDED" -> ONE_HANDED
            "ONE_HANDED_AND_SHIELD" -> ONE_HANDED_AND_SHIELD
            "DUAL" -> DUAL
            "TWO_HANDED" -> TWO_HANDED
            "BOW" -> BOW
            "STAFF" -> STAFF
            else -> throw Exception("Incorrect weapon type: $str")
        }
    }
}