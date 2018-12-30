package model

import java.awt.image.BufferedImage
import java.io.File
import java.io.Serializable

import javax.imageio.ImageIO

/**
 * Слой на кадре анимации
 */
class Layer (var imageName: String, var x : Float = 0f, var y : Float = 0f, var scale : Float = 1f, var scaleX : Float = 1f,
             var scaleY : Float = 1f, var angle : Float = 0f) : Serializable{
    /**
     * Размеры изображения слоя до скалирования
     */
    var basicWidth: Int = 0
    var basicHeight: Int = 0
    /**
     * Изображение слоя
     */
    @Transient
    var basicImage: BufferedImage? = null

    init {
        loadImage()
    }
    constructor(origin : Layer) : this(origin.imageName, origin.x, origin.y, origin.scale, origin.scaleX, origin.scaleY,
            origin.angle)

    /**
     * Загрузка изображения слоя из файла по значению imageName
     */
    fun loadImage() {
        try {
            basicImage = ImageIO.read(File("./drawable/$imageName"))
            basicWidth = basicImage!!.width
            basicHeight = basicImage!!.height
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * Обрезает формат изображения и возвращает название слоя
     */
    fun getName() = imageName.substring(0, imageName.length - 4)


    /**
     * Отражает этот слой относительно вертикальной оси
     * Также меняет right и left слои местами
     */
    fun mirror() {
        x *= -1
        angle *= -1
        imageName = when (true) {
            imageName.startsWith("left") -> "right" + imageName.substring(4)
            imageName.startsWith("right") -> "left" + imageName.substring(5)
            imageName.startsWith("head") -> imageName.substring(0..3) +
                    MoveDirection.fromString(imageName.substring(4, imageName.length - 4)).mirrored().toString() +
                    imageName.substring(imageName.length - 4)
            else -> imageName
        }
        loadImage()
    }
}
