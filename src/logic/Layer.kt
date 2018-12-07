package logic

import java.awt.image.BufferedImage
import java.io.File
import java.io.Serializable

import javax.imageio.ImageIO

class Layer (var imageName: String, var x : Float = 0f, var y : Float = 0f, var scale : Float = 1f, var scaleX : Float = 1f,
             var scaleY : Float = 1f, var angle : Float = 0f) : Serializable{
    var basicWidth: Int = 0
    var basicHeight: Int = 0
    var layerName: String
    @Transient
    var basicImage: BufferedImage? = null

    init {
        layerName = imageName.substring(0, imageName.length - 4)
        loadImage()
    }
    constructor(origin : Layer) : this(origin.imageName, origin.x, origin.y, origin.scale, origin.scaleX, origin.scaleY,
            origin.angle)

    fun loadImage() {
        try {
            basicImage = ImageIO.read(File("./drawable/$imageName"))
            basicWidth = basicImage!!.width
            basicHeight = basicImage!!.height
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
