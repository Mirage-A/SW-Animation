package ui

import logic.*
import java.awt.Color
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
    private var isMoving = false
    private var x1: Int = 0
    private var y1: Int = 0
    private var animTimer: Timer = Timer(1, {})
    private var mdList = ArrayList<JCheckBox>()
    private var wtList = ArrayList<JCheckBox>()
    private var repeat: JCheckBox = JCheckBox()
    private var changeDuration : JButton = JButton()
    private var anim : JButton = JButton()
    private var exitBtn : JButton = JButton()
    private var openAnotherAnimationBtn : JButton = JButton()
    private var newAnimationBtn : JButton = JButton()
    private var saveBtn : JButton = JButton()
    private var animationNameText : JTextField = JTextField()

    private var animation : Animation = BodyAnimation()

    init {
        extendedState = JFrame.MAXIMIZED_BOTH
        isUndecorated = true
        title = "Animation editor"
        size = Toolkit.getDefaultToolkit().screenSize
        panel = Panel()
        contentPane.add(panel)
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        // Кнопка выхода из редактора
        exitBtn = JButton("Save and exit")
        exitBtn.setBounds(width - 160 - 10, 10, 160, 24)
        exitBtn.addActionListener {
            checkExit()
        }
        exitBtn.isVisible = true
        panel.add(exitBtn)

        //Кнопка изменения скорости перерисовки
        val fpsBtn = JButton("FPS: 60")
        fpsBtn.setBounds(exitBtn.x, exitBtn.y + exitBtn.height + 4, exitBtn.width, exitBtn.height)
        fpsBtn.addActionListener {
            val input = JOptionPane.showInputDialog(null, "Input maximum FPS for editor\nHigher values may lower performance\nThis setting only affects animation player in this editor\nDefault value : 60 FPS", 60)
            try {
                val newFPS = Integer.parseInt(input)
                if (newFPS > 0) {
                    fpsBtn.text = "FPS: $newFPS"
                    panel.t.delay = 1000 / newFPS
                }
                else {
                    JOptionPane.showMessageDialog(null, "FPS can't be less or equal 0")
                }
            }
            catch(ex : Exception) {
                JOptionPane.showMessageDialog(null, "Incorrect input")
            }
        }
        fpsBtn.isVisible = true
        panel.add(fpsBtn)

        //Кнопка генерации кода
        val generateCodeBtn = JButton("Generate code")
        generateCodeBtn.setBounds(fpsBtn.x, fpsBtn.y + fpsBtn.height + 4, fpsBtn.width, fpsBtn.height)
        generateCodeBtn.addActionListener {
            if (JOptionPane.showConfirmDialog(null, "Do you want to generate source code file for Shattered World game?\nIf such file already exists, it will be overwritten.", "Code generation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                CodeGenerator.generate()
            }
        }
        generateCodeBtn.isVisible = true
        panel.add(generateCodeBtn)

        // Кнопка загрузки другой анимации
        openAnotherAnimationBtn = JButton("Open animation")
        openAnotherAnimationBtn.run {
            setBounds(generateCodeBtn.x, generateCodeBtn.y + generateCodeBtn.height + 4, generateCodeBtn.width, generateCodeBtn.height)
            addActionListener {
                loadAnimation()
            }
            isVisible = true
        }
        panel.add(openAnotherAnimationBtn)

        // Кнопка создания новой анимации
        newAnimationBtn = JButton("New animation")
        newAnimationBtn.run {
            setBounds(exitBtn.x, openAnotherAnimationBtn.y + openAnotherAnimationBtn.height + 4, openAnotherAnimationBtn.width, openAnotherAnimationBtn.height)
            addActionListener {
                createNewAnimation()
            }
            isVisible = true
        }
        panel.add(newAnimationBtn)

        // Кнопка сохранения
        saveBtn = JButton("Save animation")
        saveBtn.run {
            setBounds(newAnimationBtn.x, newAnimationBtn.y + newAnimationBtn.height + 4, newAnimationBtn.width, newAnimationBtn.height)
            addActionListener {
                serialize()
            }
            isVisible = true
        }
        panel.add(saveBtn)

        // Текст с названием и типом анимации
        animationNameText = JTextField("No animation loaded")
        animationNameText.run {
            setBounds(saveBtn.x, saveBtn.y + saveBtn.height + 4, saveBtn.width, saveBtn.height)
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersFrame.selectedFont
            isEditable = false
            isVisible = true
        }
        panel.add(animationNameText)



        // Чекбокс, который переключает отображение изображения игрока на фоне
        val player = JCheckBox("Show shape")
        player.isSelected = true
        player.setBounds(8, 10, 160, 24)
        player.addChangeListener { panel.drawPlayer = player.isSelected }
        player.isVisible = true
        panel.add(player)

        //Слайдер, позволяющий приближать и отдалять картинку
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

        //Кнопка старта/остановки анимации
        anim = JButton("Start animation")
        anim.setBounds(zoomSlider.x, zoomSlider.y + zoomSlider.height + 2, zoomSlider.width, zoomSlider.height)
        anim.addActionListener {
            if (!panel.isPlayingAnimation) {
                startAnimation()
            } else {
                stopAnimation()
            }
        }
        anim.foreground = Color(0, 208, 0)
        anim.isVisible = true
        panel.add(anim)

        //Кнопка создания отраженной анимации
        val reverse = JButton("Mirror animation")
        reverse.setBounds(anim.x, anim.y + anim.height + 4, anim.width, anim.height)
        reverse.addActionListener {
            if (JOptionPane.showConfirmDialog(this@AnimationWindow, "Do you want to create a mirrored animation?", "Mirroring animation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                mirrorAnimation()
            }
        }
        reverse.isVisible = true
        panel.add(reverse)

        //Кнопка изменения продолжительности анимации
        changeDuration = JButton("Animation duration")
        changeDuration.setBounds(reverse.x, reverse.y + reverse.height + 4, reverse.width, reverse.height)
        changeDuration.addActionListener {
            val input = JOptionPane.showInputDialog(null, "Input new animation duration here to change it\nFor repeatable animations, duration means period\n(Input 1000 to set duration to 1 sec)\nCurrent value : " + animation.duration + " ms", animation.duration)
            try {
                val newDuration = Integer.parseInt(input)
                if (newDuration > 0) {
                    animation.duration = newDuration
                }
                else {
                    JOptionPane.showMessageDialog(null, "Duration can't be less or equal 0")
                }
            }
            catch(ex : Exception) {
                JOptionPane.showMessageDialog(null, "Incorrect input")
            }
        }
        changeDuration.isVisible = true
        panel.add(changeDuration)

        //Чекбокс, позволяющий выбрать, нужно ли воспроизводить анимацию по кругу или
        //остановить воспроизведение на последнем кадре
        repeat = JCheckBox("Repeatable")
        repeat.isSelected = false
        repeat.setBounds(changeDuration.x, changeDuration.y + changeDuration.height + 4, changeDuration.width, changeDuration.height)
        repeat.addChangeListener {
            animation.isRepeatable = repeat.isSelected
        }
        repeat.isVisible = true
        panel.add(repeat)

        //Текст "Move direction:"
        val moveDirectionText = JTextField("Move direction:")
        moveDirectionText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersFrame.selectedFont
            isEditable = false
            setBounds(repeat.x, repeat.y + repeat.height + 4, repeat.width, repeat.height)
            isVisible = true
        }
        panel.add(moveDirectionText)


        //Чекбоксы выбора moveDirection-а
        for (md in MoveDirection.values()) {
            val cb = JCheckBox(md.toString())
            cb.setBounds(anim.x, moveDirectionText.y + (anim.height + 4) * (mdList.size + 1) + 4, anim.width, anim.height)
            cb.addActionListener {
                if (cb.isSelected) {
                    stopAnimation()
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
                    setCurFrame(-1)
                    layersFrame.newLayerButton.isEnabled = true
                    layersFrame.newEnabled = true
                    layersFrame.deleteLayerButton.isEnabled = false
                    layersFrame.deleteEnabled = false
                    layersFrame.renameLayerButton.isEnabled = false
                    layersFrame.renameEnabled = false
                    layersFrame.upLayerButton.isEnabled = false
                    layersFrame.upEnabled = false
                    layersFrame.downLayerButton.isEnabled = false
                    layersFrame.downEnabled = false
                    layersFrame.scrollPanel.removeAll()
                    layersFrame.btns.clear()
                    layersFrame.scrollPanel.repaint()
                    layersFrame.scrollPane.revalidate()
                    panel.frame = null
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                            framesFrame.deleteFrameButton.isEnabled = true
                            framesFrame.deleteEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                    }
                    framesFrame.scrollPane.revalidate()
                    setCurFrame(-1)
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

        //Текст "Weapon type:"
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

        //Чекбоксы выбора weaponType-а
        for (wt in WeaponType.values()) {
            val cb = JCheckBox(wt.toString())
            cb.setBounds(anim.x, weaponTypeText.y + (anim.height + 4) * (wtList.size + 1) + 4, anim.width, anim.height)
            cb.addActionListener {
                if (cb.isSelected) {
                    stopAnimation()
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
                    setCurFrame(-1)
                    layersFrame.newLayerButton.isEnabled = true
                    layersFrame.newEnabled = true
                    layersFrame.deleteLayerButton.isEnabled = false
                    layersFrame.deleteEnabled = false
                    layersFrame.renameLayerButton.isEnabled = false
                    layersFrame.renameEnabled = false
                    layersFrame.upLayerButton.isEnabled = false
                    layersFrame.upEnabled = false
                    layersFrame.downLayerButton.isEnabled = false
                    layersFrame.downEnabled = false
                    layersFrame.scrollPanel.removeAll()
                    layersFrame.btns.clear()
                    layersFrame.scrollPanel.repaint()
                    layersFrame.scrollPane.revalidate()
                    panel.frame = null
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                            framesFrame.deleteFrameButton.isEnabled = true
                            framesFrame.deleteEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                    }
                    framesFrame.scrollPane.revalidate()
                    setCurFrame(-1)
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

        //Добавляем подтверждение выхода при нажатии на кнопку закрытия окна
        addWindowListener(object : WindowListener {
            override fun windowOpened(e: WindowEvent) {}
            override fun windowIconified(e: WindowEvent) {}
            override fun windowDeiconified(e: WindowEvent) {}
            override fun windowDeactivated(e: WindowEvent) {}
            override fun windowClosing(e: WindowEvent) {
                checkExit()
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

        //Кнопочки мини-окна со слоями
        //Кнопочка создания нового слоя
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
        //Кнопочка удаления слоя
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
                        layersFrame.deleteEnabled = false
                        layersFrame.renameLayerButton.isEnabled = false
                        layersFrame.renameEnabled = false
                        layersFrame.upLayerButton.isEnabled = false
                        layersFrame.upEnabled = false
                        layersFrame.downLayerButton.isEnabled = false
                        layersFrame.downEnabled = false
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
        //Кнопочка переименования слоя
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
        //Кнопочка поднятия слоя наверх
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

        //Кнопочка опускания слоя вниз
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

        //Кнопочки мини-окна с кадрами
        //Кнопочка создания нового кадра
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
                setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                framesFrame.deleteFrameButton.isEnabled = true
                framesFrame.deleteEnabled = true
            }
            animation.frames.add(newFrame)
            framesFrame.btns.add(tmp)
            framesFrame.scrollPanel.add(tmp)
            framesFrame.scrollPanel.repaint()
            framesFrame.scrollPane.revalidate()
        }

        //Кнопочка копирования текущего кадра
        framesFrame.copyLastFrameButton.addActionListener {
            if (animation.curFrame != -1) {
                val tmp = JButton("frame" + (animation.frames.size))
                tmp.addActionListener {
                    setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                    framesFrame.deleteFrameButton.isEnabled = true
                    framesFrame.deleteEnabled = true
                }
                framesFrame.btns.add(tmp)
                framesFrame.scrollPanel.add(tmp)
                framesFrame.scrollPanel.repaint()
                framesFrame.scrollPane.revalidate()
                animation.frames.add(Frame(animation.frames[animation.curFrame]))
            }
        }

        //Кнопочка удаления кадра
        framesFrame.deleteFrameButton.addActionListener {
            if (JOptionPane.showConfirmDialog(framesFrame, "Delete the frame " + animation.curFrame + "?", "Delete frame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                if (animation.curFrame != -1) {
                    val lastCurFrame = animation.curFrame
                    setCurFrame(-1)
                    animation.frames.removeAt(lastCurFrame)
                    framesFrame.deleteFrameButton.isEnabled = false
                    framesFrame.deleteEnabled = false
                    framesFrame.scrollPanel.remove(framesFrame.btns[framesFrame.btns.size - 1])
                    framesFrame.btns.removeAt(framesFrame.btns.size - 1)
                    framesFrame.scrollPanel.repaint()
                    framesFrame.scrollPane.revalidate()
                    layersFrame.btns.clear()
                    layersFrame.scrollPanel.removeAll()
                    layersFrame.scrollPane.revalidate()
                    layersFrame.newLayerButton.isEnabled = false
                    layersFrame.newEnabled = false
                    layersFrame.deleteLayerButton.isEnabled = false
                    layersFrame.deleteEnabled = false
                    layersFrame.renameLayerButton.isEnabled = false
                    layersFrame.renameEnabled = false
                    layersFrame.upLayerButton.isEnabled = false
                    layersFrame.upEnabled = false
                    layersFrame.downLayerButton.isEnabled = false
                    layersFrame.downEnabled = false
                }
                else {
                    JOptionPane.showMessageDialog(null, "No frames selected")
                }
            }
        }

        //Кнопочка загрузки кадра
        /*framesFrame.loadFrameButton.addActionListener {
            JOptionPane.showMessageDialog(null, "Copying frames is unavailable in this version")
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
                //TODO копирование кадров
            }
        }*/

        //Слайдеры мини-окна со слайдерами изменения размера
        //Слайдер изменения размера слоя
        slidersFrame.sizeSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scale = slidersFrame.sizeSlider.value / 100f
                }
            }
        }

        //Слайдер изменения ширины слоя
        slidersFrame.widthSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scaleX = slidersFrame.widthSlider.value / 100f
                }
            }
        }

        //Слайдер изменения высоты слоя
        slidersFrame.heightSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scaleY = slidersFrame.heightSlider.value / 100f
                }
            }
        }

        //Добавляем возможность перемещать и поворачивать слои мышкой
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
        showStartMessage()
        panel.t.start()
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
        var newAnimation : Animation? = null
        val typeChoice = JOptionPane.showOptionDialog(contentPane, "Choose animation's type", "New animation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Body animation", "Legs animation"), 0)
        when (typeChoice) {
            JOptionPane.YES_OPTION -> newAnimation = BodyAnimation()
            JOptionPane.NO_OPTION -> newAnimation = LegsAnimation()
        }
        if (newAnimation != null) {
            val inputName: String? = JOptionPane.showInputDialog(this, "Enter the new animation's name (for example, FIREBALL)", "New animation", JOptionPane.PLAIN_MESSAGE)

            if (inputName.isNullOrBlank()) {
                JOptionPane.showMessageDialog(null, "Incorrect input")
            } else {
                newAnimation.name = inputName.trim { it <= ' ' }
                var path = "./"
                if (newAnimation is BodyAnimation) {
                    path += "bodyanimations/"
                }
                if (newAnimation is LegsAnimation) {
                    path += "legsanimations/"
                }
                path += newAnimation.name + ".swanim"
                if (File(path).exists()) {
                    JOptionPane.showMessageDialog(null, "Animation with this name already exists")
                }
                else {
                    for (moveDirection in MoveDirection.values()) {
                        newAnimation.data[moveDirection] = HashMap()
                        if (newAnimation is LegsAnimation) {
                            val arr = ArrayList<Frame>()
                            for (weaponType in WeaponType.values()) {
                                newAnimation.data[moveDirection]!![weaponType] = arr
                            }
                        } else if (newAnimation is BodyAnimation) {
                            for (weaponType in WeaponType.values()) {
                                newAnimation.data[moveDirection]!![weaponType] = ArrayList()
                            }
                        }
                    }
                    animation = newAnimation
                    serialize()
                    JOptionPane.showMessageDialog(this, "Animation created!", "New animation", JOptionPane.PLAIN_MESSAGE)
                }
            }
        }
    }

    /**
     * Открывает окно выбора анимации, десереализует и загружает выбранную анимацию
     * Возвращает true, если анимация успешно загружена, и false иначе
     */
    private fun loadAnimation() : Boolean {
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
                            setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                            framesFrame.deleteFrameButton.isEnabled = true
                            framesFrame.deleteEnabled = true
                        }
                        framesFrame.btns.add(tmp)
                        framesFrame.scrollPanel.add(tmp)
                    }
                    framesFrame.scrollPane.revalidate()
                    framesFrame.scrollPanel.repaint()
                    for (cb in mdList) {
                        cb.isSelected = (cb.text.equals(animation.curMoveDirection.toString()))
                    }
                    for (cb in wtList) {
                        cb.isSelected = (cb.text.equals(animation.curWeaponType.toString()))
                    }
                    setCurFrame(animation.curFrame)
                    repeat.isSelected = animation.isRepeatable
                    if (animation is LegsAnimation) {
                        animationNameText.text = "Legs: " + animation.name
                    }
                    else if (animation is BodyAnimation) {
                        animationNameText.text = "Body: " + animation.name
                    }
                    else {
                        animationNameText.text = "Unidentified type"
                    }
                    return true
                }
                else {
                    JOptionPane.showMessageDialog(this, "Invalid file", "Error", JOptionPane.ERROR_MESSAGE)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                JOptionPane.showMessageDialog(this, "Invalid file", "Error", JOptionPane.ERROR_MESSAGE)
            }
        }
        return false
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
        layersFrame.deleteEnabled = true
        layersFrame.renameLayerButton.isEnabled = true
        layersFrame.renameEnabled = true
        layersFrame.upLayerButton.isEnabled = true
        layersFrame.upEnabled = true
        layersFrame.downLayerButton.isEnabled = true
        layersFrame.downEnabled = true

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
        layersFrame.deleteLayerButton.isEnabled = false
        layersFrame.deleteEnabled = false
        layersFrame.renameLayerButton.isEnabled = false
        layersFrame.renameEnabled = false
        layersFrame.upLayerButton.isEnabled = false
        layersFrame.upEnabled = false
        layersFrame.downLayerButton.isEnabled = false
        layersFrame.downEnabled = false
        layersFrame.scrollPanel.removeAll()
        layersFrame.btns.clear()
        if (frameID != -1) {
            layersFrame.newLayerButton.isEnabled = true
            layersFrame.newEnabled = true
            val frame = animation.frames[frameID]
            frame.curLayer = -1
            for (layer in frame.layers) {
                val tmp = JButton(layer.layerName)
                tmp.addActionListener { loadLayer(layersFrame.btns.indexOf(tmp)) }
                layersFrame.btns.add(tmp)
                layersFrame.scrollPanel.add(tmp)
            }
            panel.frame = frame
        }
        else {
            panel.frame = null
            layersFrame.newLayerButton.isEnabled = false
            layersFrame.newEnabled = false
        }
        layersFrame.scrollPanel.repaint()
        layersFrame.scrollPane.revalidate()
        panel.repaint()
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

    /**
     * Переключает кадр, обновляет кнопочки и перерисовывает экран
     */
    private fun setCurFrame(frameID : Int) {
        if (animation.curFrame != -1) {
            framesFrame.btns[animation.curFrame].font = layersFrame.basicFont
        }
        animation.curFrame = frameID
        if (animation.curFrame != -1) {
            framesFrame.btns[animation.curFrame].font = layersFrame.selectedFont
        }
        loadFrame(frameID)
    }

    /**
     * Начинает воспроизведение анимации
     */
    private fun startAnimation() {
        anim.text = "Stop animation"
        repeat.isEnabled = false
        changeDuration.isEnabled = false
        openAnotherAnimationBtn.isEnabled = false
        framesFrame.isEnabled = false
        layersFrame.isEnabled = false
        slidersFrame.isEnabled = false
        for (cb in mdList) {
            cb.isEnabled = false
        }
        for (cb in wtList) {
            cb.isEnabled = false
        }
        anim.foreground = Color.RED
        panel.frames = animation.frames
        panel.isRepeatable = animation.isRepeatable
        panel.duration = animation.duration + 0L
        panel.startTime = System.currentTimeMillis()
        panel.isPlayingAnimation = true
    }

    /**
     * Останавливает воспроизведение анимации
     */
    private fun stopAnimation() {
        anim.text = "Start animation"
        anim.foreground = Color(0, 208, 0)
        repeat.isEnabled = true
        changeDuration.isEnabled = true
        openAnotherAnimationBtn.isEnabled = true
        framesFrame.isEnabled = true
        layersFrame.isEnabled = true
        slidersFrame.isEnabled = true
        for (cb in mdList) {
            cb.isEnabled = true
        }
        for (cb in wtList) {
            cb.isEnabled = true
        }
        panel.isPlayingAnimation = false
    }

    /**
     * Показывает стартовое окно с возможностью создать новую анимацию, загрузить существующую или выйти из редактора
     */
    private fun showStartMessage() {
        val ans = JOptionPane.showOptionDialog(contentPane, "Welcome to Shattered World Animation Editor!", "Welcome!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Create new animation", "Load animation", "Exit editor"), 0)
        when (ans) {
            JOptionPane.YES_OPTION -> {
                createNewAnimation()
                showStartMessage()
            }
            JOptionPane.NO_OPTION -> {
                if (!loadAnimation()) {
                    showStartMessage()
                }
            }
            JOptionPane.CANCEL_OPTION -> System.exit(0)
            else -> showStartMessage()
        }
    }

    private fun sqr(a: Double): Double {
        return a * a
    }

    private fun checkExit() {
        val ans = JOptionPane.showOptionDialog(contentPane, "Do you want to exit editor?\nAll you work will be saved.", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Save and exit", "Cancel"), 0)
        if (ans == JOptionPane.YES_OPTION) {
            serialize()
            System.exit(0)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AnimationWindow()
        }
    }
}
