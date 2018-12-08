package ui

import logic.*
import java.awt.Cursor
import java.awt.Image
import java.awt.Toolkit
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.*
import java.util.ArrayList

import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileFilter

class AnimationWindow : JFrame() {
    private var panel: Panel = Panel()
    private var layersFrame: LayersFrame = LayersFrame()
    private var framesFrame: FramesFrame = FramesFrame()
    private var slidersFrame: SlidersFrame = SlidersFrame()
    private var isPlayingAnimation = false
    private var isMoving = false
    private var x1: Int = 0
    private var y1: Int = 0
    private var animTimer: Timer
    private val animDelay = 1000
    private var mdList = ArrayList<JCheckBox>()
    private var wtList = ArrayList<JCheckBox>()

    private var animation : Animation = BodyAnimation()

    init {
        animTimer = Timer(animDelay, ActionListener {
            if (animation.frames.size != 0) {
                animation.curFrame++
                if (animation.curFrame >= animation.frames.size || animation.curFrame == -1) {
                    animation.curFrame = 0
                }
                loadFrame(animation.curFrame)
                framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                panel.repaint()
                framesFrame.btns[animation.curFrame].font = layersFrame.selectedFont
            }
        })
        title = "Animation editor"
        size = Toolkit.getDefaultToolkit().screenSize
        panel = Panel()
        contentPane.add(panel)
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        val player = JCheckBox("Show shape")
        player.isSelected = true
        player.setBounds(8, 10, 160, 24)
        player.addChangeListener { panel.drawPlayer = player.isSelected }
        player.isVisible = true
        panel.add(player)
        val zoomSlider = JSlider(100, 800, panel.zoom)
        zoomSlider.setBounds(player.x, player.y + player.height + 2, player.width, player.height)
        zoomSlider.addChangeListener {
            panel.zoom = zoomSlider.value
            try {
                panel.player = ImageIO.read(File("./icons/player.png"))
                panel.player = panel.player.getScaledInstance(panel.player.getWidth(null) * panel.zoom / 100, panel.player.getHeight(null) * panel.zoom / 100, Image.SCALE_SMOOTH)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        zoomSlider.isVisible = true
        panel.add(zoomSlider)

        val secanim = JCheckBox("Animation lasts 1 sec")
        secanim.setBounds(zoomSlider.x, zoomSlider.y + zoomSlider.height + 2, zoomSlider.width, zoomSlider.height)
        secanim.addActionListener {
            if (isPlayingAnimation) {
                if (secanim.isSelected) {
                    animTimer.delay = animDelay / animation.frames.size
                } else {
                    animTimer.delay = 40
                }
            }
        }
        secanim.isVisible = true
        panel.add(secanim)


        val anim = JButton("Start animation")
        anim.setBounds(secanim.x, secanim.y + secanim.height + 2, secanim.width, secanim.height)
        anim.addActionListener {
            if (!isPlayingAnimation) {
                panel.t.stop()
                anim.text = "Stop animation"
                if (secanim.isSelected) {
                    animTimer.delay = animDelay / animation.frames.size
                } else {
                    animTimer.delay = 40
                }
                animTimer.restart()
            } else {
                animTimer.stop()
                anim.text = "Start animation"
                if (animation.curFrame != -1) {
                    framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                }
                animation.curFrame = 0
                framesFrame.btns[0].font = layersFrame.selectedFont
                loadFrame(animation.curFrame)
                panel.t.restart()
            }
            isPlayingAnimation = !isPlayingAnimation
        }
        anim.isVisible = true
        panel.add(anim)

        val reverse = JButton("Mirror animation")
        reverse.setBounds(anim.x, anim.y + anim.height + 4, anim.width, anim.height)
        reverse.addActionListener {
            if (JOptionPane.showConfirmDialog(this@AnimationWindow, "Do you want to create a mirrored animation?", "Mirroring animation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                mirrorAnimation()
            }
        }
        reverse.isVisible = true
        panel.add(reverse)


        val moveDirectionText = JTextField("Move direction:")
        moveDirectionText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersFrame.selectedFont
            isEditable = false
            setBounds(reverse.x, reverse.y + reverse.height + 4, reverse.width, reverse.height)
            isVisible = true
        }
        panel.add(moveDirectionText)


        for (md in MoveDirection.values()) {
            val cb = JCheckBox(md.toString())
            cb.setBounds(anim.x, moveDirectionText.y + (anim.height + 4) * (mdList.size + 1) + 4, anim.width, anim.height)
            cb.addActionListener {
                if (cb.isSelected) {
                    for (checkbox in mdList) {
                        if (checkbox != cb) {
                            checkbox.isSelected = false
                        }
                    }
                    animation.curMoveDirection = MoveDirection.fromString(cb.text)
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    framesFrame.btns.clear()
                    framesFrame.scrollPanel.removeAll()
                    framesFrame.scrollPanel.repaint()
                    framesFrame.scrollPane.revalidate()
                    slidersFrame.isVisible = false
                    animation.curFrame = -1
                    layersFrame.newLayerButton.isEnabled = true
                    layersFrame.deleteLayerButton.isEnabled = false
                    layersFrame.renameLayerButton.isEnabled = false
                    layersFrame.upLayerButton.isEnabled = false
                    layersFrame.downLayerButton.isEnabled = false
                    layersFrame.scrollPanel.removeAll()
                    layersFrame.btns.clear()
                    layersFrame.scrollPanel.repaint()
                    layersFrame.scrollPane.revalidate()
                    panel.frame = null
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            if (animation.curFrame != -1) {
                                framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                            }
                            animation.curFrame = Integer.parseInt(tmp.text.substring(5))
                            tmp.font = layersFrame.selectedFont
                            loadFrame(animation.curFrame)
                            framesFrame.deleteFrameButton.isEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                    }
                    framesFrame.scrollPane.revalidate()
                    animation.curFrame = -1
                    panel.frame = null
                }
                else {
                    cb.isSelected = true
                }
            }
            mdList.add(cb)
            cb.isVisible = true
            panel.add(cb)
        }

        val weaponTypeText = JTextField("Weapon type:")
        weaponTypeText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersFrame.selectedFont
            isEditable = false
            setBounds(mdList[mdList.size-1].x, mdList[mdList.size-1].y + mdList[mdList.size-1].height + 4, mdList[mdList.size-1].width, mdList[mdList.size-1].height)
            isVisible = true
        }
        panel.add(weaponTypeText)


        for (wt in WeaponType.values()) {
            val cb = JCheckBox(wt.toString())
            cb.setBounds(anim.x, weaponTypeText.y + (anim.height + 4) * (wtList.size + 1) + 4, anim.width, anim.height)
            cb.addActionListener {
                if (cb.isSelected) {
                    for (checkbox in wtList) {
                        if (checkbox != cb) {
                            checkbox.isSelected = false
                        }
                    }
                    animation.curWeaponType = WeaponType.fromString(cb.text)
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    framesFrame.btns.clear()
                    framesFrame.scrollPanel.removeAll()
                    framesFrame.scrollPanel.repaint()
                    framesFrame.scrollPane.revalidate()
                    slidersFrame.isVisible = false
                    animation.curFrame = -1
                    layersFrame.newLayerButton.isEnabled = true
                    layersFrame.deleteLayerButton.isEnabled = false
                    layersFrame.renameLayerButton.isEnabled = false
                    layersFrame.upLayerButton.isEnabled = false
                    layersFrame.downLayerButton.isEnabled = false
                    layersFrame.scrollPanel.removeAll()
                    layersFrame.btns.clear()
                    layersFrame.scrollPanel.repaint()
                    layersFrame.scrollPane.revalidate()
                    panel.frame = null
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            if (animation.curFrame != -1) {
                                framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                            }
                            animation.curFrame = Integer.parseInt(tmp.text.substring(5))
                            tmp.font = layersFrame.selectedFont
                            loadFrame(animation.curFrame)
                            framesFrame.deleteFrameButton.isEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                    }
                    framesFrame.scrollPane.revalidate()
                    animation.curFrame = -1
                    panel.frame = null
                }
                else {
                    cb.isSelected = true
                }
            }
            wtList.add(cb)
            cb.isVisible = true
            panel.add(cb)
        }

        addWindowListener(object : WindowListener {
            override fun windowOpened(e: WindowEvent) {}
            override fun windowIconified(e: WindowEvent) {}
            override fun windowDeiconified(e: WindowEvent) {}
            override fun windowDeactivated(e: WindowEvent) {}
            override fun windowClosing(e: WindowEvent) {
                val ans = JOptionPane.showOptionDialog(contentPane, "What do you want to do?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Open another animation", "Continue work", "Save and exit"), 0)
                if (ans == JOptionPane.YES_OPTION) {
                    serialize()
                    loadAnimation()
                } else if (ans == JOptionPane.CANCEL_OPTION) {
                    serialize()
                    System.exit(0)
                }

            }

            override fun windowClosed(e: WindowEvent) {}
            override fun windowActivated(e: WindowEvent) {}
        })
        isVisible = true
        layersFrame = LayersFrame()
        framesFrame = FramesFrame()
        slidersFrame = SlidersFrame()
        val screen = Toolkit.getDefaultToolkit().screenSize
        layersFrame.setLocation(screen.width - layersFrame.width - 20, screen.height - layersFrame.height - 60)
        layersFrame.isVisible = true
        framesFrame.setLocation(screen.width - framesFrame.width - 20, screen.height - layersFrame.height - framesFrame.height - 80)
        framesFrame.isVisible = true
        slidersFrame.setLocation(layersFrame.x - 20 - slidersFrame.width, screen.height - 60 - slidersFrame.height)
        slidersFrame.isVisible = false

        layersFrame.newLayerButton.addActionListener {
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
                    val layer = Layer(image.name)
                    for (frame in animation.frames) {
                        frame.layers.add(Layer(layer))
                    }
                    val tmp = JButton(layer.layerName)
                    tmp.addActionListener { loadLayer(layersFrame.btns.indexOf(tmp)) }
                    layersFrame.btns.add(tmp)
                    layersFrame.scrollPanel.add(tmp)
                    layersFrame.scrollPanel.revalidate()
                    loadLayer(layersFrame.btns.size - 1)
                }
            }
        }
        layersFrame.deleteLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    if (JOptionPane.showConfirmDialog(layersFrame, "Delete the layer " +layer.layerName + "?", "Delete layer", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                        layersFrame.scrollPanel.remove(layersFrame.btns[frame.curLayer])
                        layersFrame.btns.removeAt(frame.curLayer)
                        val layerID = frame.curLayer
                        frame.curLayer = -1
                        layersFrame.deleteLayerButton.isEnabled = false
                        layersFrame.renameLayerButton.isEnabled = false
                        layersFrame.upLayerButton.isEnabled = false
                        layersFrame.downLayerButton.isEnabled = false
                        layersFrame.scrollPanel.repaint()
                        layersFrame.scrollPane.revalidate()
                        for (fr in animation.frames) {
                            fr.curLayer = -1
                            fr.layers.removeAt(layerID)
                        }
                    }
                }
            }
        }
        layersFrame.renameLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    val newName = JOptionPane.showInputDialog(layersFrame, "Create a new name for the layer " + layer.layerName, "Rename the layer", JOptionPane.PLAIN_MESSAGE).trim { it <= ' ' }
                    layer.layerName = newName
                    layersFrame.btns[frame.curLayer].text = newName
                }
            }
        }
        layersFrame.upLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    if (frame.curLayer > 0) {
                        val btns = layersFrame.btns
                        layersFrame.btns = ArrayList()

                        for (i in 0 until frame.curLayer - 1) {
                            layersFrame.btns.add(btns[i])
                        }
                        layersFrame.btns.add(btns[frame.curLayer])
                        layersFrame.btns.add(btns[frame.curLayer - 1])
                        for (i in frame.curLayer + 1 until btns.size) {
                            layersFrame.btns.add(btns[i])
                        }


                        layersFrame.scrollPanel.removeAll()
                        for (i in layersFrame.btns.indices) {
                            layersFrame.scrollPanel.add(layersFrame.btns[i])
                        }
                        layersFrame.scrollPanel.repaint()
                        layersFrame.scrollPane.revalidate()

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
        layersFrame.downLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    if (frame.curLayer < frame.layers.size - 1) {
                        val btns = layersFrame.btns
                        layersFrame.btns = ArrayList()

                        for (i in 0 until frame.curLayer) {
                            layersFrame.btns.add(btns[i])
                        }
                        layersFrame.btns.add(btns[frame.curLayer + 1])
                        layersFrame.btns.add(btns[frame.curLayer])
                        for (i in frame.curLayer + 2 until btns.size) {
                            layersFrame.btns.add(btns[i])
                        }

                        layersFrame.scrollPanel.removeAll()
                        for (i in layersFrame.btns.indices) {
                            layersFrame.scrollPanel.add(layersFrame.btns[i])
                        }
                        layersFrame.scrollPanel.repaint()
                        layersFrame.scrollPane.revalidate()

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
        framesFrame.newFrameButton.addActionListener {
            val newFrame = Frame()
            var layersKol = 0
            if (animation.frames.isNotEmpty()) {
                layersKol = animation.frames[0].layers.size
            }
            for (i in 0 until layersKol) {
                newFrame.layers.add(Layer(animation.frames[0].layers[i]))
            }
            val tmp = JButton("frame"+animation.frames.size)
            tmp.addActionListener {
                if (animation.curFrame != -1) {
                    framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                }
                animation.curFrame = Integer.parseInt(tmp.text.substring(5))
                tmp.font = layersFrame.selectedFont
                loadFrame(animation.curFrame)
                framesFrame.deleteFrameButton.isEnabled = true
            }
            animation.frames.add(newFrame)
            framesFrame.btns.add(tmp)
            framesFrame.scrollPanel.add(tmp)
            framesFrame.scrollPanel.repaint()
            framesFrame.scrollPane.revalidate()
        }
        framesFrame.copyLastFrameButton.addActionListener {
            if (animation.curFrame != -1) {
                val tmp = JButton("frame" + (animation.frames.size))
                tmp.addActionListener {
                    if (animation.curFrame != -1) {
                        framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                    }
                    animation.curFrame = Integer.parseInt(tmp.text.substring(5))
                    tmp.font = layersFrame.selectedFont
                    loadFrame(animation.curFrame)
                    framesFrame.deleteFrameButton.isEnabled = true
                }
                framesFrame.btns.add(tmp)
                framesFrame.scrollPanel.add(tmp)
                framesFrame.scrollPanel.repaint()
                framesFrame.scrollPane.revalidate()
                animation.frames.add(Frame(animation.frames[animation.curFrame]))
            }
        }
        framesFrame.deleteFrameButton.addActionListener {
            if (JOptionPane.showConfirmDialog(framesFrame, "Delete the frame " + animation.curFrame + "?", "Delete frame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                if (animation.curFrame != -1) {
                    framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                }
                animation.frames.removeAt(animation.curFrame)
                framesFrame.deleteFrameButton.isEnabled = false
                framesFrame.scrollPanel.remove(framesFrame.btns[framesFrame.btns.size - 1])
                framesFrame.btns.removeAt(framesFrame.btns.size - 1)
                framesFrame.scrollPanel.repaint()
                framesFrame.scrollPane.revalidate()
                animation.curFrame = -1
                layersFrame.btns.clear()
                layersFrame.scrollPanel.removeAll()
                layersFrame.scrollPane.revalidate()
                layersFrame.deleteLayerButton.isEnabled = false
                layersFrame.renameLayerButton.isEnabled = false
                layersFrame.upLayerButton.isEnabled = false
                layersFrame.downLayerButton.isEnabled = false
            }
        }
        framesFrame.loadFrameButton.addActionListener {
            val fc = JFileChooser("./animations")
            fc.addChoosableFileFilter(object : FileFilter() {

                override fun getDescription(): String {
                    return "Shattered World animations (.SWANIM)"
                }

                override fun accept(f: File): Boolean {
                    return f.name.endsWith(".swanim")
                }
            })
            fc.dialogTitle = "Choose a frame to create a copy of it"

            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Copying frames is unavailable in this version")
                //TODO
                /*val file = fc.selectedFile
                if (file.name.endsWith(".swanim")) {
                    val frame = File(framesFolder.absolutePath + "/" + "frame" + framesFolder.list()!!.size + ".swanim")
                    try {
                        saveFrame()
                        frame.createNewFile()
                        val out = FileWriter(frame)
                        out.write(String(Files.readAllBytes(file.toPath())))
                        out.close()
                        val tmp = JButton("frame" + (framesFolder.list()!!.size - 1))
                        tmp.addActionListener {
                            saveFrame()
                            if (curFrame != -1) {
                                framesFrame.btns[curFrame].font = layersFrame.basicFont
                            }
                            curFrame = Integer.parseInt(tmp.text.substring(5))
                            tmp.font = layersFrame.selectedFont
                            loadFrame(curFrame)
                            framesFrame.deleteFrameButton.isEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                        framesFrame.scrollPanel.repaint()
                        framesFrame.scrollPane.revalidate()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }*/
            }
        }
        slidersFrame.sizeSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scale = slidersFrame.sizeSlider.value / 100f
                }
            }
        }
        slidersFrame.widthSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scaleX = slidersFrame.widthSlider.value / 100f
                }
            }
        }
        slidersFrame.heightSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scaleY = slidersFrame.heightSlider.value / 100f
                }
            }
        }
        addMouseListener(object : MouseListener {
            override fun mouseReleased(e: MouseEvent) {
                isMoving = false
            }

            override fun mousePressed(e: MouseEvent) {
                if (animation.curFrame != -1) {
                    val frame = animation.frames[animation.curFrame]
                    if (frame.curLayer != -1) {
                        val layer = frame.layers[frame.curLayer]
                        val scrW = panel.width
                        val scrH = panel.height
                        val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
                        val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
                        val range = Math.sqrt(sqr((layer.x * panel.zoom / 100 + scrW / 2 - e.x).toDouble()) + sqr(((layer.y + panel.centerY) * panel.zoom / 100 + scrH / 2 - e.y).toDouble())).toInt()
                        if (range < Math.min(scaledWidth, scaledHeight) * panel.zoom / 200) {
                            isMoving = true
                            x1 = Math.round((e.x - (layer.x * panel.zoom / 100f + scrW / 2f)) * 100f / panel.zoom)
                            y1 = Math.round(((layer.y + panel.centerY) * panel.zoom / 100f + scrH / 2f - e.y) * 100f / panel.zoom)
                        } else {
                            isMoving = false
                        }
                    }
                }
            }

            override fun mouseExited(e: MouseEvent) {

            }

            override fun mouseEntered(e: MouseEvent) {

            }

            override fun mouseClicked(e: MouseEvent) {

            }
        })
        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseMoved(e: MouseEvent) {
                if (animation.curFrame != -1) {
                    val frame = animation.frames[animation.curFrame]
                    if (frame.curLayer != -1) {
                        val layer = frame.layers[frame.curLayer]
                        val scrW = panel.width
                        val scrH = panel.height
                        val scaledWidth = layer.basicWidth * layer.scale * layer.scaleX
                        val scaledHeight = layer.basicHeight * layer.scale * layer.scaleY
                        val range = Math.sqrt(sqr((layer.x * panel.zoom / 100 + scrW / 2 - e.x).toDouble()) + sqr(((layer.y + panel.centerY) * panel.zoom / 100 + scrH / 2 - e.y).toDouble())).toInt()
                        if (range < Math.min(scaledWidth, scaledHeight) * panel.zoom / 200) {
                            panel.cursor = Cursor(Cursor.MOVE_CURSOR)
                        } else {
                            panel.cursor = Cursor(Cursor.N_RESIZE_CURSOR)
                        }
                    } else {
                        panel.cursor = Cursor(Cursor.DEFAULT_CURSOR)
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                if (animation.curFrame != -1) {
                    val frame = animation.frames[animation.curFrame]
                    if (frame.curLayer != -1) {
                        val layer = frame.layers[frame.curLayer]
                        val scrW = panel.width
                        val scrH = panel.height
                        if (isMoving) {
                            layer.x = (e.x - scrW / 2f) * 100f / panel.zoom - x1
                            layer.y = (e.y - scrH / 2f) * 100f / panel.zoom - panel.centerY + y1
                        } else {
                            val sy = ((layer.y + panel.centerY) * panel.zoom / 100 + scrH / 2 - e.y).toDouble()
                            val sx = (e.x - layer.x * panel.zoom / 100 - scrW / 2).toDouble()
                            if (Math.abs(sy) >= Math.abs(sx)) {
                                when (true) {
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
        }
        )
        val ans = JOptionPane.showOptionDialog(contentPane, "Welcome to Shattered World animation editor!", "Welcome!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Create new animation", "Load animation", "Exit editor"), 0)
        when (ans) {
            JOptionPane.YES_OPTION -> createNewAnimation()
            JOptionPane.NO_OPTION -> loadAnimation()
            JOptionPane.CANCEL_OPTION -> System.exit(0)
        }
    }

    /**
     * Сериализует текущую анимацию и сохраняет ее в файл
     */
    private fun serialize() {
        val path = when (animation) {
            is BodyAnimation -> "bodyanimations/" + animation.name + ".swanim"
            is LegsAnimation -> "legsanimations/" + animation.name + ".swanim"
            else -> throw Exception("Undefined type of animation")
        }

        val file = File(path)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdir()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        val fos = FileOutputStream(path)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(animation)
        oos.flush()
        oos.close()

    }

    /**
     * Создает новую пустую анимацию через диалоговые окна и сохраняет ее
     */
    private fun createNewAnimation() {
        //выбор типа анимации (body or legs)
        var newAnimation : Animation = BodyAnimation()
        val typeChoice = JOptionPane.showOptionDialog(contentPane, "Choose animation's type", "New animation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Body animation", "Legs animation"), 0)
        when (typeChoice) {
            JOptionPane.YES_OPTION -> newAnimation = BodyAnimation()
            JOptionPane.NO_OPTION -> newAnimation = LegsAnimation()
        }

        newAnimation.name = JOptionPane.showInputDialog(this, "Enter the new animation's name (for example, Fireball)", "New animation", JOptionPane.PLAIN_MESSAGE).trim { it <= ' ' }

        //инициализация animation.data
        for (moveDirection in MoveDirection.values()) {
            newAnimation.data[moveDirection] = HashMap()
            if (newAnimation is LegsAnimation) {
                // Если LegsAnimation, то от оружия анимация не зависит
                val arr = ArrayList<Frame>()
                for (weaponType in WeaponType.values()) {
                    newAnimation.data[moveDirection]!![weaponType] = arr
                }
            }
            else if (newAnimation is BodyAnimation){
                // BodyAnimation разная для разного оружия
                for (weaponType in WeaponType.values()) {
                    newAnimation.data[moveDirection]!![weaponType] = ArrayList()
                }
            }
        }
        animation = newAnimation
        serialize()
        JOptionPane.showMessageDialog(this, "Animation created!", "New animation", JOptionPane.PLAIN_MESSAGE)
        val ans = JOptionPane.showOptionDialog(contentPane, "Welcome to Shattered World animation editor!", "Welcome!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Create new animation", "Load animation", "Exit editor"), 0)
        when (ans) {
            JOptionPane.YES_OPTION -> createNewAnimation()
            JOptionPane.NO_OPTION -> loadAnimation()
            JOptionPane.CANCEL_OPTION -> System.exit(0)
        }
    }

    /**
     * Открывает окно выбора анимации, десереализует и загружает выбранную анимацию
     */
    private fun loadAnimation() {
        val fc = JFileChooser("./")
        fc.addChoosableFileFilter(object : FileFilter() {

            override fun getDescription(): String {
                return "Shattered World animations (.SWANIM)"
            }

            override fun accept(f: File): Boolean {
                return f.name.endsWith(".swanim")
            }
        })
        fc.dialogTitle = "Open animation"

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                val file = fc.selectedFile
                if (file.name.endsWith(".swanim")) {
                    serialize()
                    val fis = FileInputStream(file)
                    val oin = ObjectInputStream(fis)
                    val obj = oin.readObject()
                    animation = obj as Animation
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    framesFrame.btns.clear()
                    framesFrame.scrollPanel.removeAll()
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            if (animation.curFrame != -1) {
                                framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
                            }
                            animation.curFrame = Integer.parseInt(tmp.text.substring(5))
                            tmp.font = layersFrame.selectedFont
                            loadFrame(animation.curFrame)
                            framesFrame.deleteFrameButton.isEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                    }
                    framesFrame.scrollPane.revalidate()
                    for (cb in mdList) {
                        cb.isSelected = (cb.text.equals(animation.curMoveDirection.toString()))
                    }
                    for (cb in wtList) {
                        cb.isSelected = (cb.text.equals(animation.curWeaponType.toString()))
                    }
                    if (animation.curFrame != -1) {
                        loadFrame(animation.curFrame)
                        framesFrame.btns[animation.curFrame].font = layersFrame.selectedFont
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                JOptionPane.showMessageDialog(this, "Invalid file", "Error", JOptionPane.ERROR_MESSAGE)
                loadAnimation()
            }

        }
    }

    /**
     * Переключает активный слой
     */
    private fun loadLayer(layerID: Int) {
        if (animation.frames[animation.curFrame].curLayer != -1) {
            layersFrame.btns[animation.frames[animation.curFrame].curLayer].font = layersFrame.basicFont
        }
        layersFrame.btns[layerID].font = layersFrame.selectedFont
        animation.frames[animation.curFrame].curLayer = layerID
        layersFrame.deleteLayerButton.isEnabled = true
        layersFrame.renameLayerButton.isEnabled = true
        layersFrame.upLayerButton.isEnabled = true
        layersFrame.downLayerButton.isEnabled = true

        val layer = animation.frames[animation.curFrame].layers[layerID]
        slidersFrame.sizeSlider.value = Math.round(layer.scale * 100)
        slidersFrame.widthSlider.value = Math.round(layer.scaleX * 100)
        slidersFrame.heightSlider.value = Math.round(layer.scaleY * 100)
        slidersFrame.isVisible = true
    }

    /**
     * Переключает активный кадр
     */
    private fun loadFrame(frameID: Int) {
        slidersFrame.isVisible = false
        animation.curFrame = frameID
        val frame = animation.frames[frameID]
        frame.curLayer = -1
        layersFrame.newLayerButton.isEnabled = true
        layersFrame.deleteLayerButton.isEnabled = false
        layersFrame.renameLayerButton.isEnabled = false
        layersFrame.upLayerButton.isEnabled = false
        layersFrame.downLayerButton.isEnabled = false
        layersFrame.scrollPanel.removeAll()
        layersFrame.btns.clear()
        for (layer in frame.layers) {
            val tmp = JButton(layer.layerName)
            tmp.addActionListener { loadLayer(layersFrame.btns.indexOf(tmp)) }
            layersFrame.btns.add(tmp)
            layersFrame.scrollPanel.add(tmp)
        }
        layersFrame.scrollPanel.repaint()
        layersFrame.scrollPane.revalidate()
        panel.frame = frame
    }

    /**
     * Создает отраженную копию всех кадров для соотвествующего moveDirection-а
     */
    private fun mirrorAnimation() {
        val mirroredMD = animation.curMoveDirection.mirrored()
        if (mirroredMD == animation.curMoveDirection) {
            JOptionPane.showMessageDialog(null, "This move direction can't be mirrored")
        }
        else {
            var mirroredFrames = animation.data[mirroredMD]!![animation.curWeaponType]!!
            mirroredFrames.clear()
            var curFrames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
            for (frame in curFrames) {
                mirroredFrames.add(Frame(frame))
            }
            for (mFrame in mirroredFrames) {
                for (mLayer in mFrame.layers) {
                    mLayer.x *= -1
                    mLayer.angle *= -1
                }
            }

        }
    }

    private fun sqr(a: Double): Double {
        return a * a
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AnimationWindow()
        }
    }
}
