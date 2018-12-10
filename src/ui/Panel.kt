package ui

import logic.Frame
import logic.Layer
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.ActionListener
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.util.ArrayList

import javax.imageio.ImageIO
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.Timer

class Panel : JPanel() {
    internal var drawPlayer = true
    internal var verticalScroll: JScrollPane? = null
    internal var horizontalScroll: JScrollPane? = null
    internal var player: Image = BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
    private var ram: BufferedImage = BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
    var frame : Frame? = null
    var frames : ArrayList<Frame> = ArrayList()
    internal var zoom = 600
    internal val centerY = 106 - 64
    internal val pointRadius = 4
    internal var t: Timer
    internal var isPlayingAnimation = false
    var startTime = 0L
    var isRepeatable : Boolean = false
    var duration = 1L

    init {
        layout = null
        try {
            player = ImageIO.read(File("./icons/player.png"))
            player = player.getScaledInstance(player.getWidth(null) * zoom / 100, player.getHeight(null) * zoom / 100, Image.SCALE_SMOOTH)
            ram = ImageIO.read(File("./icons/ram.png"))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        t = Timer(1000 / 60, ActionListener { repaint() })
    }

    public override fun paintComponent(gr: Graphics) {
        super.paintComponent(gr)
        val scrW = width
        val scrH = height
        if (isPlayingAnimation) {
            if (frames.isEmpty()) return
            var timePassed = System.currentTimeMillis() - startTime
            if (timePassed >= duration) {
                if (isRepeatable) {
                    timePassed %= duration
                }
                else {
                    timePassed = duration
                }
                /*timePassed = when (true) {
                    isRepeatable -> 0
                    else -> duration - 1
                }*/
            }
            if (frames.size == 1 || timePassed == duration) {
                val curFrame = frames[frames.size - 1]
                val layers = curFrame.layers
                for (i in layers.indices) {
                    val layer = layers[i]
                    if (layer.basicImage == null) {
                        layer.loadImage()
                    }
                    val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
                    val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
                    val at = AffineTransform.getTranslateInstance(((layer.x - scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(), ((layer.y - scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
                    at.rotate(-layer.angle.toDouble(), (scaledWidth / 2 * zoom / 100).toDouble(), (scaledHeight / 2 * zoom / 100).toDouble())
                    val g2d = gr as Graphics2D
                    g2d.drawImage(layer.basicImage!!.getScaledInstance(Math.round(scaledWidth * zoom / 100), Math.round(scaledHeight * zoom / 100), Image.SCALE_SMOOTH), at, null)
                }
            }
            else {
                val interval = duration / (frames.size - 1f)
                val fr1 = frames[Math.min((timePassed / interval).toInt(), frames.size - 2)]
                val fr2 = frames[Math.min((timePassed / interval + 1).toInt(), frames.size - 1)]
                val progress = (timePassed.toFloat() % interval) / interval
                val layers1 = fr1.layers
                val layers2 = fr2.layers
                for (i in layers1.indices) {
                    val layer1 = layers1[i]
                    val layer2 = layers2[i]
                    if (layer1.basicImage == null) {
                        layer1.loadImage()
                    }
                    if (layer2.basicImage == null) {
                        layer2.loadImage()
                    }
                    val scaledWidth1 = layer1.basicWidth * layer1.scale * layer1.scaleX
                    val scaledHeight1 = layer1.basicHeight * layer1.scale * layer1.scaleY
                    val scaledWidth2 = layer2.basicWidth * layer2.scale * layer2.scaleX
                    val scaledHeight2 = layer2.basicHeight * layer2.scale * layer2.scaleY
                    val angle1 = layer1.angle
                    val angle2 = layer2.angle
                    val x = curValue(layer1.x, layer2.x, progress)
                    val y = curValue(layer1.y, layer2.y, progress)
                    val w = curValue(scaledWidth1, scaledWidth2, progress)
                    val h = curValue(scaledHeight1, scaledHeight2, progress)
                    val angle = curValue(angle1, angle2, progress)
                    val at = AffineTransform.getTranslateInstance(((x - w / 2) * zoom / 100 + scrW / 2).toDouble(), ((y - h / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
                    at.rotate(-angle.toDouble(), (w / 2 * zoom / 100).toDouble(), (h / 2 * zoom / 100).toDouble())
                    val g2d = gr as Graphics2D
                    g2d.drawImage(layer1.basicImage!!.getScaledInstance(Math.round(w * zoom / 100), Math.round(h * zoom / 100), Image.SCALE_SMOOTH), at, null)
                }

            }
        }
        else {
            if (frame == null) return
            val layers = frame!!.layers
            for (i in layers.indices) {
                val layer = layers[i]
                if (layer.basicImage == null) {
                    layer.loadImage()
                }
                val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
                val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
                val at = AffineTransform.getTranslateInstance(((layer.x - scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(), ((layer.y - scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
                at.rotate(-layer.angle.toDouble(), (scaledWidth / 2 * zoom / 100).toDouble(), (scaledHeight / 2 * zoom / 100).toDouble())
                val g2d = gr as Graphics2D
                g2d.drawImage(layer.basicImage!!.getScaledInstance(Math.round(scaledWidth * zoom / 100), Math.round(scaledHeight * zoom / 100), Image.SCALE_SMOOTH), at, null)
            }
        }
        gr.color = Color.BLACK
        gr.drawLine(scrW / 2, 0, scrW / 2, scrH)
        gr.drawLine(0, scrH / 2 + centerY * zoom / 100, scrW, scrH / 2 + centerY * zoom / 100)
        if (!isPlayingAnimation && frame!!.curLayer != -1) {
            val layer = frame!!.layers[frame!!.curLayer]
            val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
            val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
            gr.color = Color.BLUE
            val g = gr as Graphics2D
            val at = AffineTransform.getTranslateInstance(((layer.x - scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(), ((layer.y - scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
            at.rotate(-layer.angle.toDouble(), (scaledWidth / 2 * zoom / 100).toDouble(), (scaledHeight / 2 * zoom / 100).toDouble())
            gr.drawImage(ram.getScaledInstance(Math.round(scaledWidth * zoom / 100), Math.round(scaledHeight * zoom / 100), Image.SCALE_SMOOTH), at, null)
        }
        if (drawPlayer) {
            gr.drawImage(player, scrW / 2 - player.getWidth(null) / 2, scrH / 2 - player.getHeight(null) / 2, null)
        }
    }

    fun curValue(startValue: Float, endValue : Float, progress : Float) : Float {
        return startValue + (endValue - startValue) * progress
    }
}
