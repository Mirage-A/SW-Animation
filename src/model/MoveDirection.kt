package model

import java.io.Serializable
import java.lang.Exception

/**
 * Направление движения
 */
enum class MoveDirection : Serializable{
    RIGHT,
    UP_RIGHT,
    UP,
    UP_LEFT,
    LEFT,
    DOWN_LEFT,
    DOWN,
    DOWN_RIGHT;

    /**
     * Преобразует направление движения в строку
     */
    override fun toString() : String {
        return when (this) {
            RIGHT -> "RIGHT"
            UP_RIGHT -> "UP_RIGHT"
            UP -> "UP"
            UP_LEFT -> "UP_LEFT"
            LEFT -> "LEFT"
            DOWN_LEFT -> "DOWN_LEFT"
            DOWN -> "DOWN"
            DOWN_RIGHT -> "DOWN_RIGHT"
        }
    }

    /**
     * Возвращает направление движения, обратное к данному относительно вертикальной оси
     */
    fun mirrored() : MoveDirection {
        return when (this) {
            RIGHT -> LEFT
            UP_RIGHT -> UP_LEFT
            UP -> DOWN
            UP_LEFT -> UP_RIGHT
            LEFT -> RIGHT
            DOWN_LEFT -> DOWN_RIGHT
            DOWN -> UP
            DOWN_RIGHT -> DOWN_LEFT
        }
    }

    companion object {
        /**
         * Преобразует строку в направление движения (операция, обратная toString())
         * @throws Exception если строка не соответствует никакому направлению движения
         */
        fun fromString(str: String) : MoveDirection {
            return when (str) {
                "RIGHT" -> RIGHT
                "UP_RIGHT" -> UP_RIGHT
                "UP" -> UP
                "UP_LEFT" -> UP_LEFT
                "LEFT" -> LEFT
                "DOWN_LEFT" -> DOWN_LEFT
                "DOWN" -> DOWN
                "DOWN_RIGHT" -> DOWN_RIGHT
                else -> throw Exception("Incorrect move direction: $str")
            }
        }
    }
}