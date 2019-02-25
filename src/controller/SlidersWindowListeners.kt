package controller

import model.Model.animation
import javax.swing.event.ChangeListener

val sizeSliderListener = ChangeListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.scale = SlidersWindow.sizeSlider.value / 100f
        }
    }
}

val widthSliderListener = ChangeListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.scaleX = SlidersWindow.widthSlider.value / 100f
        }
    }
}

val heightSliderListener = ChangeListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.scaleY = SlidersWindow.heightSlider.value / 100f
        }
    }
}