package model

import java.io.File
import org.dom4j.io.SAXReader
import org.dom4j.DocumentHelper
import java.io.FileWriter
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter




/**
 * Основной класс модели
 * Абстрактная модель, хранящая все данные об анимации
 */
class Animation() {
    /**
     * Тип анимации
     */
    var type : AnimationType = AnimationType.NULL
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

    constructor(type: AnimationType) : this() {
        this.type = type
    }

    /**
     * Конструктор считывания анимации из файла XML (.swa)
     */
    constructor(swaFile : File) : this() {
        val reader = SAXReader()
        val document = reader.read(swaFile)

        frames = data[MoveDirection.RIGHT]!![WeaponType.ONE_HANDED]!!
    }

    /**
     * Сериализует анимацию в формате XML и записывает в файл
     */
    fun serialize(swaFile: File) {
        val document = DocumentHelper.createDocument()

        val animation = document.addElement("animation")
        animation.run {
            addAttribute("type", type.toString())
            addAttribute("name", name)
            addAttribute("duration", "" + duration)
            addAttribute("isRepeatable", "" + isRepeatable)
        }
        val anim = animation.addElement("data")
        for (md in MoveDirection.values()) {
            val moveDirection = anim.addElement(md.toString())
            for (wt in WeaponType.values()) {
                val weaponType = moveDirection.addElement(wt.toString())
                val frames = data[md]!![wt]!!
                weaponType.addAttribute("framesCount", "" + frames.size)
                for (frameIndex in frames.indices) {
                    val frame = frames[frameIndex]
                    val fr = weaponType.addElement("frame$frameIndex")
                    fr.addAttribute("layersCount", "" + frame.layers.size)
                    for (layerIndex in frame.layers.indices) {
                        val layer = frame.layers[layerIndex]
                        fr.addElement("layer$layerIndex").run {
                            addAttribute("imageName", layer.imageName)
                            addAttribute("x", "" + layer.x)
                            addAttribute("y", "" + layer.y)
                            addAttribute("scale", "" + layer.scale)
                            addAttribute("scaleX", "" + layer.scaleX)
                            addAttribute("scaleY", "" + layer.scaleY)
                            addAttribute("angle", "" + layer.angle)
                        }
                    }
                }
            }
        }

        val fileWriter = FileWriter(swaFile)
        var encoding = document.xmlEncoding

        if (encoding == null)
            encoding = "UTF-8"

        val outputFormat = OutputFormat("  ", true, encoding)
        val writer = XMLWriter(fileWriter, outputFormat)
        writer.write(document)
        writer.close()
    }
}