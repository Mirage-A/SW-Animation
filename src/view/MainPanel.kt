package view

import model.AnimationType
import model.Frame
import java.awt.*
import java.awt.event.ActionListener
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import java.util.ArrayList

import javax.imageio.ImageIO
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    var showTileGrid = true

    var objectX = 0f
    var objectY = 0f
    var objectWidth: Float = 0f
    var objectHeight: Float = 0f
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
                    if (!layer.isVisible) continue
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
                    if (!layer1.isVisible || !layer2.isVisible) continue
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
                if (!layer.isVisible) continue
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
        if (!isPlayingAnimation && frame!!.curLayer != -1) {
            val layer = frame!!.layers[frame!!.curLayer]
            val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX + 4f
            val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY + 4f
            gr.color = Color.BLUE
            gr as Graphics2D
            val halfDiag = sqrt(scaledWidth * scaledWidth + scaledHeight * scaledHeight) / 2f
            var point1 = Point(0f, 0f)
            var point2 = Point(0f, 0f)
            val angle = layer.angle
            var part = angle / Math.PI.toFloat() % 2f
            if (part < 0) part += 2f
            when {
                part == 0f || part == 1f -> {
                    point1 = Point(-scaledWidth / 2f, scaledHeight / 2f)
                    point2 = Point(scaledWidth / 2f, scaledHeight / 2f)
                }
                part == 0.5f || part == 1.5f -> {
                    point1 = Point(-scaledHeight / 2f, scaledWidth / 2f)
                    point2 = Point(scaledHeight / 2f, scaledWidth / 2f)
                }
                part in 0f..0.5f || part in 1f..1.5f -> {
                    val rightCenterPoint = Point(scaledWidth / 2f * cos(angle), scaledWidth / 2f * sin(angle))
                    val angle2 = Math.PI.toFloat() / 2f - angle
                    point1 = Point(rightCenterPoint.x - scaledHeight / 2f * cos(angle2), rightCenterPoint.y + scaledHeight / 2f * sin(angle2))
                    point2 = Point(rightCenterPoint.x + scaledHeight / 2f * cos(angle2), rightCenterPoint.y - scaledHeight / 2f * sin(angle2))
                }
                part in 0.5f..1f || part in 1.5f..2f -> {
                    val rightCenterPoint = Point(scaledWidth / 2f * cos(angle), scaledWidth / 2f * sin(angle))
                    val angle2 = Math.PI.toFloat() / 2f - angle
                    point1 = Point(rightCenterPoint.x - scaledHeight / 2f * cos(angle2), rightCenterPoint.y + scaledHeight / 2f * sin(angle2))
                    point2 = Point(rightCenterPoint.x + scaledHeight / 2f * cos(angle2), rightCenterPoint.y - scaledHeight / 2f * sin(angle2))
                }
                else -> {}
            }
            gr.drawPolygon(
                    intArrayOf(
                            scrW / 2 + (layer.x + point1.x).toInt() * zoom / 100,
                            scrW / 2 + (layer.x + point2.x).toInt() * zoom / 100,
                            scrW / 2 + (layer.x - point1.x).toInt() * zoom / 100,
                            scrW / 2 + (layer.x - point2.x).toInt() * zoom / 100
                    ),
                    intArrayOf(
                            scrH / 2 - (-layer.y + point1.y).toInt() * zoom / 100,
                            scrH / 2 - (-layer.y + point2.y).toInt() * zoom / 100,
                            scrH / 2 - (-layer.y - point1.y).toInt() * zoom / 100,
                            scrH / 2 - (-layer.y - point2.y).toInt() * zoom / 100
                    ),
                    4
            )
        }
        when {
            animType == AnimationType.LEGS && drawPlayer ->
                gr.drawImage(player, scrW / 2 - player.getWidth(null) / 2, scrH / 2 - player.getHeight(null) + 14 * zoom / 100, null)
            animType == AnimationType.BODY && drawPlayer ->
                gr.drawImage(player, scrW / 2 - player.getWidth(null) / 2, scrH / 2 - player.getHeight(null) / 2, null)
            animType == AnimationType.OBJECT && showTileGrid -> {
                val sceneCenter = Point(objectX, objectY)
                val virtualCenter = getVirtualScreenPointFromScene(sceneCenter)
                val xShift = virtualCenter.x % TILE_WIDTH
                val yShift = - virtualCenter.y % TILE_HEIGHT
                gr.color = Color.BLACK
                val sceneWidth = ((this.height * zoom / 100) / TILE_HEIGHT + 2).toInt()
                val sceneHeight = ((this.width * zoom / 100) / TILE_WIDTH + 2).toInt()
                for (sceneX in -sceneWidth..sceneWidth) {
                    val left = getVirtualScreenPointFromScene(Point(sceneX.toFloat(), -sceneHeight.toFloat()))
                    val right = getVirtualScreenPointFromScene(Point(sceneX.toFloat(), sceneHeight.toFloat()))
                    gr.drawLine(
                            scrW / 2 + (left.x + xShift).toInt() * zoom / 100,
                            scrH / 2 - (left.y + yShift).toInt() * zoom / 100,
                            scrW / 2 + (right.x + xShift).toInt() * zoom / 100,
                            scrH / 2 - (right.y + yShift).toInt() * zoom / 100
                            )
                }
                for (sceneY in -sceneHeight..sceneHeight) {
                    val up = getVirtualScreenPointFromScene(Point(sceneWidth.toFloat(), sceneY.toFloat()))
                    val down = getVirtualScreenPointFromScene(Point(-sceneWidth.toFloat(), sceneY.toFloat()))
                    gr.drawLine(
                            scrW / 2 + (up.x + xShift).toInt() * zoom / 100,
                            scrH / 2 - (up.y + yShift).toInt() * zoom / 100,
                            scrW / 2 + (down.x + xShift).toInt() * zoom / 100,
                            scrH / 2 - (down.y + yShift).toInt() * zoom / 100
                    )
                }
                val w = MainPanel.objectWidth / 2f
                val h = MainPanel.objectHeight / 2f
                val left = getVirtualScreenPointFromScene(Point(-w, -h))
                val right = getVirtualScreenPointFromScene(Point(w, h))
                val up = getVirtualScreenPointFromScene(Point(-w, h))
                val down = getVirtualScreenPointFromScene(Point(w, -h))
                gr.color = Color.RED
                gr.fillPolygon(
                        intArrayOf(
                                scrW / 2 + (left.x).toInt() * zoom / 100,
                                scrW / 2 + (up.x).toInt() * zoom / 100,
                                scrW / 2 + (right.x).toInt() * zoom / 100,
                                scrW / 2 + (down.x).toInt() * zoom / 100
                        ),
                        intArrayOf(
                                scrH / 2 - (left.y).toInt() * zoom / 100,
                                scrH / 2 - (up.y).toInt() * zoom / 100,
                                scrH / 2 - (right.y).toInt() * zoom / 100,
                                scrH / 2 - (down.y).toInt() * zoom / 100
                                ),
                        4
                )
            }
        }
        gr.color = Color.BLACK
        gr.drawLine(scrW / 2, 0, scrW / 2, scrH)
        gr.drawLine(0, scrH / 2 + centerY * zoom / 100, scrW, scrH / 2 + centerY * zoom / 100)
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

data class Point(val x: Float, val y: Float)

const val TILE_WIDTH = 128f
const val TILE_HEIGHT = 64f

fun getVirtualScreenPointFromScene(scenePoint: Point): Point {
    val x = TILE_WIDTH / 2 * scenePoint.x + TILE_WIDTH / 2 * scenePoint.y
    val y = -TILE_HEIGHT / 2 * scenePoint.x + TILE_HEIGHT / 2 * scenePoint.y
    return Point(x, y)
}

fun getScenePointFromVirtualScreen(virtualScreenPoint: Point): Point {
    val x = virtualScreenPoint.x / TILE_WIDTH - virtualScreenPoint.y / TILE_HEIGHT
    val y = virtualScreenPoint.x / TILE_WIDTH + virtualScreenPoint.y / TILE_HEIGHT
    return Point(x, y)
}
