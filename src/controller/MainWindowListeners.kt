package controller

import model.Model
import model.MoveDirection
import model.WeaponType
import view.MainPanel
import java.awt.Image
import java.awt.event.ActionListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.event.ChangeListener

val exitButtonListener = ActionListener {
    MainWindow.checkExit()
}

val fpsButtonListener = ActionListener {
    val input = JOptionPane.showInputDialog(null, "Input maximum FPS for editor\nHigher values may lower performance\nThis setting only affects animation showPlayerImageCheckbox in this editor\nDefault value : 60 FPS", 60)
    try {
        val newFPS = Integer.parseInt(input)
        if (newFPS > 0) {
            MainWindow.fpsBtn.text = "FPS: $newFPS"
            MainPanel.t.delay = 1000 / newFPS
        }
        else {
            JOptionPane.showMessageDialog(null, "FPS can't be less or equal 0")
        }
    }
    catch(ex : Exception) {
        JOptionPane.showMessageDialog(null, "Incorrect input")
    }
}

val openAnotherAnimationButtonListener = ActionListener {
    MainWindow.loadAnimation()
}

val createNewAnimationButtonListener = ActionListener {
    MainWindow.createNewAnimation()
}

val saveAnimationButtonListener = ActionListener {
    Model.serialize()
}

fun Float.toShortString(): String = ((this * 10f).toInt().toFloat() / 10f).toString()

val objectSizeButtonListener = ActionListener {
    var input = JOptionPane.showInputDialog(
            null,
            "Input probable object position and size on game scene to modify tile grid",
            "x=${MainPanel.objectX.toShortString()};y=${MainPanel.objectY.toShortString()};width=${MainPanel.objectWidth.toShortString()};height=${MainPanel.objectHeight.toShortString()};")
    try {
        input = input
                .replace("x=", "")
                .replace("y=", "")
                .replace("width=", "")
                .replace("height=", "")
        val values = input.split(";")
        val x = values[0].toFloat()
        val y = values[1].toFloat()
        val width = values[2].toFloat()
        val height = values[3].toFloat()
        MainPanel.objectX = x
        MainPanel.objectY = y
        MainPanel.objectWidth = width
        MainPanel.objectHeight = height
    }
    catch(ex : Exception) {
        JOptionPane.showMessageDialog(null, "Incorrect input")
    }
}

val zoomSliderListener = ChangeListener {
    MainPanel.zoom = MainWindow.zoomSlider.value
    try {
        MainPanel.player = ImageIO.read(File("./drawable/player.png"))
        MainPanel.player = MainPanel.player.getScaledInstance(MainPanel.player.getWidth(null) * MainPanel.zoom / 100, MainPanel.player.getHeight(null) * MainPanel.zoom / 100, Image.SCALE_SMOOTH)
    } catch (ex: Exception) {
        JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
        System.exit(0)
    }
}

val toggleAnimationButtonListener = ActionListener {
    if (!MainPanel.isPlayingAnimation) {
        MainWindow.startAnimation()
    } else {
        MainWindow.stopAnimation()
    }
}

val createMirroredAnimationButtonListener = ActionListener {
    if (JOptionPane.showConfirmDialog(null, "Do you want to create a mirrored animation?", "Mirroring animation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
        Model.mirrorAnimation()
    }
}

val changeDurationButtonListener = ActionListener {
    val input = JOptionPane.showInputDialog(null, "Input new animation duration here to change it\nFor repeatable animations, duration means period\n(Input 1000 to set duration to 1 sec)\nCurrent value : " + Model.animation.duration + " ms", Model.animation.duration)
    try {
        val newDuration = Integer.parseInt(input)
        if (newDuration > 0) {
            Model.animation.duration = newDuration
        }
        else {
            JOptionPane.showMessageDialog(null, "Duration can't be less or equal 0")
        }
    }
    catch(ex : Exception) {
        JOptionPane.showMessageDialog(null, "Incorrect input")
    }
}

val isAnimationRepeatableCheckboxListener = ActionListener {
    Model.animation.isRepeatable = MainWindow.isAnimationRepeatableCheckbox.isSelected
}

fun getMoveDirectionCheckboxListener(md: MoveDirection) = ActionListener {
    if (MainWindow.moveDirectionCheckboxes[md]?.isSelected == true) {
        MainWindow.stopAnimation()
        for ((moveDirection, checkbox) in MainWindow.moveDirectionCheckboxes) {
            if (md != moveDirection) {
                checkbox.isSelected = false
            }
        }
        Model.animation.curMoveDirection = md
        Model.animation.frames = Model.animation.data[Model.animation.curMoveDirection]!![Model.animation.curWeaponType]!!
        Model.animation.curFrame = -1
        MainPanel.frame = null
        FramesWindow.run {
            frameButtons.clear()
            scrollPanel.removeAll()
            scrollPanel.repaint()
            scrollPane.revalidate()
            for (i in Model.animation.frames.indices) {
                val tmp = JButton("frame$i")
                tmp.addActionListener {
                    MainWindow.setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                    deleteFrameButton.isEnabled = true
                    deleteEnabled = true
                }
                frameButtons.add(tmp)
                scrollPanel.add(tmp)
            }
            scrollPane.revalidate()
        }
        SlidersWindow.isVisible = false
        LayersWindow.run {
            newLayerButton.isEnabled = true
            newEnabled = true
            deleteLayerButton.isEnabled = false
            deleteEnabled = false
            renameEnabled = false
            upLayerButton.isEnabled = false
            upEnabled = false
            downLayerButton.isEnabled = false
            downEnabled = false
            scrollPanel.removeAll()
            layerButtons.clear()
            scrollPanel.repaint()
            scrollPane.revalidate()
        }
        MainWindow.setCurFrame(-1)
        MainPanel.frame = null
    }
    else {
        MainWindow.moveDirectionCheckboxes[md]?.isSelected = true
    }
}

fun getWeaponTypeCheckboxListener(wt: WeaponType) = ActionListener {
    if (MainWindow.weaponTypeCheckboxes[wt]?.isSelected == true) {
        MainWindow.stopAnimation()
        for (checkbox in MainWindow.weaponTypeCheckboxes.filter { it.key != wt }.values) {
            checkbox.isSelected = false
        }
        Model.animation.curWeaponType = wt
        Model.animation.frames = Model.animation.data[Model.animation.curMoveDirection]!![Model.animation.curWeaponType]!!
        Model.animation.curFrame = -1
        MainPanel.frame = null
        FramesWindow.run {
            frameButtons.clear()
            scrollPanel.removeAll()
            scrollPanel.repaint()
            scrollPane.revalidate()
            for (i in Model.animation.frames.indices) {
                val tmp = JButton("frame$i")
                tmp.addActionListener {
                    MainWindow.setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                    deleteFrameButton.isEnabled = true
                    deleteEnabled = true
                }
                frameButtons.add(tmp)
                scrollPanel.add(tmp)
            }
            scrollPane.revalidate()
        }
        SlidersWindow.isVisible = false
        LayersWindow.run {
            newLayerButton.isEnabled = true
            newEnabled = true
            deleteLayerButton.isEnabled = false
            deleteEnabled = false
            renameEnabled = false
            upLayerButton.isEnabled = false
            upEnabled = false
            downLayerButton.isEnabled = false
            downEnabled = false
            scrollPanel.removeAll()
            layerButtons.clear()
            scrollPanel.repaint()
            scrollPane.revalidate()
        }
        MainWindow.setCurFrame(-1)
        MainPanel.frame = null
    }
    else {
        MainWindow.weaponTypeCheckboxes[wt]?.isSelected = true
    }
}