package model

import java.io.Serializable
import java.lang.Exception

/**
 * Тип анимации
 */
enum class AnimationType : Serializable{
    NULL,
    BODY,
    LEGS;

    /**
     * Преобразует тип оружия в строку
     */
    override fun toString() : String {
        return when (this) {
            NULL -> "NULL"
            BODY -> "BODY"
            LEGS -> "LEGS"
        }
    }
    companion object {
        /**
         * Преобразует строку в тип оружия (операция, обратная toString())
         * @throws Exception если строка не соответствует никакому типу оружия
         */
        fun fromString(str: String) : AnimationType {
            return when (str) {
                "NULL" -> NULL
                "BODY" -> BODY
                "LEGS" -> LEGS
                else -> throw Exception("Incorrect animation type: $str")
            }
        }
    }
}