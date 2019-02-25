package controller

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.ArrayList

import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

/**
 * Дополнительное окно, позволяющее работать с кадрами анимации
 */
object FramesWindow : JFrame() {
    /**
     * Размер маленьких кнопочек
     */
    private val buttonSize = 20
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
    internal val newFrameButton = JButton(ImageIcon("./icons/create.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Create a new frame"
        isEnabled = false
    }
    /**
     * Кнопочка копирования выбранного кадра
     */
    internal val copyLastFrameButton = JButton(ImageIcon("./icons/createCopy.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Create a copy of selected frame"
        isEnabled = false
    }
    /**
     * Кнопочка удаления выбранного кадра
     */
    internal val deleteFrameButton = JButton(ImageIcon("./icons/delete.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Delete selected frame"
        isEnabled = false
    }
    /**
     * Кнопочка поднятия выбранного кадра наверх
     */
    internal val upFrameButton = JButton(ImageIcon("./icons/up.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Move selected frame up"
        isEnabled = false
    }
    /**
     * Кнопочка опускания выбранного кадра вниз
     */
    internal val downFrameButton = JButton(ImageIcon("./icons/down.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Move selected frame down"
        isEnabled = false
    }
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

    internal val panel = JPanel().apply {
        layout = null
        add(scrollPane)
        add(newFrameButton)
        add(copyLastFrameButton)
        add(deleteFrameButton)
        add(upFrameButton)
        add(downFrameButton)
    }

    private val yMargin = 39

    init {
        setSize(200, 250)
        title = "Frames"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE

        contentPane.add(panel)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                scrollPane.setBounds(0, 0, width - 14, height - yMargin + 1 - buttonSize)
                newFrameButton.setBounds(0, height - yMargin - buttonSize, buttonSize, buttonSize)
                copyLastFrameButton.setBounds(buttonSize, height - yMargin - buttonSize, buttonSize, buttonSize)
                deleteFrameButton.setBounds(buttonSize * 2, height - yMargin - buttonSize, buttonSize, buttonSize)
                upFrameButton.setBounds(buttonSize * 3, height - yMargin - buttonSize, buttonSize, buttonSize)
                downFrameButton.setBounds(buttonSize * 4, height - yMargin - buttonSize, buttonSize, buttonSize)
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
