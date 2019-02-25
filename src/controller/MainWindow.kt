package controller

import model.*
import view.MainPanel
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
object MainWindow : JFrame() {
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
     * Текст, отображающий на экране тип и название текущей анимации
     */
    private val animationNameText : JTextField

    /**
     * Инициализация всего интерфейса
     */
    init {
        extendedState = JFrame.MAXIMIZED_BOTH
        isUndecorated = true
        title = "Animation editor"
        size = Toolkit.getDefaultToolkit().screenSize
        contentPane.add(MainPanel)
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        // Кнопка выхода из редактора
        exitBtn = JButton("Save and exit")
        exitBtn.setBounds(width - 160 - 10, 10, 160, 24)
        exitBtn.addActionListener {
            checkExit()
        }
        exitBtn.isVisible = true
        MainPanel.add(exitBtn)

        //Кнопка изменения скорости перерисовки
        fpsBtn = JButton("FPS: 60")
        fpsBtn.setBounds(exitBtn.x, exitBtn.y + exitBtn.height + 4, exitBtn.width, exitBtn.height)
        fpsBtn.addActionListener {
            val input = JOptionPane.showInputDialog(null, "Input maximum FPS for editor\nHigher values may lower performance\nThis setting only affects animation showPlayerImageCheckbox in this editor\nDefault value : 60 FPS", 60)
            try {
                val newFPS = Integer.parseInt(input)
                if (newFPS > 0) {
                    fpsBtn.text = "FPS: $newFPS"
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
        fpsBtn.isVisible = true
        MainPanel.add(fpsBtn)

        // Кнопка загрузки другой анимации
        openAnotherAnimationBtn = JButton("Open animation")
        openAnotherAnimationBtn.run {
            setBounds(fpsBtn.x, fpsBtn.y + fpsBtn.height + 4, fpsBtn.width, fpsBtn.height)
            addActionListener {
                loadAnimation()
            }
            isVisible = true
        }
        MainPanel.add(openAnotherAnimationBtn)

        // Кнопка создания новой анимации
        newAnimationBtn = JButton("New animation")
        newAnimationBtn.run {
            setBounds(exitBtn.x, openAnotherAnimationBtn.y + openAnotherAnimationBtn.height + 4, openAnotherAnimationBtn.width, openAnotherAnimationBtn.height)
            addActionListener {
                createNewAnimation()
            }
            isVisible = true
        }
        MainPanel.add(newAnimationBtn)

        // Кнопка сохранения
        saveAnimationBtn = JButton("Save animation")
        saveAnimationBtn.run {
            setBounds(newAnimationBtn.x, newAnimationBtn.y + newAnimationBtn.height + 4, newAnimationBtn.width, newAnimationBtn.height)
            addActionListener {
                serialize()
            }
            isVisible = true
        }
        MainPanel.add(saveAnimationBtn)

        // Текст с названием и типом анимации
        animationNameText = JTextField("No animation loaded")
        animationNameText.run {
            setBounds(saveAnimationBtn.x, saveAnimationBtn.y + saveAnimationBtn.height + 4, saveAnimationBtn.width, saveAnimationBtn.height)
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = LayersWindow.selectedFont
            isEditable = false
            isVisible = true
        }
        MainPanel.add(animationNameText)



        // Чекбокс, который переключает отображение изображения игрока на фоне
        showPlayerImageCheckbox = JCheckBox("Show shape")
        showPlayerImageCheckbox.isSelected = true
        showPlayerImageCheckbox.setBounds(8, 10, 160, 24)
        showPlayerImageCheckbox.addChangeListener { MainPanel.drawPlayer = showPlayerImageCheckbox.isSelected }
        showPlayerImageCheckbox.isVisible = true
        MainPanel.add(showPlayerImageCheckbox)

        //Слайдер, позволяющий приближать и отдалять картинку
        zoomSlider = JSlider(100, 800, MainPanel.zoom)
        zoomSlider.setBounds(showPlayerImageCheckbox.x, showPlayerImageCheckbox.y + showPlayerImageCheckbox.height + 2, showPlayerImageCheckbox.width, showPlayerImageCheckbox.height)
        zoomSlider.addChangeListener {
            MainPanel.zoom = zoomSlider.value
            try {
                MainPanel.player = ImageIO.read(File("./drawable/player.png"))
                MainPanel.player = MainPanel.player.getScaledInstance(MainPanel.player.getWidth(null) * MainPanel.zoom / 100, MainPanel.player.getHeight(null) * MainPanel.zoom / 100, Image.SCALE_SMOOTH)
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(null, "Unexpected error occurred:\n" + ex.message, "Error :(", JOptionPane.ERROR_MESSAGE)
                System.exit(0)
            }
        }
        zoomSlider.isVisible = true
        MainPanel.add(zoomSlider)

        //Кнопка старта/остановки анимации
        toggleAnimationBtn = JButton("Start animation")
        toggleAnimationBtn.setBounds(zoomSlider.x, zoomSlider.y + zoomSlider.height + 2, zoomSlider.width, zoomSlider.height)
        toggleAnimationBtn.addActionListener {
            if (!MainPanel.isPlayingAnimation) {
                startAnimation()
            } else {
                stopAnimation()
            }
        }
        toggleAnimationBtn.foreground = Color(0, 208, 0)
        toggleAnimationBtn.isVisible = true
        MainPanel.add(toggleAnimationBtn)

        //Кнопка создания отраженной анимации
        createMirroredAnimationBtn = JButton("Mirror animation")
        createMirroredAnimationBtn.setBounds(toggleAnimationBtn.x, toggleAnimationBtn.y + toggleAnimationBtn.height + 4, toggleAnimationBtn.width, toggleAnimationBtn.height)
        createMirroredAnimationBtn.addActionListener {
            if (JOptionPane.showConfirmDialog(null, "Do you want to create a mirrored animation?", "Mirroring animation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                mirrorAnimation()
            }
        }
        createMirroredAnimationBtn.isVisible = true
        MainPanel.add(createMirroredAnimationBtn)

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
        MainPanel.add(changeDurationBtn)

        //Чекбокс, позволяющий выбрать, нужно ли воспроизводить анимацию по кругу или
        //остановить воспроизведение на последнем кадре
        isAnimationRepeatableCheckbox = JCheckBox("Repeatable")
        isAnimationRepeatableCheckbox.isSelected = false
        isAnimationRepeatableCheckbox.setBounds(changeDurationBtn.x, changeDurationBtn.y + changeDurationBtn.height + 4, changeDurationBtn.width, changeDurationBtn.height)
        isAnimationRepeatableCheckbox.addChangeListener {
            animation.isRepeatable = isAnimationRepeatableCheckbox.isSelected
        }
        isAnimationRepeatableCheckbox.isVisible = true
        MainPanel.add(isAnimationRepeatableCheckbox)

        //Текст "Move direction:"
        moveDirectionText = JTextField("Move direction:")
        moveDirectionText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = LayersWindow.selectedFont
            isEditable = false
            setBounds(isAnimationRepeatableCheckbox.x, isAnimationRepeatableCheckbox.y + isAnimationRepeatableCheckbox.height + 4, isAnimationRepeatableCheckbox.width, isAnimationRepeatableCheckbox.height)
            isVisible = true
        }
        MainPanel.add(moveDirectionText)


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
                    MainPanel.frame = null
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
                    MainPanel.frame = null
                }
                else {
                    cb.isSelected = true
                }
            }
            moveDirectionCheckboxes.add(cb)
            cb.isVisible = true
            MainPanel.add(cb)
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
        MainPanel.add(weaponTypeText)

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
                    MainPanel.frame = null
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
                    MainPanel.frame = null
                }
                else {
                    cb.isSelected = true
                }
            }
            weaponTypeCheckboxes.add(cb)
            cb.isVisible = true
            MainPanel.add(cb)
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


        addMouseListener(MouseListener)
        addMouseMotionListener(MouseListener)
        showStartMessage()
        MainPanel.t.start()
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
        val typeChoice = JOptionPane.showOptionDialog(contentPane, "Choose animation's type", "New animation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Body animation", "Legs animation", "Object animation"), 0)
        when (typeChoice) {
            JOptionPane.YES_OPTION -> newAnimation = Animation(AnimationType.BODY)
            JOptionPane.NO_OPTION -> newAnimation = Animation(AnimationType.LEGS)
            JOptionPane.CANCEL_OPTION -> newAnimation = Animation(AnimationType.OBJECT)
        }
        if (newAnimation != null) {
            val inputName: String? = JOptionPane.showInputDialog(this, "Enter the new animation's name (for example, FIREBALL)", "New animation", JOptionPane.PLAIN_MESSAGE)

            if (inputName.isNullOrBlank()) {
                JOptionPane.showMessageDialog(null, "Incorrect input")
            } else {
                newAnimation.name = inputName.trim()
                val path = animationDirectory.path + "/" + animation.type.toString() + "/" + newAnimation.name + ".swa"
                if (File(path).exists()) {
                    JOptionPane.showMessageDialog(null, "Animation with this name already exists")
                }
                else {
                    val commonArray = ArrayList<Frame>()
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
                        } else {
                            for (weaponType in WeaponType.values()) {
                                newAnimation.data[moveDirection]!![weaponType] = commonArray
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
                    MainPanel.animType = animation.type
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
    fun loadLayer(layerID: Int) {
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
    fun loadFrame(frameID: Int) {
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
            MainPanel.frame = frame
        }
        else {
            MainPanel.frame = null
            layersWindow.newLayerButton.isEnabled = false
            layersWindow.newEnabled = false
        }
        layersWindow.scrollPanel.repaint()
        layersWindow.scrollPane.revalidate()
        MainPanel.repaint()
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
    fun setCurFrame(frameID : Int) {
        if (animation.curFrame != -1) {
            framesWindow.frameButtons[animation.curFrame].font = layersWindow.basicFont
        }
        animation.curFrame = frameID
        if (frameID == -1) {
            framesWindow.copyEnabled = false
            framesWindow.deleteEnabled = false
            framesWindow.upEnabled = false
            framesWindow.downEnabled = false
            framesWindow.isEnabled = !MainPanel.isPlayingAnimation
        }
        else {
            framesWindow.copyEnabled = true
            framesWindow.deleteEnabled = true
            framesWindow.upEnabled = true
            framesWindow.downEnabled = true
            framesWindow.isEnabled = !MainPanel.isPlayingAnimation
            framesWindow.frameButtons[animation.curFrame].font = layersWindow.selectedFont
        }
        loadFrame(frameID)
    }

    /**
     * Начинает воспроизведение анимации
     */
    fun startAnimation() {
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
        MainPanel.frames = animation.frames
        MainPanel.isRepeatable = animation.isRepeatable
        MainPanel.duration = animation.duration + 0L
        MainPanel.startTime = System.currentTimeMillis()
        MainPanel.isPlayingAnimation = true
    }

    /**
     * Останавливает воспроизведение анимации
     */
    fun stopAnimation() {
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
        MainPanel.isPlayingAnimation = false
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


}
