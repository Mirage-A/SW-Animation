package controller

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import java.util.ArrayList
import javax.imageio.ImageIO

import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

/**
 * Дополнительное окно, позволяющее работать с кадрами анимации
 */
object FramesWindow : JFrame() {

    internal val panel = JPanel().apply {
        layout = null
    }

    /**
     * Панель окна
     */
    internal val scrollPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }
    /**
     * Прокручиваемая панелька, вложенная в scrollPanel и содержащая кнопки с выбором кадра
     */
    internal val scrollPane = JScrollPane(scrollPanel).apply {
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        isVisible = true
    }
    /**
     * Кнопочка создания нового кадра
     */
    internal val newFrameButton : JButton = UIFactory.createMiniButton(
            "create", "Create a new frame", panel, newFrameButtonListener)
    /**
     * Кнопочка копирования выбранного кадра
     */
    internal val copyLastFrameButton : JButton = UIFactory.createMiniButton(
            "createCopy", "Create a copy of selected frame", panel, copyLastFrameButtonListener)
    /**
     * Кнопочка удаления выбранного кадра
     */
    internal val deleteFrameButton : JButton = UIFactory.createMiniButton(
            "delete", "Delete selected frame", panel, deleteFrameButtonListener)
    /**
     * Кнопочка поднятия выбранного кадра наверх
     */
    internal val upFrameButton : JButton = UIFactory.createMiniButton(
            "up", "Move selected frame up", panel, upFrameButtonListener)
    /**
     * Кнопочка опускания выбранного кадра вниз
     */
    internal val downFrameButton : JButton = UIFactory.createMiniButton(
            "down", "Move selected frame down", panel, downFrameButtonListener)
    /**
     * Активны ли данные кнопки (без учета того, активно ли все окно)
     */
    internal var newEnabled = true
    internal var copyEnabled = true
    internal var deleteEnabled = true
    internal var upEnabled = true
    internal var downEnabled = true

    /**
     * Кнопки переключения кадра
     */
    internal var frameButtons = ArrayList<JButton>()

    init {
        iconImage = ImageIO.read(File("./icon.png"))
        isUndecorated = true
        setSize(200, 250)
        title = "Frames"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        panel.add(scrollPane)
        contentPane.add(panel)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                scrollPane.setBounds(0, 0, width, height + 1 - UIFactory.MINI_BUTTON_SIZE)
                newFrameButton.setLocation(0, height - UIFactory.MINI_BUTTON_SIZE)
                copyLastFrameButton.setLocation(UIFactory.MINI_BUTTON_SIZE, height - UIFactory.MINI_BUTTON_SIZE)
                deleteFrameButton.setLocation(UIFactory.MINI_BUTTON_SIZE * 2, height - UIFactory.MINI_BUTTON_SIZE)
                upFrameButton.setLocation(UIFactory.MINI_BUTTON_SIZE * 3, height - UIFactory.MINI_BUTTON_SIZE)
                downFrameButton.setLocation(UIFactory.MINI_BUTTON_SIZE * 4, height - UIFactory.MINI_BUTTON_SIZE)
                scrollPane.revalidate()
            }
        })
        scrollPane.revalidate()
        isVisible = false
    }

    /**
     * Перегрузка метода setEnabled класса JFrame
     * Не изменяет активность самого окна, но изменяет активность всех элементов внутри окна
     * (кнопочек, панели с кнопками выбора кадра и этих кнопок)
     * setEnabled(true) также учитывает для маленьких кнопочек то, выполняются ли условия для их активности
     * (учитываются значения newEnabled, copyEnabled и т.д.)
     */
    override fun setEnabled(b: Boolean) {
        scrollPanel.isEnabled = b
        scrollPane.isEnabled = b
        newFrameButton.isEnabled = b && newEnabled
        copyLastFrameButton.isEnabled = b && copyEnabled
        deleteFrameButton.isEnabled = b && deleteEnabled
        upFrameButton.isEnabled = b && upEnabled
        downFrameButton.isEnabled = b && downEnabled
        for (btn in frameButtons) {
            btn.isEnabled = b
        }
    }
}
