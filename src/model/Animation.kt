package model

import java.io.File
import org.dom4j.io.SAXReader
import org.dom4j.DocumentHelper
import org.dom4j.Element
import java.io.FileWriter
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION
import view.MainPanel
import javax.swing.JOptionPane
import kotlin.system.exitProcess


/**
 * Хранит все данные об анимации
 */
class Animation() {
    /**
     * Тип анимации
     */
    @Transient var type : AnimationType = AnimationType.OBJECT
    /**
     * Список кадров анимации
     */
    var frames : ArrayList<Frame> = ArrayList()
    /**
     * Номер текущего кадра анимации
     */
    @Transient var curFrame : Int = -1
    /**
     * Текущее направление движения
     */
    @Transient var curMoveDirection = MoveDirection.RIGHT
    /**
     * Текущий тип оружия
     */
    @Transient var curWeaponType = WeaponType.UNARMED
    /**
     * Название анимации
     */
    @Transient var name : String = "NO_NAME"
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
        try {
            for (moveDirection in MoveDirection.values()) {
                data[moveDirection] = HashMap()
                for (weaponType in WeaponType.values()) {
                    data[moveDirection]!![weaponType] = ArrayList()
                }
            }
            val reader = SAXReader()
            val document = reader.read(swaFile)
            val animation = document.rootElement
            type = AnimationType.fromString(animation.attributeValue("type"))
            name = animation.attributeValue("name")
            duration = Integer.parseInt(animation.attributeValue("duration"))
            isRepeatable = animation.attributeValue("isRepeatable") == "true"
            MainPanel.objectX = animation.attributeValue("objectX")?.toFloatOrNull() ?: 0f
            MainPanel.objectY = animation.attributeValue("objectY")?.toFloatOrNull() ?: 0f
            MainPanel.objectWidth = animation.attributeValue("objectWidth")?.toFloatOrNull() ?: 0f
            MainPanel.objectHeight = animation.attributeValue("objectHeight")?.toFloatOrNull() ?: 0f
            for (md in animation.elements()) {
                md as Element
                val moveDirection = MoveDirection.fromString(md.name)
                for (wt in md.elements()) {
                    wt as Element
                    val weaponType = WeaponType.fromString(wt.name)
                    val framesArr = data[moveDirection]!![weaponType]!!
                    for (fr in wt.elements()) {
                        fr as Element
                        val frame = Frame()
                        for (lyr in fr.elements()) {
                            lyr as Element
                            val layer = Layer(
                                    lyr.attributeValue("imageName"),
                                    lyr.attributeValue("x").toFloat(),
                                    lyr.attributeValue("y").toFloat(),
                                    lyr.attributeValue("scale").toFloat(),
                                    lyr.attributeValue("scaleX").toFloat(),
                                    lyr.attributeValue("scaleY").toFloat(),
                                    lyr.attributeValue("angle").toFloat(),
                                    lyr.attributeValue("flipX")?.toBoolean() ?: false,
                                    lyr.attributeValue("isVisible")?.toBoolean() ?: true
                            )
                            frame.layers.add(layer)
                        }
                        framesArr.add(frame)
                    }
                }
            }

            frames = data[MoveDirection.RIGHT]!![WeaponType.UNARMED]!!
        }
        catch(ex: Exception) {
            JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
            exitProcess(0)
        }
    }

    /**
     * Сериализует анимацию в формате XML и записывает в файл
     */
    fun serialize(swaFile: File) {
        try {
            val document = DocumentHelper.createDocument()
            val animation = document.addElement("animation")
            animation.run {
                addAttribute("type", type.toString())
                addAttribute("name", this@Animation.name)
                addAttribute("duration", "" + duration)
                addAttribute("isRepeatable", "" + isRepeatable)
                if (type == AnimationType.OBJECT) {
                    addAttribute("objectX", "" + MainPanel.objectX)
                    addAttribute("objectY", "" + MainPanel.objectY)
                    addAttribute("objectWidth", "" + MainPanel.objectWidth)
                    addAttribute("objectHeight", "" + MainPanel.objectHeight)
                }
            }
            for (md in MoveDirection.values()) {
                val moveDirection = animation.addElement(md.toString())
                for (wt in WeaponType.values()) {
                    val weaponType = moveDirection.addElement(wt.toString())
                    val frames = data[md]!![wt]!!
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
                                addAttribute("flipX", "" + layer.flipX)
                                addAttribute("isVisible", "" + layer.isVisible)
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
        catch(ex : Exception) {
            JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
            exitProcess(0)
        }
    }
}