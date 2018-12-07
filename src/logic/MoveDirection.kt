package logic

import java.io.Serializable
import java.lang.Exception

enum class MoveDirection : Serializable{
    RIGHT,
    UP_RIGHT,
    UP,
    UP_LEFT,
    LEFT,
    DOWN_LEFT,
    DOWN,
    DOWN_RIGHT;

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