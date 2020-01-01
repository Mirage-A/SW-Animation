package controller

import model.Layer
import model.Model.animation
import java.awt.event.ActionListener
import java.io.File
import java.util.ArrayList
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileFilter

val newLayerButtonListener = ActionListener {
    val fc = JFileChooser("./drawable")
    fc.addChoosableFileFilter(object : FileFilter() {
        override fun getDescription(): String {
            return "Images (.PNG)"
        }

        override fun accept(f: File): Boolean {
            return f.name.endsWith(".png")
        }
    })
    fc.dialogTitle = "Choose an image for the new layer"

    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val image = fc.selectedFile
        if (image.name.endsWith(".png")) {
            val absImagePath = image.absolutePath
            val startAbsPath = File("drawable").absolutePath
            val relativePath = absImagePath.substring(startAbsPath.length + 1, absImagePath.length)
            val layer = Layer(relativePath.substring(0, relativePath.length - 4))
            for (frame in animation.frames) {
                frame.layers.add(Layer(layer))
            }
            val tmp = JButton(layer.imageName)
            tmp.addActionListener {
                MainWindow.loadLayer(LayersWindow.layerButtons.indexOf(tmp))
            }
            LayersWindow.run {
                layerButtons.add(tmp)
                scrollPanel.add(tmp)
                scrollPanel.revalidate()
            }
            MainWindow.loadLayer(LayersWindow.layerButtons.size - 1)
        }
    }
}

val deleteLayerButtonListener = ActionListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            val layer = frame.layers[frame.curLayer]
            if (JOptionPane.showConfirmDialog(LayersWindow, "Delete the layer " +layer.imageName + "?", "Delete layer", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                LayersWindow.run {
                    scrollPanel.remove(layerButtons[frame.curLayer])
                    layerButtons.removeAt(frame.curLayer)
                    deleteLayerButton.isEnabled = false
                    deleteEnabled = false
                    renameEnabled = false
                    upLayerButton.isEnabled = false
                    upEnabled = false
                    downLayerButton.isEnabled = false
                    downEnabled = false
                    scrollPanel.repaint()
                    scrollPane.revalidate()
                }
                val layerID = frame.curLayer
                frame.curLayer = -1
                for (fr in animation.frames) {
                    fr.curLayer = -1
                    fr.layers.removeAt(layerID)
                }
            }
        }
    }
}

val upLayerButtonListener = ActionListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            if (frame.curLayer > 0) {
                LayersWindow.run {
                    val buttons = layerButtons
                    layerButtons = ArrayList()

                    for (i in 0 until frame.curLayer - 1) {
                        layerButtons.add(buttons[i])
                    }
                    layerButtons.add(buttons[frame.curLayer])
                    layerButtons.add(buttons[frame.curLayer - 1])
                    for (i in frame.curLayer + 1 until buttons.size) {
                        layerButtons.add(buttons[i])
                    }


                    scrollPanel.removeAll()
                    for (i in layerButtons.indices) {
                        scrollPanel.add(layerButtons[i])
                    }
                    scrollPanel.repaint()
                    scrollPane.revalidate()
                }
                val layerID = frame.curLayer
                for (fr in animation.frames) {
                    fr.curLayer--
                    val tmp = fr.layers[layerID]
                    fr.layers[layerID] = fr.layers[layerID - 1]
                    fr.layers[layerID - 1] = tmp
                }
            }
        }
    }
}

val downLayerButtonListener = ActionListener {
    if (animation.curFrame != -1) {
        val frame = animation.frames[animation.curFrame]
        if (frame.curLayer != -1) {
            if (frame.curLayer < frame.layers.size - 1) {
                LayersWindow.run {
                    val buttons = layerButtons
                    layerButtons = ArrayList()

                    for (i in 0 until frame.curLayer) {
                        layerButtons.add(buttons[i])
                    }
                    layerButtons.add(buttons[frame.curLayer + 1])
                    layerButtons.add(buttons[frame.curLayer])
                    for (i in frame.curLayer + 2 until buttons.size) {
                        layerButtons.add(buttons[i])
                    }

                    scrollPanel.removeAll()
                    for (i in layerButtons.indices) {
                        scrollPanel.add(layerButtons[i])
                    }
                    scrollPanel.repaint()
                    scrollPane.revalidate()
                }
                val layerID = frame.curLayer
                for (fr in animation.frames) {
                    fr.curLayer++
                    val tmp = fr.layers[layerID]
                    fr.layers[layerID] = fr.layers[layerID + 1]
                    fr.layers[layerID + 1] = tmp
                }
            }
        }
    }
}