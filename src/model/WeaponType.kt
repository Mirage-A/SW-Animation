package model

import java.io.Serializable
import java.lang.Exception

/**
 * Тип оружия
 */
enum class WeaponType : Serializable{
    ONE_HANDED,
    ONE_HANDED_AND_SHIELD,
    DUAL,
    TWO_HANDED,
    BOW,
    STAFF;

    /**
     * Преобразует тип оружия в строку
     */
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
    companion object {
        /**
         * Преобразует строку в тип оружия (операция, обратная toString())
         * @throws Exception если строка не соответствует никакому типу оружия
         */
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
}