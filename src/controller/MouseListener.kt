package controller

import model.Model.animation
import view.MainPanel
import java.awt.Cursor
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

object MouseListener : MouseListener, MouseMotionListener{
    /**
     * Зажата ли мышь (двигается ли какой-то слой в данный момент)
     */
    private var isMouseMoving = false
    /**
     * Координаты мыши (обновляются, если она зажата)
     */
    private var x: Int = 0
    private var y: Int = 0

    override fun mouseMoved(e: MouseEvent?) {
        e ?: return
        if (animation.curFrame != -1) {
            val frame = animation.frames[animation.curFrame]
            if (frame.curLayer != -1) {
                val layer = frame.layers[frame.curLayer]
                val scrW = MainPanel.width
                val scrH = MainPanel.height
                val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
                val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
                val range = Math.sqrt(sqr((layer.x * MainPanel.zoom / 100 + scrW / 2 - e.x).toDouble()) + sqr(((layer.y + MainPanel.centerY) * MainPanel.zoom / 100 + scrH / 2 - e.y).toDouble())).toInt()
                if (range < Math.min(scaledWidth, scaledHeight) * MainPanel.zoom / 200) {
                    MainPanel.cursor = Cursor(Cursor.MOVE_CURSOR)
                } else {
                    MainPanel.cursor = Cursor(Cursor.N_RESIZE_CURSOR)
                }
            } else {
                MainPanel.cursor = Cursor(Cursor.DEFAULT_CURSOR)
            }
        }
    }

    override fun mouseDragged(e: MouseEvent?) {
        e ?: return
        if (animation.curFrame != -1) {
            val frame = animation.frames[animation.curFrame]
            if (frame.curLayer != -1) {
                val layer = frame.layers[frame.curLayer]
                val scrW = MainPanel.width
                val scrH = MainPanel.height
                if (isMouseMoving) {
                    layer.x = (e.x - scrW / 2f) * 100f / MainPanel.zoom - x
                    layer.y = (e.y - scrH / 2f) * 100f / MainPanel.zoom - MainPanel.centerY + y
                } else {
                    val sy = ((layer.y + MainPanel.centerY) * MainPanel.zoom / 100 + scrH / 2 - e.y).toDouble()
                    val sx = (e.x - layer.x * MainPanel.zoom / 100 - scrW / 2).toDouble()
                    if (Math.abs(sy) >= Math.abs(sx)) {
                        when {
                            (sx > 0) -> layer.angle = Math.atan(sy / sx).toFloat()
                            (sx < 0) -> layer.angle = (Math.PI + Math.atan(sy / sx)).toFloat()
                            (sy > 0) -> layer.angle = (Math.PI / 2).toFloat()
                            else -> layer.angle = (- Math.PI / 2).toFloat()
                        }
                    } else {
                        if (sy != 0.0) {
                            layer.angle = (- Math.atan(sx / sy) - Math.PI / 2).toFloat()
                            if (sy > 0) {
                                layer.angle += Math.PI.toFloat()
                            }
                        } else if (sx > 0) {
                            layer.angle = 0f
                        } else {
                            layer.angle = Math.PI.toFloat()
                        }
                    }
                }
            }
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        e ?: return
        if (animation.curFrame != -1) {
            val frame = animation.frames[animation.curFrame]
            if (frame.curLayer != -1) {
                val layer = frame.layers[frame.curLayer]
                val scrW = MainPanel.width
                val scrH = MainPanel.height
                val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
                val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
                val range = Math.sqrt(sqr((layer.x * MainPanel.zoom / 100 + scrW / 2 - e.x).toDouble()) + sqr(((layer.y + MainPanel.centerY) * MainPanel.zoom / 100 + scrH / 2 - e.y).toDouble())).toInt()
                if (range < Math.min(scaledWidth, scaledHeight) * MainPanel.zoom / 200) {
                    isMouseMoving = true
                    x = Math.round((e.x - (layer.x * MainPanel.zoom / 100f + scrW / 2f)) * 100f / MainPanel.zoom)
                    y = Math.round(((layer.y + MainPanel.centerY) * MainPanel.zoom / 100f + scrH / 2f - e.y) * 100f / MainPanel.zoom)
                } else {
                    isMouseMoving = false
                }
            }
        }
    }

    override fun mouseReleased(e: MouseEvent?) {
        isMouseMoving = false
    }

    override fun mouseEntered(e: MouseEvent?) {

    }

    override fun mouseClicked(e: MouseEvent?) {

    }

    override fun mouseExited(e: MouseEvent?) {

    }

}