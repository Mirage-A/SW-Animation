package controller

import model.*
import model.Model.animation
import view.MainPanel
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.*
import java.io.*
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter
import kotlin.collections.HashMap
import kotlin.collections.indices
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
    val moveDirectionCheckboxes = TreeMap<MoveDirection, JCheckBox>()
    /**
     * Чекбоксы с выбором типа оружия
     */
    val weaponTypeCheckboxes = TreeMap<WeaponType, JCheckBox>()
    /**
     * Чекбокс, определяющий, показывать ли эскиз гуманоида на фоне для помощи в подборе размера изображений
     */
    val showPlayerImageCheckbox : JCheckBox = JCheckBox("Show shape").apply {
        isSelected = true
        setBounds(8, 10, 160, 24)
        addChangeListener {
            MainPanel.drawPlayer = isSelected
        }
        isVisible = true
        MainPanel.add(this)
    }

    val colorPlayerCheckbox : JCheckBox = JCheckBox("Colored equipment").apply {
        isSelected = true
        setBounds(showPlayerImageCheckbox.x, showPlayerImageCheckbox.y + showPlayerImageCheckbox.height + 2,
                showPlayerImageCheckbox.width, showPlayerImageCheckbox.height)
        addChangeListener {
            MainPanel.colorPlayer = isSelected
        }
        isVisible = true
        MainPanel.add(this)
    }

    /**
     * Слайдер, позволяющий приближать/отдалять картинку в редакторе
     */
    val zoomSlider : JSlider = JSlider(100, 800, MainPanel.zoom).apply {
        setBounds(showPlayerImageCheckbox.x, colorPlayerCheckbox.y + colorPlayerCheckbox.height + 2,
                showPlayerImageCheckbox.width, showPlayerImageCheckbox.height)
        isVisible = true
        addChangeListener(zoomSliderListener)
        MainPanel.add(this)
    }
    /**
     * Кнопочка, позволяющая запустить или остановить анимацию
     */
    val toggleAnimationBtn : JButton = UIFactory.createButton(
            zoomSlider.x, zoomSlider.y + zoomSlider.height + 2, "Start animation", toggleAnimationButtonListener
    ).apply {
        foreground = Color(0, 208, 0)
    }
    /**
     * Кнопочка, позволяющая создать отраженную относительно вертикальной оси анимацию
     * для соответствующего направления движения
     */
    val createMirroredAnimationBtn: JButton = UIFactory.createButton(
            toggleAnimationBtn.x, toggleAnimationBtn.y + toggleAnimationBtn.height + 4,
            "Mirror animation", createMirroredAnimationButtonListener
    )
    /**
     * Кнопочка, позволяющая выбрать длительность (период) анимации
     */
    val changeDurationBtn : JButton = UIFactory.createButton(
            createMirroredAnimationBtn.x, createMirroredAnimationBtn.y + createMirroredAnimationBtn.height + 4,
            "Animation duration", changeDurationButtonListener
    )
    /**
     * Чекбокс, определяющий, повторяется ли анимация после завершения
     */
    val isAnimationRepeatableCheckbox: JCheckBox = UIFactory.createCheckbox(
            changeDurationBtn.x, changeDurationBtn.y + changeDurationBtn.height + 4,
            "Repeatable", isAnimationRepeatableCheckboxListener)
    /**
     * Текст "Move direction: " над чекбоксами с выбором направления движения
     */
    val moveDirectionText: JTextField
    /**
     * Текст "Weapon type: " над чекбоксами с выбором типа оружия
     */
    val weaponTypeText : JTextField
    /**
     * Элементы интерфейса в правом верхнем углу
     */
    /**
     * Кнопочка, отвечающая за выход из редактора
     */
    val exitBtn : JButton = UIFactory.createButton(
            Toolkit.getDefaultToolkit().screenSize.width - 160 - 10, 10, "Save and exit", exitButtonListener)
    /**
     * Кнопочка, позволяюшая выбрать FPS перерисовки анимации в редакторе
     */
    val fpsBtn : JButton = UIFactory.createButton(
            exitBtn.x, exitBtn.y + exitBtn.height + 4, "FPS: 60", fpsButtonListener
    )
    /**
     * Кнопочка, позволяющая выбрать и загрузить другую анимацию
     */
    val openAnotherAnimationBtn : JButton = UIFactory.createButton(
            fpsBtn.x, fpsBtn.y + fpsBtn.height + 4, "Open animation", openAnotherAnimationButtonListener
    )
    /**
     * Кнопочка, позволяющая создать новую анимацию
     */
    val newAnimationBtn : JButton = UIFactory.createButton(
            exitBtn.x, openAnotherAnimationBtn.y + openAnotherAnimationBtn.height + 4, "New animation", createNewAnimationButtonListener
    )
    /**
     * Кнопочка, позволяющая сохранить (сериализовать) текущую анимацию
     */
    val saveAnimationBtn : JButton = UIFactory.createButton(
            newAnimationBtn.x, newAnimationBtn.y + newAnimationBtn.height + 4, "Save animation", saveAnimationButtonListener
    )
    /**
     * Текст, отображающий на экране тип и название текущей анимации
     */
    val animationNameText : JTextField = JTextField("No animation loaded").apply {
        setBounds(saveAnimationBtn.x, saveAnimationBtn.y + saveAnimationBtn.height + 4, saveAnimationBtn.width, saveAnimationBtn.height)
        isOpaque = false
        horizontalAlignment = JTextField.CENTER
        font = LayersWindow.selectedFont
        isEditable = false
        isVisible = true
        MainPanel.add(this)
    }
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

        //Чекбокс, позволяющий выбрать, нужно ли воспроизводить анимацию по кругу или
        //остановить воспроизведение на последнем кадре

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
            moveDirectionCheckboxes[md] = UIFactory.createCheckbox(toggleAnimationBtn.x, moveDirectionText.y + (toggleAnimationBtn.height + 4) * (moveDirectionCheckboxes.size + 1) + 4,
                    md.toString(), getMoveDirectionCheckboxListener(md))
        }

        //Текст "Weapon type:"
        weaponTypeText = JTextField("Weapon type:")
        weaponTypeText.run {
            isOpaque = false
            horizontalAlignment = JTextField.CENTER
            font = LayersWindow.selectedFont
            isEditable = false
            setBounds(moveDirectionCheckboxes[MoveDirection.DOWN_RIGHT]!!.x, moveDirectionCheckboxes[MoveDirection.DOWN_RIGHT]!!.y + moveDirectionCheckboxes[MoveDirection.DOWN_RIGHT]!!.height + 4, moveDirectionCheckboxes[MoveDirection.DOWN_RIGHT]!!.width, moveDirectionCheckboxes[MoveDirection.DOWN_RIGHT]!!.height)
            isVisible = true
        }
        MainPanel.add(weaponTypeText)

        //Чекбоксы выбора weaponType-а
        for (wt in WeaponType.values()) {
            weaponTypeCheckboxes[wt] = UIFactory.createCheckbox(
                    toggleAnimationBtn.x, weaponTypeText.y + (toggleAnimationBtn.height + 4) * (weaponTypeCheckboxes.size + 1) + 4,
                    wt.toString(), getWeaponTypeCheckboxListener(wt))
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
        LayersWindow.run {
            setLocation(screen.width - width - 20, screen.height - height - 60)
            isVisible = true
        }
        FramesWindow.run {
            setLocation(screen.width - width - 20, screen.height - LayersWindow.height - height - 80)
            isVisible = true
        }
        SlidersWindow.run {
            setLocation(LayersWindow.x - 20 - width, screen.height - 60 - height)
            isVisible = false
        }

        addMouseListener(MouseListener)
        addMouseMotionListener(MouseListener)
        showStartMessage()
        MainPanel.t.start()
    }


    /**
     * Создает новую пустую анимацию через диалоговые окна и сохраняет ее
     */
    fun createNewAnimation() {
        var newAnimation : Animation? = null
        val typeChoice = JOptionPane.showOptionDialog(contentPane, "Choose animation's type", "New animation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Body animation", "Legs animation", "Object animation"), 0)
        when (typeChoice) {
            JOptionPane.YES_OPTION -> newAnimation = Animation(AnimationType.BODY)
            JOptionPane.NO_OPTION -> newAnimation = Animation(AnimationType.LEGS)
            JOptionPane.CANCEL_OPTION -> newAnimation = Animation(AnimationType.OBJECT)
        }
        if (newAnimation != null) {
            val inputName: String? = JOptionPane.showInputDialog(this, "Enter the new animation's name (for example, fireball)", "New animation", JOptionPane.PLAIN_MESSAGE)

            if (inputName.isNullOrBlank()) {
                JOptionPane.showMessageDialog(null, "Incorrect input")
            } else {
                newAnimation.name = inputName.trim()
                val folder = when (newAnimation.type) {
                    AnimationType.BODY -> "humanoid/body"
                    AnimationType.LEGS -> "humanoid/legs"
                    AnimationType.OBJECT -> "object"
                }
                val path = Model.rootAnimationDirectory.path + "/" + folder + "/" + newAnimation.name + ".xml"
                if (File(path).exists()) {
                    JOptionPane.showMessageDialog(null, "Animation with this name already exists")
                }
                else {
                    val commonArray = ArrayList<Frame>()
                    for (moveDirection in MoveDirection.values()) {
                        newAnimation.data[moveDirection] = HashMap()
                        when (newAnimation.type) {
                            AnimationType.LEGS -> {
                                val arr = ArrayList<Frame>()
                                for (weaponType in WeaponType.values()) {
                                    newAnimation.data[moveDirection]!![weaponType] = arr
                                }
                            }
                            AnimationType.BODY -> {
                                for (weaponType in WeaponType.values()) {
                                    newAnimation.data[moveDirection]!![weaponType] = ArrayList()
                                }
                            }
                            else -> {
                                for (weaponType in WeaponType.values()) {
                                    newAnimation.data[moveDirection]!![weaponType] = commonArray
                                }
                            }
                        }
                    }
                    val tmp = animation
                    val tmpFile = Model.animationFile
                    animation = newAnimation
                    Model.animationFile = File(path)
                    Model.serialize()
                    animation = tmp
                    Model.animationFile = tmpFile
                    JOptionPane.showMessageDialog(this, "Animation created!", "New animation", JOptionPane.PLAIN_MESSAGE)
                }
            }
        }
    }

    /**
     * Открывает окно выбора анимации, десереализует и загружает выбранную анимацию
     * Возвращает true, если анимация успешно загружена, и false иначе
     */
    fun loadAnimation() : Boolean {
        if (!Model.rootAnimationDirectory.exists()) {
            Model.rootAnimationDirectory.mkdirs()
        }
        val fc = JFileChooser(Model.rootAnimationDirectory)
        fc.addChoosableFileFilter(object : FileFilter() {

            override fun getDescription(): String {
                return "Shattered World animations (.xml)"
            }

            override fun accept(f: File): Boolean {
                return f.name.endsWith(".xml")
            }
        })
        fc.dialogTitle = "Open animation"

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                val file = fc.selectedFile
                if (file.name.endsWith(".xml")) {
                    Model.serialize()
                    Model.animationFile = file
                    Model.animationDirectory = Model.animationFile!!.parentFile
                    animation = Animation(file)
                    animation.frames = animation.data[animation.curMoveDirection]!![animation.curWeaponType]!!
                    FramesWindow.run {
                        frameButtons.clear()
                        scrollPanel.removeAll()
                        for (i in animation.frames.indices) {
                            val tmp = JButton("frame$i")
                            tmp.addActionListener {
                                setCurFrame(Integer.parseInt(tmp.text.substring(5)))
                                deleteFrameButton.isEnabled = true
                                deleteEnabled = true
                            }
                            frameButtons.add(tmp)
                            scrollPanel.add(tmp)
                        }
                        scrollPane.revalidate()
                        scrollPanel.repaint()
                    }
                    for (cb in moveDirectionCheckboxes.values) {
                        cb.isSelected = (cb.text == animation.curMoveDirection.toString())
                    }
                    for (cb in weaponTypeCheckboxes.values) {
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
        LayersWindow.run {
            if (animation.frames[animation.curFrame].curLayer != -1) {
                layerButtons[animation.frames[animation.curFrame].curLayer].font = basicFont
            }
            layerButtons[layerID].font = selectedFont
            animation.frames[animation.curFrame].curLayer = layerID
            deleteLayerButton.isEnabled = true
            deleteEnabled = true
            renameEnabled = true
            upLayerButton.isEnabled = true
            upEnabled = true
            downLayerButton.isEnabled = true
            downEnabled = true
        }

        val layer = animation.frames[animation.curFrame].layers[layerID]
        SlidersWindow.run {
            flipCheckBox.isSelected = layer.flipX
            sizeSlider.value = Math.round(layer.scale * 100)
            widthSlider.value = Math.round(layer.scaleX * 100)
            heightSlider.value = Math.round(layer.scaleY * 100)
            isVisible = true
        }
    }

    /**
     * Переключает активный кадр
     */
    fun loadFrame(frameID: Int) {
        SlidersWindow.isVisible = false
        LayersWindow.run {
            deleteLayerButton.isEnabled = false
            deleteEnabled = false
            renameEnabled = false
            upLayerButton.isEnabled = false
            upEnabled = false
            downLayerButton.isEnabled = false
            downEnabled = false
            scrollPanel.removeAll()
            layerButtons.clear()
            if (frameID != -1) {
                newLayerButton.isEnabled = true
                newEnabled = true
                val frame = animation.frames[frameID]
                frame.curLayer = -1
                for (layer in frame.layers) {
                    val tmp = JButton(layer.imageName)
                    tmp.addActionListener {
                        loadLayer(layerButtons.indexOf(tmp))
                    }
                    layerButtons.add(tmp)
                    scrollPanel.add(tmp)
                }
                MainPanel.frame = frame
            } else {
                MainPanel.frame = null
                newLayerButton.isEnabled = false
                newEnabled = false
            }
            scrollPanel.repaint()
            scrollPane.revalidate()
        }
        MainPanel.repaint()
    }


    /**
     * Переключает кадр, обновляет кнопочки и перерисовывает экран
     */
    fun setCurFrame(frameID : Int) {
        if (animation.curFrame != -1) {
            FramesWindow.frameButtons[animation.curFrame].font = LayersWindow.basicFont
        }
        animation.curFrame = frameID
        if (frameID == -1) {
            FramesWindow.run {
                copyEnabled = false
                deleteEnabled = false
                upEnabled = false
                downEnabled = false
                isEnabled = !MainPanel.isPlayingAnimation
            }
        }
        else {
            FramesWindow.run {
                copyEnabled = true
                deleteEnabled = true
                upEnabled = true
                downEnabled = true
                isEnabled = !MainPanel.isPlayingAnimation
                frameButtons[animation.curFrame].font = LayersWindow.selectedFont
            }
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
        FramesWindow.isEnabled = false
        LayersWindow.isEnabled = false
        SlidersWindow.isEnabled = false
        for (cb in moveDirectionCheckboxes.values) {
            cb.isEnabled = false
        }
        for (cb in weaponTypeCheckboxes.values) {
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
        FramesWindow.isEnabled = true
        LayersWindow.isEnabled = true
        SlidersWindow.isEnabled = true
        for (cb in moveDirectionCheckboxes.values) {
            cb.isEnabled = true
        }
        for (cb in weaponTypeCheckboxes.values) {
            cb.isEnabled = true
        }
        MainPanel.isPlayingAnimation = false
    }

    /**
     * Показывает стартовое окно с возможностью создать новую анимацию, загрузить существующую или выйти из редактора
     */
    fun showStartMessage() {
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
    fun checkExit() {
        val ans = JOptionPane.showOptionDialog(contentPane, "Do you want to exit editor?\nAll you work will be saved.", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Save and exit", "Cancel"), 0)
        if (ans == JOptionPane.YES_OPTION) {
            Model.serialize()
            System.exit(0)
        }
    }


}
