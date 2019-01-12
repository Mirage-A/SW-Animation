package controller

import model.*
import view.Panel
import java.awt.Color
import java.awt.Cursor
import java.awt.Image
import java.awt.Toolkit
import java.awt.event.*
import java.io.*
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileFilter
import kotlin.collections.HashMap
import kotlin.collections.indices
import kotlin.collections.isNotEmpty
import kotlin.collections.set

/**
 * Основной класс контроллера
 * Представляет собой основное окно редактора с кнопочками и их логикой
 */
class MainWindow : JFrame() {
    /**
     * View, на котором перерисовывается анимация
     */
    private val panel: Panel
    /**
     * Дополнительные окна - кадры, слои, размеры слоя соответственно
     */
    private val framesWindow: FramesWindow
    private val layersWindow: LayersWindow
    private val slidersWindow: SlidersWindow
    /**
     * Параметры мыши
     */
    /**
     * Зажата ли мышь (двигается ли какой-то слой в данный момент)
     */
    private var isMouseMoving = false
    /**
     * Координаты мыши (обновляются, если она зажата)
     */
    private var mouseX: Int = 0
    private var mouseY: Int = 0
    /**
     * Элементы интерфейса в левом верхнем углу
     */
    /**
     * Чекбоксы с выбором направления движения
     */
    private val moveDirectionCheckboxes = ArrayList<JCheckBox>()
    /**
     * Слайдер, позволяющий приближать/отдалять картинку в редакторе
     */
    private val zoomSlider :JSlider
    /**
     * Чекбоксы с выбором типа оружия
     */
    private val weaponTypeCheckboxes = ArrayList<JCheckBox>()
    /**
     * Чекбокс, определяющий, повторяется ли анимация после завершения
     */
    private val isAnimationRepeatableCheckbox: JCheckBox
    /**
     * Чекбокс, определяющий, показывать ли эскиз гуманоида на фоне для помощи в подборе размера изображений
     */
    private val showPlayerImageCheckbox : JCheckBox
    /**
     * Кнопочка, позволяющая выбрать длительность (период) анимации
     */
    private val changeDurationBtn : JButton
    /**
     * Кнопочка, позволяющая запустить или остановить анимацию
     */
    private val toggleAnimationBtn : JButton
    /**
     * Кнопочка, позволяющая создать отраженную относительно вертикальной оси анимацию
     * для соответствующего направления движения
     */
    private val createMirroredAnimationBtn: JButton
    /**
     * Текст "Move direction: " над чекбоксами с выбором направления движения
     */
    private val moveDirectionText: JTextField
    /**
     * Текст "Weapon type: " над чекбоксами с выбором типа оружия
     */
    private val weaponTypeText : JTextField
    /**
     * Элементы интерфейса в правом верхнем углу
     */
    /**
     * Кнопочка, отвечающая за выход из редактора
     */
    private val exitBtn : JButton
    /**
     * Кнопочка, позволяющая выбрать и загрузить другую анимацию
     */
    private val openAnotherAnimationBtn : JButton
    /**
     * Кнопочка, позволяющая создать новую анимацию
     */
    private val newAnimationBtn : JButton
    /**
     * Кнопочка, позволяющая сохранить (сериализовать) текущую анимацию
     */
    private val saveAnimationBtn : JButton
    /**
     * Кнопочка, позволяюшая выбрать FPS перерисовки анимации в редакторе
     */
    private val fpsBtn : JButton
    /**
     * Кнопочка, запускающая CodeGenerator, генерирующий файл с кодом,
     * который потом нужно вставить в проект Shattered World - Client
     */
    private val generateCodeBtn : JButton
    /**
     * Текст, отображающий на экране тип и название текущей анимации
     */
    private val animationNameText : JTextField
    /**
     * Сама анимация (модель)
     */
    private var animation : Animation = Animation()

    /**
     * Файл с текущей анимацией
     */
    private var animationFile : File? = null

    /**
     * Текущая директория с анимацией
     */
    private var animationDirectory : File = File("./animations")

    private var settingsFile: File = File("./settings.txt")

    /**
     * Инициализация всего интерфейса
     */
    init {
        if (settingsFile.exists()) {
            animationDirectory = File(Scanner(settingsFile).nextLine())
        }
        extendedState = JFrame.MAXIMIZED_BOTH
        isUndecorated = true
        title = "Animation editor"
        size = Toolkit.getDefaultToolkit().screenSize
        panel = Panel()
        contentPane.add(panel)
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        layersWindow = LayersWindow()
        framesWindow = FramesWindow()
        slidersWindow = SlidersWindow()

        // Кнопка выхода из редактора
        exitBtn = JButton("Save and exit")
        exitBtn.setBounds(width - 160 - 10, 10, 160, 24)
        exitBtn.addActionListener {
            checkExit()
        }
        exitBtn.isVisible = true
        panel.add(exitBtn)

        //Кнопка изменения скорости перерисовки
        fpsBtn = JButton("FPS: 60")
        fpsBtn.setBounds(exitBtn.x, exitBtn.y + exitBtn.height + 4, exitBtn.width, exitBtn.height)
        fpsBtn.addActionListener {
            val input = JOptionPane.showInputDialog(null, "Input maximum FPS for editor\nHigher values may lower performance\nThis setting only affects animation showPlayerImageCheckbox in this editor\nDefault value : 60 FPS", 60)
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
        generateCodeBtn = JButton("Generate code")
        generateCodeBtn.setBounds(fpsBtn.x, fpsBtn.y + fpsBtn.height + 4, fpsBtn.width, fpsBtn.height)
        generateCodeBtn.addActionListener {
            if (JOptionPane.showConfirmDialog(null, "Do you want to generate source code file for Shattered World game?\nIf such file already exists, it will be overwritten.", "Code generation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                try {
                    CodeGenerator.generate()
                }
                catch(ex: Exception) {
                    JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
                    System.exit(0)
                }
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
        saveAnimationBtn = JButton("Save animation")
        saveAnimationBtn.run {
            setBounds(newAnimationBtn.x, newAnimationBtn.y + newAnimationBtn.height + 4, newAnimationBtn.width, newAnimationBtn.height)
            addActionListener {
                serialize()
            }
            isVisible = true
        }
        panel.add(saveAnimationBtn)

        // Текст с названием и типом анимации
        animationNameText = JTextField("No animation loaded")
        animationNameText.run {
            setBounds(saveAnimationBtn.x, saveAnimationBtn.y + saveAnimationBtn.height + 4, saveAnimationBtn.width, saveAnimationBtn.height)
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersWindow.selectedFont
            isEditable = false
            isVisible = true
        }
        panel.add(animationNameText)



        // Чекбокс, который переключает отображение изображения игрока на фоне
        showPlayerImageCheckbox = JCheckBox("Show shape")
        showPlayerImageCheckbox.isSelected = true
        showPlayerImageCheckbox.setBounds(8, 10, 160, 24)
        showPlayerImageCheckbox.addChangeListener { panel.drawPlayer = showPlayerImageCheckbox.isSelected }
        showPlayerImageCheckbox.isVisible = true
        panel.add(showPlayerImageCheckbox)

        //Слайдер, позволяющий приближать и отдалять картинку
        zoomSlider = JSlider(100, 800, panel.zoom)
        zoomSlider.setBounds(showPlayerImageCheckbox.x, showPlayerImageCheckbox.y + showPlayerImageCheckbox.height + 2, showPlayerImageCheckbox.width, showPlayerImageCheckbox.height)
        zoomSlider.addChangeListener {
            panel.zoom = zoomSlider.value
            try {
                panel.player = ImageIO.read(File("./drawable/player.png"))
                panel.player = panel.player.getScaledInstance(panel.player.getWidth(null) * panel.zoom / 100, panel.player.getHeight(null) * panel.zoom / 100, Image.SCALE_SMOOTH)
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
                System.exit(0)
            }
        }
        zoomSlider.isVisible = true
        panel.add(zoomSlider)

        //Кнопка старта/остановки анимации
        toggleAnimationBtn = JButton("Start animation")
        toggleAnimationBtn.setBounds(zoomSlider.x, zoomSlider.y + zoomSlider.height + 2, zoomSlider.width, zoomSlider.height)
        toggleAnimationBtn.addActionListener {
            if (!panel.isPlayingAnimation) {
                startAnimation()
            } else {
                stopAnimation()
            }
        }
        toggleAnimationBtn.foreground = Color(0, 208, 0)
        toggleAnimationBtn.isVisible = true
        panel.add(toggleAnimationBtn)

        //Кнопка создания отраженной анимации
        createMirroredAnimationBtn = JButton("Mirror animation")
        createMirroredAnimationBtn.setBounds(toggleAnimationBtn.x, toggleAnimationBtn.y + toggleAnimationBtn.height + 4, toggleAnimationBtn.width, toggleAnimationBtn.height)
        createMirroredAnimationBtn.addActionListener {
            if (JOptionPane.showConfirmDialog(null, "Do you want to create a mirrored animation?", "Mirroring animation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                mirrorAnimation()
            }
        }
        createMirroredAnimationBtn.isVisible = true
        panel.add(createMirroredAnimationBtn)

        //Кнопка изменения продолжительности анимации
        changeDurationBtn = JButton("Animation duration")
        changeDurationBtn.setBounds(createMirroredAnimationBtn.x, createMirroredAnimationBtn.y + createMirroredAnimationBtn.height + 4, createMirroredAnimationBtn.width, createMirroredAnimationBtn.height)
        changeDurationBtn.addActionListener {
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
        changeDurationBtn.isVisible = true
        panel.add(changeDurationBtn)

        //Чекбокс, позволяющий выбрать, нужно ли воспроизводить анимацию по кругу или
        //остановить воспроизведение на последнем кадре
        isAnimationRepeatableCheckbox = JCheckBox("Repeatable")
        isAnimationRepeatableCheckbox.isSelected = false
        isAnimationRepeatableCheckbox.setBounds(changeDurationBtn.x, changeDurationBtn.y + changeDurationBtn.height + 4, changeDurationBtn.width, changeDurationBtn.height)
        isAnimationRepeatableCheckbox.addChangeListener {
            animation.isRepeatable = isAnimationRepeatableCheckbox.isSelected
        }
        isAnimationRepeatableCheckbox.isVisible = true
        panel.add(isAnimationRepeatableCheckbox)

        //Текст "Move direction:"
        moveDirectionText = JTextField("Move direction:")
        moveDirectionText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersWindow.selectedFont
            isEditable = false
            setBounds(isAnimationRepeatableCheckbox.x, isAnimationRepeatableCheckbox.y + isAnimationRepeatableCheckbox.height + 4, isAnimationRepeatableCheckbox.width, isAnimationRepeatableCheckbox.height)
            isVisible = true
        }
        panel.add(moveDirectionText)


        //Чекбоксы выбора moveDirection-а
        for (md in MoveDirection.values()) {
            val cb = JCheckBox(md.toString())
            cb.setBounds(toggleAnimationBtn.x, moveDirectionText.y + (toggleAnimationBtn.height + 4) * (moveDirectionCheckboxes.size + 1) + 4, toggleAnimationBtn.width, toggleAnimationBtn.height)
            cb.addActionListener {
                if (cb.isSelected) {
                    stopAnimation()
                    for (checkbox in moveDirectionCheckboxes) {
                        if (checkbox != cb) {
                            checkbox.isSelected = false
                        }
                    }
                    animation.curMoveDirection = MoveDirection.fromString(cb.text)
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    animation.curFrame = -1
                    framesWindow.frameButtons.clear()
                    framesWindow.scrollPanel.removeAll()
                    framesWindow.scrollPanel.repaint()
                    framesWindow.scrollPane.revalidate()
                    slidersWindow.isVisible = false
                    layersWindow.newLayerButton.isEnabled = true
                    layersWindow.newEnabled = true
                    layersWindow.deleteLayerButton.isEnabled = false
                    layersWindow.deleteEnabled = false
                    layersWindow.renameEnabled = false
                    layersWindow.upLayerButton.isEnabled = false
                    layersWindow.upEnabled = false
                    layersWindow.downLayerButton.isEnabled = false
                    layersWindow.downEnabled = false
                    layersWindow.scrollPanel.removeAll()
                    layersWindow.layerButtons.clear()
                    layersWindow.scrollPanel.repaint()
                    layersWindow.scrollPane.revalidate()
                    panel.frame = null
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                            framesWindow.deleteFrameButton.isEnabled = true
                            framesWindow.deleteEnabled = true
                        }
                        framesWindow.frameButtons.add(tmp)
                        framesWindow.scrollPanel.add(tmp)
                    }
                    framesWindow.scrollPane.revalidate()
                    setCurFrame(-1)
                    panel.frame = null
                }
                else {
                    cb.isSelected = true
                }
            }
            moveDirectionCheckboxes.add(cb)
            cb.isVisible = true
            panel.add(cb)
        }

        //Текст "Weapon type:"
        weaponTypeText = JTextField("Weapon type:")
        weaponTypeText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = layersWindow.selectedFont
            isEditable = false
            setBounds(moveDirectionCheckboxes[moveDirectionCheckboxes.size-1].x, moveDirectionCheckboxes[moveDirectionCheckboxes.size-1].y + moveDirectionCheckboxes[moveDirectionCheckboxes.size-1].height + 4, moveDirectionCheckboxes[moveDirectionCheckboxes.size-1].width, moveDirectionCheckboxes[moveDirectionCheckboxes.size-1].height)
            isVisible = true
        }
        panel.add(weaponTypeText)

        //Чекбоксы выбора weaponType-а
        for (wt in WeaponType.values()) {
            val cb = JCheckBox(wt.toString())
            cb.setBounds(toggleAnimationBtn.x, weaponTypeText.y + (toggleAnimationBtn.height + 4) * (weaponTypeCheckboxes.size + 1) + 4, toggleAnimationBtn.width, toggleAnimationBtn.height)
            cb.addActionListener {
                if (cb.isSelected) {
                    stopAnimation()
                    for (checkbox in weaponTypeCheckboxes) {
                        if (checkbox != cb) {
                            checkbox.isSelected = false
                        }
                    }
                    animation.curWeaponType = WeaponType.fromString(cb.text)
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    animation.curFrame = -1
                    framesWindow.frameButtons.clear()
                    framesWindow.scrollPanel.removeAll()
                    framesWindow.scrollPanel.repaint()
                    framesWindow.scrollPane.revalidate()
                    slidersWindow.isVisible = false
                    layersWindow.newLayerButton.isEnabled = true
                    layersWindow.newEnabled = true
                    layersWindow.deleteLayerButton.isEnabled = false
                    layersWindow.deleteEnabled = false
                    layersWindow.renameEnabled = false
                    layersWindow.upLayerButton.isEnabled = false
                    layersWindow.upEnabled = false
                    layersWindow.downLayerButton.isEnabled = false
                    layersWindow.downEnabled = false
                    layersWindow.scrollPanel.removeAll()
                    layersWindow.layerButtons.clear()
                    layersWindow.scrollPanel.repaint()
                    layersWindow.scrollPane.revalidate()
                    panel.frame = null
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                            framesWindow.deleteFrameButton.isEnabled = true
                            framesWindow.deleteEnabled = true
                        }
                        framesWindow.frameButtons.add(tmp)
                        framesWindow.scrollPanel.add(tmp)
                    }
                    framesWindow.scrollPane.revalidate()
                    setCurFrame(-1)
                    panel.frame = null
                }
                else {
                    cb.isSelected = true
                }
            }
            weaponTypeCheckboxes.add(cb)
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
        val screen = Toolkit.getDefaultToolkit().screenSize
        layersWindow.setLocation(screen.width - layersWindow.width - 20, screen.height - layersWindow.height - 60)
        layersWindow.isVisible = true
        framesWindow.setLocation(screen.width - framesWindow.width - 20, screen.height - layersWindow.height - framesWindow.height - 80)
        framesWindow.isVisible = true
        slidersWindow.setLocation(layersWindow.x - 20 - slidersWindow.width, screen.height - 60 - slidersWindow.height)
        slidersWindow.isVisible = false

        //Кнопочки мини-окна со слоями
        //Кнопочка создания нового слоя
        layersWindow.newLayerButton.addActionListener {
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
                    val tmp = JButton(layer.getName())
                    tmp.addActionListener { loadLayer(layersWindow.layerButtons.indexOf(tmp)) }
                    layersWindow.layerButtons.add(tmp)
                    layersWindow.scrollPanel.add(tmp)
                    layersWindow.scrollPanel.revalidate()
                    loadLayer(layersWindow.layerButtons.size - 1)
                }
            }
        }
        //Кнопочка удаления слоя
        layersWindow.deleteLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    if (JOptionPane.showConfirmDialog(layersWindow, "Delete the layer " +layer.getName() + "?", "Delete layer", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                        layersWindow.scrollPanel.remove(layersWindow.layerButtons[frame.curLayer])
                        layersWindow.layerButtons.removeAt(frame.curLayer)
                        val layerID = frame.curLayer
                        frame.curLayer = -1
                        layersWindow.deleteLayerButton.isEnabled = false
                        layersWindow.deleteEnabled = false
                        layersWindow.renameEnabled = false
                        layersWindow.upLayerButton.isEnabled = false
                        layersWindow.upEnabled = false
                        layersWindow.downLayerButton.isEnabled = false
                        layersWindow.downEnabled = false
                        layersWindow.scrollPanel.repaint()
                        layersWindow.scrollPane.revalidate()
                        for (fr in animation.frames) {
                            fr.curLayer = -1
                            fr.layers.removeAt(layerID)
                        }
                    }
                }
            }
        }
        //Кнопочка поднятия слоя наверх
        layersWindow.upLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    if (frame.curLayer > 0) {
                        val btns = layersWindow.layerButtons
                        layersWindow.layerButtons = ArrayList()

                        for (i in 0 until frame.curLayer - 1) {
                            layersWindow.layerButtons.add(btns[i])
                        }
                        layersWindow.layerButtons.add(btns[frame.curLayer])
                        layersWindow.layerButtons.add(btns[frame.curLayer - 1])
                        for (i in frame.curLayer + 1 until btns.size) {
                            layersWindow.layerButtons.add(btns[i])
                        }


                        layersWindow.scrollPanel.removeAll()
                        for (i in layersWindow.layerButtons.indices) {
                            layersWindow.scrollPanel.add(layersWindow.layerButtons[i])
                        }
                        layersWindow.scrollPanel.repaint()
                        layersWindow.scrollPane.revalidate()

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
        layersWindow.downLayerButton.addActionListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    if (frame.curLayer < frame.layers.size - 1) {
                        val btns = layersWindow.layerButtons
                        layersWindow.layerButtons = ArrayList()

                        for (i in 0 until frame.curLayer) {
                            layersWindow.layerButtons.add(btns[i])
                        }
                        layersWindow.layerButtons.add(btns[frame.curLayer + 1])
                        layersWindow.layerButtons.add(btns[frame.curLayer])
                        for (i in frame.curLayer + 2 until btns.size) {
                            layersWindow.layerButtons.add(btns[i])
                        }

                        layersWindow.scrollPanel.removeAll()
                        for (i in layersWindow.layerButtons.indices) {
                            layersWindow.scrollPanel.add(layersWindow.layerButtons[i])
                        }
                        layersWindow.scrollPanel.repaint()
                        layersWindow.scrollPane.revalidate()

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
        framesWindow.newFrameButton.addActionListener {
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
                framesWindow.deleteFrameButton.isEnabled = true
                framesWindow.deleteEnabled = true
            }
            animation.frames.add(newFrame)
            framesWindow.frameButtons.add(tmp)
            framesWindow.scrollPanel.add(tmp)
            framesWindow.scrollPanel.repaint()
            framesWindow.scrollPane.revalidate()
        }

        //Кнопочка копирования текущего кадра
        framesWindow.copyLastFrameButton.addActionListener {
            if (animation.curFrame != -1) {
                val tmp = JButton("frame" + (animation.frames.size))
                tmp.addActionListener {
                    setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                    framesWindow.deleteFrameButton.isEnabled = true
                    framesWindow.deleteEnabled = true
                }
                framesWindow.frameButtons.add(tmp)
                framesWindow.scrollPanel.add(tmp)
                framesWindow.scrollPanel.repaint()
                framesWindow.scrollPane.revalidate()
                animation.frames.add(Frame(animation.frames[animation.curFrame]))
            }
        }

        //Кнопочка удаления кадра
        framesWindow.deleteFrameButton.addActionListener {
            if (JOptionPane.showConfirmDialog(framesWindow, "Delete the frame " + animation.curFrame + "?", "Delete frame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                if (animation.curFrame != -1) {
                    val lastCurFrame = animation.curFrame
                    setCurFrame(-1)
                    animation.frames.removeAt(lastCurFrame)
                    framesWindow.deleteFrameButton.isEnabled = false
                    framesWindow.deleteEnabled = false
                    framesWindow.scrollPanel.remove(framesWindow.frameButtons[framesWindow.frameButtons.size - 1])
                    framesWindow.frameButtons.removeAt(framesWindow.frameButtons.size - 1)
                    framesWindow.scrollPanel.repaint()
                    framesWindow.scrollPane.revalidate()
                    layersWindow.layerButtons.clear()
                    layersWindow.scrollPanel.removeAll()
                    layersWindow.scrollPane.revalidate()
                    layersWindow.newLayerButton.isEnabled = false
                    layersWindow.newEnabled = false
                    layersWindow.deleteLayerButton.isEnabled = false
                    layersWindow.deleteEnabled = false
                    layersWindow.renameEnabled = false
                    layersWindow.upLayerButton.isEnabled = false
                    layersWindow.upEnabled = false
                    layersWindow.downLayerButton.isEnabled = false
                    layersWindow.downEnabled = false
                }
                else {
                    JOptionPane.showMessageDialog(null, "No frames selected")
                }
            }
        }
        //Кнопочка поднятия кадра наверх
        framesWindow.upFrameButton.addActionListener {
            if (animation.curFrame > 0) {
                val tmp = animation.frames[animation.curFrame]
                animation.frames[animation.curFrame] = animation.frames[animation.curFrame - 1]
                animation.frames[animation.curFrame - 1] = tmp
                setCurFrame(animation.curFrame - 1)
            }
        }

        //Кнопочка опускания кадра вниз
        framesWindow.downFrameButton.addActionListener {
            if (animation.curFrame != -1 && animation.curFrame < animation.frames.size - 1) {
                val tmp = animation.frames[animation.curFrame]
                animation.frames[animation.curFrame] = animation.frames[animation.curFrame + 1]
                animation.frames[animation.curFrame + 1] = tmp
                setCurFrame(animation.curFrame + 1)
            }
        }

        //Слайдеры мини-окна со слайдерами изменения размера
        //Слайдер изменения размера слоя
        slidersWindow.sizeSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scale = slidersWindow.sizeSlider.value / 100f
                }
            }
        }

        //Слайдер изменения ширины слоя
        slidersWindow.widthSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scaleX = slidersWindow.widthSlider.value / 100f
                }
            }
        }

        //Слайдер изменения высоты слоя
        slidersWindow.heightSlider.addChangeListener {
            if (animation.curFrame != -1) {
                val frame = animation.frames[animation.curFrame]
                if (frame.curLayer != -1) {
                    val layer = frame.layers[frame.curLayer]
                    layer.scaleY = slidersWindow.heightSlider.value / 100f
                }
            }
        }

        //Добавляем возможность перемещать и поворачивать слои мышкой
        addMouseListener(object : MouseListener {
            override fun mouseReleased(e: MouseEvent) {
                isMouseMoving = false
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
                            isMouseMoving = true
                            mouseX = Math.round((e.x - (layer.x * panel.zoom / 100f + scrW / 2f)) * 100f / panel.zoom)
                            mouseY = Math.round(((layer.y + panel.centerY) * panel.zoom / 100f + scrH / 2f - e.y) * 100f / panel.zoom)
                        } else {
                            isMouseMoving = false
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
                        if (isMouseMoving) {
                            layer.x = (e.x - scrW / 2f) * 100f / panel.zoom - mouseX
                            layer.y = (e.y - scrH / 2f) * 100f / panel.zoom - panel.centerY + mouseY
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
        if (animationFile != null) {
            if (!animationFile!!.parentFile.parentFile.exists()) {
                animationFile!!.parentFile.parentFile.mkdir()
            }
            if (!animationFile!!.parentFile.exists()) {
                animationFile!!.parentFile.mkdir()
            }
            if (!animationFile!!.exists()) {
                animationFile!!.createNewFile()
            }
            animation.serialize(animationFile!!)
        }
    }

    /**
     * Создает новую пустую анимацию через диалоговые окна и сохраняет ее
     */
    private fun createNewAnimation() {
        var newAnimation : Animation? = null
        val typeChoice = JOptionPane.showOptionDialog(contentPane, "Choose animation's type", "New animation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Body animation", "Legs animation"), 0)
        when (typeChoice) {
            JOptionPane.YES_OPTION -> newAnimation = Animation(AnimationType.BODY)
            JOptionPane.NO_OPTION -> newAnimation = Animation(AnimationType.LEGS)
        }
        if (newAnimation != null) {
            val inputName: String? = JOptionPane.showInputDialog(this, "Enter the new animation's name (for example, FIREBALL)", "New animation", JOptionPane.PLAIN_MESSAGE)

            if (inputName.isNullOrBlank()) {
                JOptionPane.showMessageDialog(null, "Incorrect input")
            } else {
                newAnimation.name = inputName.trim()
                val path = animationDirectory.path + "/" + animation.type + "/" + newAnimation.name + ".swa"
                if (File(path).exists()) {
                    JOptionPane.showMessageDialog(null, "Animation with this name already exists")
                }
                else {
                    for (moveDirection in MoveDirection.values()) {
                        newAnimation.data[moveDirection] = HashMap()
                        if (newAnimation.type == AnimationType.LEGS) {
                            val arr = ArrayList<Frame>()
                            for (weaponType in WeaponType.values()) {
                                newAnimation.data[moveDirection]!![weaponType] = arr
                            }
                        } else if (newAnimation.type == AnimationType.BODY) {
                            for (weaponType in WeaponType.values()) {
                                newAnimation.data[moveDirection]!![weaponType] = ArrayList()
                            }
                        }
                    }
                    val tmp = animation
                    val tmpFile = animationFile
                    animation = newAnimation
                    animationFile = File(path)
                    serialize()
                    animation = tmp
                    animationFile = tmpFile
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
        if (!animationDirectory.exists()) {
            animationDirectory.mkdirs()
        }
        val fc = JFileChooser(animationDirectory)
        fc.addChoosableFileFilter(object : FileFilter() {

            override fun getDescription(): String {
                return "Shattered World animations (.SWA)"
            }

            override fun accept(f: File): Boolean {
                return f.name.endsWith(".swa")
            }
        })
        fc.dialogTitle = "Open animation"

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                val file = fc.selectedFile
                if (file.name.endsWith(".swa")) {
                    serialize()
                    animationFile = file
                    animationDirectory = animationFile!!.parentFile
                    animation = Animation(file)
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    framesWindow.frameButtons.clear()
                    framesWindow.scrollPanel.removeAll()
                    for (i in animation.frames.indices) {
                        val tmp = JButton("frame$i")
                        tmp.addActionListener {
                            setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                            framesWindow.deleteFrameButton.isEnabled = true
                            framesWindow.deleteEnabled = true
                        }
                        framesWindow.frameButtons.add(tmp)
                        framesWindow.scrollPanel.add(tmp)
                    }
                    framesWindow.scrollPane.revalidate()
                    framesWindow.scrollPanel.repaint()
                    for (cb in moveDirectionCheckboxes) {
                        cb.isSelected = (cb.text == animation.curMoveDirection.toString())
                    }
                    for (cb in weaponTypeCheckboxes) {
                        cb.isSelected = (cb.text == animation.curWeaponType.toString())
                    }
                    setCurFrame(animation.curFrame)
                    isAnimationRepeatableCheckbox.isSelected = animation.isRepeatable
                    animationNameText.text = animation.type.toString() + ": " + animation.name
                    panel.animType = animation.type
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
            layersWindow.layerButtons[animation.frames[animation.curFrame].curLayer].font = layersWindow.basicFont
        }
        layersWindow.layerButtons[layerID].font = layersWindow.selectedFont
        animation.frames[animation.curFrame].curLayer = layerID
        layersWindow.deleteLayerButton.isEnabled = true
        layersWindow.deleteEnabled = true
        layersWindow.renameEnabled = true
        layersWindow.upLayerButton.isEnabled = true
        layersWindow.upEnabled = true
        layersWindow.downLayerButton.isEnabled = true
        layersWindow.downEnabled = true

        val layer = animation.frames[animation.curFrame].layers[layerID]
        slidersWindow.sizeSlider.value = Math.round(layer.scale * 100)
        slidersWindow.widthSlider.value = Math.round(layer.scaleX * 100)
        slidersWindow.heightSlider.value = Math.round(layer.scaleY * 100)
        slidersWindow.isVisible = true
    }

    /**
     * Переключает активный кадр
     */
    private fun loadFrame(frameID: Int) {
        slidersWindow.isVisible = false
        layersWindow.deleteLayerButton.isEnabled = false
        layersWindow.deleteEnabled = false
        layersWindow.renameEnabled = false
        layersWindow.upLayerButton.isEnabled = false
        layersWindow.upEnabled = false
        layersWindow.downLayerButton.isEnabled = false
        layersWindow.downEnabled = false
        layersWindow.scrollPanel.removeAll()
        layersWindow.layerButtons.clear()
        if (frameID != -1) {
            layersWindow.newLayerButton.isEnabled = true
            layersWindow.newEnabled = true
            val frame = animation.frames[frameID]
            frame.curLayer = -1
            for (layer in frame.layers) {
                val tmp = JButton(layer.getName())
                tmp.addActionListener { loadLayer(layersWindow.layerButtons.indexOf(tmp)) }
                layersWindow.layerButtons.add(tmp)
                layersWindow.scrollPanel.add(tmp)
            }
            panel.frame = frame
        }
        else {
            panel.frame = null
            layersWindow.newLayerButton.isEnabled = false
            layersWindow.newEnabled = false
        }
        layersWindow.scrollPanel.repaint()
        layersWindow.scrollPane.revalidate()
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
            val mirroredFrames = animation.data[mirroredMD]!![animation.curWeaponType]!!
            mirroredFrames.clear()
            val curFrames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
            for (frame in curFrames) {
                mirroredFrames.add(Frame(frame))
            }
            for (mFrame in mirroredFrames) {
                mFrame.mirror(animation.curMoveDirection)
            }

        }
    }

    /**
     * Переключает кадр, обновляет кнопочки и перерисовывает экран
     */
    private fun setCurFrame(frameID : Int) {
        if (animation.curFrame != -1) {
            framesWindow.frameButtons[animation.curFrame].font = layersWindow.basicFont
        }
        animation.curFrame = frameID
        if (frameID == -1) {
            framesWindow.copyEnabled = false
            framesWindow.deleteEnabled = false
            framesWindow.upEnabled = false
            framesWindow.downEnabled = false
            framesWindow.isEnabled = !panel.isPlayingAnimation
        }
        else {
            framesWindow.copyEnabled = true
            framesWindow.deleteEnabled = true
            framesWindow.upEnabled = true
            framesWindow.downEnabled = true
            framesWindow.isEnabled = !panel.isPlayingAnimation
            framesWindow.frameButtons[animation.curFrame].font = layersWindow.selectedFont
        }
        loadFrame(frameID)
    }

    /**
     * Начинает воспроизведение анимации
     */
    private fun startAnimation() {
        toggleAnimationBtn.text = "Stop animation"
        isAnimationRepeatableCheckbox.isEnabled = false
        changeDurationBtn.isEnabled = false
        openAnotherAnimationBtn.isEnabled = false
        framesWindow.isEnabled = false
        layersWindow.isEnabled = false
        slidersWindow.isEnabled = false
        for (cb in moveDirectionCheckboxes) {
            cb.isEnabled = false
        }
        for (cb in weaponTypeCheckboxes) {
            cb.isEnabled = false
        }
        toggleAnimationBtn.foreground = Color.RED
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
        toggleAnimationBtn.text = "Start animation"
        toggleAnimationBtn.foreground = Color(0, 208, 0)
        isAnimationRepeatableCheckbox.isEnabled = true
        changeDurationBtn.isEnabled = true
        openAnotherAnimationBtn.isEnabled = true
        framesWindow.isEnabled = true
        layersWindow.isEnabled = true
        slidersWindow.isEnabled = true
        for (cb in moveDirectionCheckboxes) {
            cb.isEnabled = true
        }
        for (cb in weaponTypeCheckboxes) {
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

    /**
     * Функция, которая выводит окно с подтверждением выхода из редактора
     */
    private fun checkExit() {
        val ans = JOptionPane.showOptionDialog(contentPane, "Do you want to exit editor?\nAll you work will be saved.", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Save and exit", "Cancel"), 0)
        if (ans == JOptionPane.YES_OPTION) {
            serialize()
            System.exit(0)
        }
    }

    /**
     * Вспомогательная функция, вычисляет квадрат числа типа Double
     */
    private fun sqr(a: Double): Double {
        return a * a
    }


    /**
     * Точка входа в программу
     */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                MainWindow()
            }
            catch(ex: Exception) {
                JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
                System.exit(0)
            }
        }
    }
}
