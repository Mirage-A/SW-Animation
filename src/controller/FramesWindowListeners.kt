package controller

import model.Frame
import model.Layer
import model.Model.animation
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JOptionPane

val newFrameButtonListener = ActionListener {
    val newFrame = Frame()
    var layersKol = 0
    if (animation.frames.isNotEmpty()) {
        layersKol = animation.frames[0].layers.size
    }
    for (i in 0 until layersKol) {
        newFrame.layers.add(Layer(animation.frames[0].layers[i]))
    }
    val tmp = JButton("frame${animation.frames.size}")
    tmp.addActionListener {
        MainWindow.setCurFrame(Integer.parseInt(tmp.text.substring(5)))
        FramesWindow.deleteFrameButton.isEnabled = true
        FramesWindow.deleteEnabled = true
    }
    animation.frames.add(newFrame)
    FramesWindow.run {
        frameButtons.add(tmp)
        scrollPanel.add(tmp)
        scrollPanel.repaint()
        scrollPane.revalidate()
    }
}

val copyLastFrameButtonListener = ActionListener {
    if (animation.curFrame != -1) {
        val tmp = JButton("frame" + (animation.frames.size))
        tmp.addActionListener {
            MainWindow.setCurFrame(Integer.parseInt(tmp.text.substring(5)))
            FramesWindow.deleteFrameButton.isEnabled = true
            FramesWindow.deleteEnabled = true
        }
        FramesWindow.run {
            frameButtons.add(tmp)
            scrollPanel.add(tmp)
            scrollPanel.repaint()
            scrollPane.revalidate()
        }
        animation.frames.add(Frame(animation.frames[animation.curFrame]))
    }
}

val deleteFrameButtonListener = ActionListener {
    if (JOptionPane.showConfirmDialog(FramesWindow, "Delete the frame " + animation.curFrame + "?", "Delete frame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
        if (animation.curFrame != -1) {
            val lastCurFrame = animation.curFrame
            MainWindow.setCurFrame(-1)
            animation.frames.removeAt(lastCurFrame)
            FramesWindow.run {
                deleteFrameButton.isEnabled = false
                deleteEnabled = false
                scrollPanel.remove(FramesWindow.frameButtons[FramesWindow.frameButtons.size - 1])
                frameButtons.removeAt(FramesWindow.frameButtons.size - 1)
                scrollPanel.repaint()
                scrollPane.revalidate()
            }
            LayersWindow.run {
                layerButtons.clear()
                scrollPanel.removeAll()
                scrollPane.revalidate()
                newLayerButton.isEnabled = false
                newEnabled = false
                deleteLayerButton.isEnabled = false
                deleteEnabled = false
                renameEnabled = false
                upLayerButton.isEnabled = false
                upEnabled = false
                downLayerButton.isEnabled = false
                downEnabled = false
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "No frames selected")
        }
    }
}

val upFrameButtonListener = ActionListener {
    if (animation.curFrame > 0) {
        val tmp = animation.frames[animation.curFrame]
        animation.frames[animation.curFrame] = animation.frames[animation.curFrame - 1]
        animation.frames[animation.curFrame - 1] = tmp
        MainWindow.setCurFrame(animation.curFrame - 1)
    }
}

val downFrameButtonListener = ActionListener {
    if (animation.curFrame != -1 && animation.curFrame < animation.frames.size - 1) {
        val tmp = animation.frames[animation.curFrame]
        animation.frames[animation.curFrame] = animation.frames[animation.curFrame + 1]
        animation.frames[animation.curFrame + 1] = tmp
        MainWindow.setCurFrame(animation.curFrame + 1)
    }
}