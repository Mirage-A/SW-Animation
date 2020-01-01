package model

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

/**
 * Слой на кадре анимации
 */
class Layer (
        var imageName: String,
        var x : Float = 0f,
        var y : Float = 0f,
        var scale : Float = 1f,
        var scaleX : Float = 1f,
        var scaleY : Float = 1f,
        var angle : Float = 0f,
        var flipX: Boolean = false
) {
    /**
     * Размеры изображения слоя до скалирования
     */
    @Transient var basicWidth: Int = 0
    @Transient var basicHeight: Int = 0
    /**
     * Изображение слоя
     */
    @Transient var basicImage: BufferedImage? = null

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
            basicImage = ImageIO.read(File("./drawable/$imageName.png"))
            basicWidth = basicImage!!.width
            basicHeight = basicImage!!.height
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    /**
     * Отражает этот слой относительно вертикальной оси
     * Также меняет right и left слои местами
     */
    fun mirror(md: MoveDirection) {
        //TODO implement
        /*
        x *= -1
        angle *= -1
        if (imageName.startsWith("head")) {
            imageName = imageName.substring(0..3) +
                    MoveDirection.fromString(imageName.substring(4, imageName.length - 4)).mirrored().toString() +
                    imageName.substring(imageName.length - 4)
        }
        if (md == MoveDirection.DOWN_RIGHT || md == MoveDirection.DOWN_LEFT ||
                md == MoveDirection.UP_LEFT || md == MoveDirection.UP_RIGHT) {
            if (imageName.startsWith("left")) {
                imageName = "right" + imageName.substring(4)
            }
            else if (imageName.startsWith("right")) {
                imageName = "left" + imageName.substring(5)
            }
        }
        loadImage()*/
    }
}
