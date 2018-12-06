package logic

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

class Layer (var imageName: String, var x : Float = 0f, var y : Float = 0f, var scale : Float = 1f, var scaleX : Float = 1f,
             var scaleY : Float = 1f, var angle : Float = 0f){
    var basicWidth: Int = 0
    var basicHeight: Int = 0
    var basicImage: BufferedImage = BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
    var layerName: String

    init {
        layerName = imageName.substring(0, imageName.length - 4)
        try {
            basicImage = ImageIO.read(File("./drawable/$imageName"))
            basicWidth = basicImage.width
            basicHeight = basicImage.height
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    }
}
