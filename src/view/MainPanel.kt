package view

import model.AnimationType
import model.Frame
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
import javax.swing.Timer

/**
 * Основной класс вида
 * Представляет собой наследника JPanel, на которой рисуется анимация
 */
object MainPanel : JPanel() {
    /**
     * Включено ли рисование изображения гуманоида в редакторе
     */
    internal var drawPlayer = true

    var colorPlayer = true
    /**
     * Изображение гуманоида
     */
    internal var player: Image = BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
    /**
     * Изображение синей рамки, отрисовываемое поверх активного слоя
     */
    private var ram: BufferedImage = BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
    /**
     * Текущий кадр, который отрисовывается на панели, если анимация отключена
     */
    var frame : Frame? = null
    /**
     * Список кадров, которые анимируются, если анимация включена
     */
    var frames : ArrayList<Frame> = ArrayList()
    /**
     * Коэффициент увеличения кадра в редакторе (в процентах)
     */
    internal var zoom = 600
    /**
     * Смещение центра координат по вертикальной оси относительно центра экрана (без учёта zoom-а)
     */
    internal val centerY = 0
    /**
     * Таймер перерисовки панели
     */
    internal var t: Timer
    /**
     * Включена ли анимация
     */
    var isPlayingAnimation = false
    /**
     * Время старта анимации
     */
    var startTime = 0L
    /**
     * Является ли анимация повторяемой (иначе она останавливается на последнем кадре)
     */
    var isRepeatable : Boolean = false
    /**
     * Длительность (период) анимации
     */
    var duration = 1L

    /**
     * Тип анимации
     */
    var animType = AnimationType.OBJECT

    init {
        layout = null
        try {
            player = ImageIO.read(File("./drawable/player.png"))
            player = player.getScaledInstance(player.getWidth(null) * zoom / 100, player.getHeight(null) * zoom / 100, Image.SCALE_SMOOTH)
            ram = ImageIO.read(File("./icons/ram.png"))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        t = Timer(1000 / 60, ActionListener { repaint() })
    }

    /**
     * Метод перерисовки панели
     */
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
                    val img = if (colorPlayer) layer.coloredImage else layer.basicImage
                    g2d.drawImage(img!!.mirroredX(layer.flipX).getScaledInstance(Math.round(scaledWidth * zoom / 100), Math.round(scaledHeight * zoom / 100), Image.SCALE_SMOOTH), at, null)
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
                    val angle1 = (layer1.angle % (2 * Math.PI)).toFloat()
                    var angle2 = (layer2.angle % (2 * Math.PI)).toFloat()
                    if (angle2 > angle1) {
                        if (angle1  - angle2  + 2 * Math.PI < angle2 - angle1) {
                            angle2 -= 2 * Math.PI.toFloat()
                        }
                    }
                    if (angle2 < angle1) {
                        if (angle2  - angle1  + 2 * Math.PI < angle1 - angle2) {
                            angle2 += 2 * Math.PI.toFloat()
                        }
                    }
                    val x = curValue(layer1.x, layer2.x, progress)
                    val y = curValue(layer1.y, layer2.y, progress)
                    val w = curValue(scaledWidth1, scaledWidth2, progress)
                    val h = curValue(scaledHeight1, scaledHeight2, progress)
                    val angle = curValue(angle1, angle2, progress)
                    val at = AffineTransform()
                    at.translate(((x - w / 2) * zoom / 100 + scrW / 2).toDouble(), ((y - h / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
                    at.rotate(-angle.toDouble(), (w / 2 * zoom / 100).toDouble(), (h / 2 * zoom / 100).toDouble())
                    //TODO Flip
                    val g2d = gr as Graphics2D
                    val img = if (colorPlayer) layer1.coloredImage else layer1.basicImage
                    g2d.drawImage(img!!.mirroredX(layer1.flipX).getScaledInstance(Math.round(w * zoom / 100), Math.round(h * zoom / 100), Image.SCALE_SMOOTH), at, null)
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
                val at = AffineTransform()
                //at.scale(-1.0, -1.0)
                //TODO Flip
                at.translate(((layer.x - scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(),
                        ((layer.y - scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
                at.rotate(-layer.angle.toDouble(), (scaledWidth / 2 * zoom / 100).toDouble(), (scaledHeight / 2 * zoom / 100).toDouble())
                val g2d = gr as Graphics2D
                val img = if (colorPlayer) layer.coloredImage else layer.basicImage
                g2d.drawImage(img!!.mirroredX(layer.flipX).getScaledInstance(Math.round(scaledWidth * zoom / 100), Math.round(scaledHeight * zoom / 100), Image.SCALE_SMOOTH), at, null)
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
            gr as Graphics2D
            val at = AffineTransform.getTranslateInstance(((layer.x - scaledWidth / 2) * zoom / 100 + scrW / 2).toDouble(), ((layer.y - scaledHeight / 2 + centerY) * zoom / 100 + scrH / 2).toDouble())
            at.rotate(-layer.angle.toDouble(), (scaledWidth / 2 * zoom / 100).toDouble(), (scaledHeight / 2 * zoom / 100).toDouble())
            gr.drawImage(ram.getScaledInstance(Math.round(scaledWidth * zoom / 100), Math.round(scaledHeight * zoom / 100), Image.SCALE_SMOOTH), at, null)
        }
        if (drawPlayer) {
            if (animType == AnimationType.LEGS) {
                gr.drawImage(player, scrW / 2 - player.getWidth(null) / 2, scrH / 2 - player.getHeight(null) + 14 * zoom / 100, null)
            }
            else if (animType == AnimationType.BODY) {
                gr.drawImage(player, scrW / 2 - player.getWidth(null) / 2, scrH / 2 - player.getHeight(null) / 2, null)
            }
        }
    }

    /**
     * Возвращает значение между startValue и endValue, причем степень перехода от первого ко второму равна progress
     * (если progress == 0, то возвращается startValue, если progress == 1, то возвращается endValue,
     * если progress == 0.5, то возвращается их среднее арифметическое и т.д.)
     */
    private fun curValue(startValue: Float, endValue : Float, progress : Float) : Float {
        return startValue + (endValue - startValue) * progress
    }

    private fun BufferedImage.mirroredX(flipX: Boolean): BufferedImage {
        if (!flipX) return this
        val img = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB)
        for (i in 0 until this.width) {
            for (j in 0 until this.height) {
                img.setRGB(i, j, this.getRGB(this.width - i - 1, j))
            }
        }
        return img
    }

}
