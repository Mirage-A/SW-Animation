package model

import java.io.Serializable

/**
 * Основной класс модели
 * Абстрактная сериализуемая модель, хранящая все данные об анимации
 */
class Animation(val type : AnimationType = AnimationType.NULL) : Serializable {
    /**
     * Список кадров анимации
     */
    var frames : ArrayList<Frame> = ArrayList()
    /**
     * Номер текущего кадра анимации
     */
    var curFrame : Int = -1
    /**
     * Текущее направление движения
     */
    var curMoveDirection = MoveDirection.RIGHT
    /**
     * Текущий тип оружия
     */
    var curWeaponType = WeaponType.ONE_HANDED
    /**
     * Название анимации
     */
    var name : String = "NO_NAME"
    /**
     * Длительность (период) анимации
     */
    var duration = 1000
    /**
     * Является ли анимация периодической (иначе она останавливается на последнем кадре)
     */
    var isRepeatable = true
    /**
     * Словарь из данных анимации: по moveDirection-у и WeaponType-у получаем список кадров
     */
    var data = HashMap<MoveDirection, HashMap<WeaponType, ArrayList<Frame>>>()

}