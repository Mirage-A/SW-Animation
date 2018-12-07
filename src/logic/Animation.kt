package logic

import java.io.Serializable

abstract class Animation : Serializable {
    var frames : ArrayList<Frame> = ArrayList()
    var curFrame : Int = -1
    var curMoveDirection = MoveDirection.RIGHT
    var curWeaponType = WeaponType.ONE_HANDED
    var name : String = "NO_NAME"
    /**
     * Словарь из данных анимации: по moveDirection-у и WeaponType-у получаем список кадров
     */
    var data = HashMap<MoveDirection, HashMap<WeaponType, ArrayList<Frame>>>()

}