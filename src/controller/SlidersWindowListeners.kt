package controller

import model.Model.animation
import java.awt.event.ActionListener
import javax.swing.JOptionPane
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

val flipCheckBoxListener = ActionListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            for (anyFrame in animation.frames) {
                val layer = anyFrame.layers[frame.curLayer]
                if (layer.flipX != SlidersWindow.flipCheckBox.isSelected) {
                    layer.flipX = SlidersWindow.flipCheckBox.isSelected
                }
            }
        }
    }
}

val visibleCheckBoxListener = ActionListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.isVisible = SlidersWindow.visibleCheckBox.isSelected
        }
    }
}

val sizeSliderListener = ChangeListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.scale = SlidersWindow.sizeSlider.value / 100f
        }
    }
    SlidersWindow.sizeButton.text = SlidersWindow.sizeSlider.value.toString()
}

val widthSliderListener = ChangeListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.scaleX = SlidersWindow.widthSlider.value / 100f
        }
    }
    SlidersWindow.xButton.text = SlidersWindow.widthSlider.value.toString()
}

val heightSliderListener = ChangeListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            layer.scaleY = SlidersWindow.heightSlider.value / 100f
        }
    }
    SlidersWindow.yButton.text = SlidersWindow.heightSlider.value.toString()
}

val sizeButtonListener = ActionListener {
    val input = JOptionPane.showInputDialog(SlidersWindow, "Set layer scale", SlidersWindow.sizeSlider.value)?.toIntOrNull()
    if (input == null || input !in SLIDER_MIN_VALUE..SLIDER_MAX_VALUE)
        JOptionPane.showMessageDialog(SlidersWindow, "Incorrect input")
    else {
        SlidersWindow.sizeSlider.value = input
        sizeSliderListener.stateChanged(ChangeEvent(Any()))
    }
}

val xButtonListener = ActionListener {
    val input = JOptionPane.showInputDialog(SlidersWindow, "Set layer X scale", SlidersWindow.widthSlider.value)?.toIntOrNull()
    if (input == null || input !in SLIDER_MIN_VALUE..SLIDER_MAX_VALUE)
        JOptionPane.showMessageDialog(SlidersWindow, "Incorrect input")
    else {
        SlidersWindow.widthSlider.value = input
        widthSliderListener.stateChanged(ChangeEvent(Any()))
    }
}

val yButtonListener = ActionListener {
    val input = JOptionPane.showInputDialog(SlidersWindow, "Set layer Y scale", SlidersWindow.heightSlider.value)?.toIntOrNull()
    if (input == null || input !in SLIDER_MIN_VALUE..SLIDER_MAX_VALUE)
        JOptionPane.showMessageDialog(SlidersWindow, "Incorrect input")
    else {
        SlidersWindow.heightSlider.value = input
        heightSliderListener.stateChanged(ChangeEvent(Any()))
    }
}