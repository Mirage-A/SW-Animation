package ui

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
    internal var curLayer = -1
    internal var ram: BufferedImage = BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
    internal var layers: ArrayList<Layer>
    internal var zoom = 600
    internal val centerY = 106 - 64
    internal val pointRadius = 4
    internal var t: Timer

    init {
        layout = null
        layers = ArrayList()
        try {
            player = ImageIO.read(File("./icons/player.png"))
            player = player.getScaledInstance(player.getWidth(null) * zoom / 100, player.getHeight(null) * zoom / 100, Image.SCALE_SMOOTH)
            ram = ImageIO.read(File("./icons/ram.png"))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        t = Timer(50, ActionListener { repaint() })
        t.start()
    }

    public override fun paintComponent(gr: Graphics) {
        super.paintComponent(gr)
        val scrW = width
        val scrH = height
        for (i in layers.indices) {
            val layer = layers[i]
            val at = AffineTransform.getTranslateInstance(((layer.x - layer.scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(), ((layer.y - layer.scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
            at.rotate(-layer.rotationAngle, (layer.scaledWidth / 2 * zoom / 100).toDouble(), (layer.scaledHeight / 2 * zoom / 100).toDouble())
            val g2d = gr as Graphics2D
            g2d.drawImage(layer.basicImage.getScaledInstance(layer.scaledWidth * zoom / 100, layer.scaledHeight * zoom / 100, Image.SCALE_SMOOTH), at, null)
        }
        gr.color = Color.BLACK
        gr.drawLine(scrW / 2, 0, scrW / 2, scrH)
        gr.drawLine(0, scrH / 2 + centerY * zoom / 100, scrW, scrH / 2 + centerY * zoom / 100)
        if (curLayer != -1) {
            val layer = layers[curLayer]
            gr.color = Color.BLUE
            val g = gr as Graphics2D
            val at = AffineTransform.getTranslateInstance(((layer.x - layer.scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(), ((layer.y - layer.scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
            at.rotate(-layer.rotationAngle, (layer.scaledWidth / 2 * zoom / 100).toDouble(), (layer.scaledHeight / 2 * zoom / 100).toDouble())
            gr.drawImage(ram.getScaledInstance(layer.scaledWidth * zoom / 100, layer.scaledHeight * zoom / 100, Image.SCALE_SMOOTH), at, null)
        }
        if (drawPlayer) {
            gr.drawImage(player, scrW / 2 - player.getWidth(null) / 2, scrH / 2 - player.getHeight(null) / 2, null)
        }
    }
}
