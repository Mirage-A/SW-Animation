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
class FramesWindow : JFrame() {
    /**
     * Размер маленьких кнопочек
     */
    private val buttonSize = 20
    /**
     * Панель окна
     */
    internal val scrollPanel: JPanel
    /**
     * Прокручиваемая панелька, вложенная в scrollPanel и содержащая кнопки с выбором кадра
     */
    internal val scrollPane: JScrollPane
    /**
     * Кнопочка создания нового кадра
     */
    internal val newFrameButton: JButton
    /**
     * Кнопочка копирования выбранного кадра
     */
    internal val copyLastFrameButton: JButton
    /**
     * Кнопочка удаления выбранного кадра
     */
    internal val deleteFrameButton: JButton
    /**
     * Кнопочка поднятия выбранного кадра наверх
     */
    internal val upFrameButton: JButton
    /**
     * Кнопочка опускания выбранного кадра вниз
     */
    internal val downFrameButton: JButton
    /**
     * Активны ли данные кнопки (без учета того, активно ли все окно)
     */
    private var newEnabled = true
    internal var copyEnabled = true
    internal var deleteEnabled = true
    internal var upEnabled = true
    internal var downEnabled = true

    /**
     * Кнопки переключения кадра
     */
    internal var frameButtons: ArrayList<JButton>

    init {
        setSize(200, 250)
        title = "Frames"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        frameButtons = ArrayList()
        val panel = JPanel()
        panel.layout = null
        contentPane.add(panel)

        scrollPanel = JPanel()
        scrollPanel.layout = BoxLayout(scrollPanel, BoxLayout.Y_AXIS)
        scrollPane = JScrollPane(scrollPanel)
        scrollPane.setBounds(0, 0, width - 14, height - 46 - buttonSize)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.isVisible = true
        panel.add(scrollPane)

        newFrameButton = JButton(ImageIcon("./icons/create.png"))
        newFrameButton.setBounds(0, height - 47 - buttonSize, buttonSize, buttonSize)
        newFrameButton.setSize(buttonSize, buttonSize)
        newFrameButton.isVisible = true
        newFrameButton.toolTipText = "Create a new frame"
        newFrameButton.isEnabled = false
        panel.add(newFrameButton)

        copyLastFrameButton = JButton(ImageIcon("./icons/createCopy.png"))
        copyLastFrameButton.setBounds(buttonSize, height - 47 - buttonSize, buttonSize, buttonSize)
        copyLastFrameButton.setSize(buttonSize, buttonSize)
        copyLastFrameButton.isVisible = true
        copyLastFrameButton.toolTipText = "Create a copy of selected frame"
        copyLastFrameButton.isEnabled = false
        panel.add(copyLastFrameButton)

        deleteFrameButton = JButton(ImageIcon("./icons/delete.png"))
        deleteFrameButton.setBounds(buttonSize * 2, height - 47 - buttonSize, buttonSize, buttonSize)
        deleteFrameButton.setSize(buttonSize, buttonSize)
        deleteFrameButton.isVisible = true
        deleteFrameButton.toolTipText = "Delete selected frame"
        deleteFrameButton.isEnabled = false
        panel.add(deleteFrameButton)

        upFrameButton = JButton(ImageIcon("./icons/up.png"))
        upFrameButton.setBounds(buttonSize * 3, height - 47 - buttonSize, buttonSize, buttonSize)
        upFrameButton.setSize(buttonSize, buttonSize)
        upFrameButton.isVisible = true
        upFrameButton.toolTipText = "Move selected frame up"
        upFrameButton.isEnabled = false
        panel.add(upFrameButton)

        downFrameButton = JButton(ImageIcon("./icons/down.png"))
        downFrameButton.setBounds(buttonSize * 4, height - 47 - buttonSize, buttonSize, buttonSize)
        downFrameButton.setSize(buttonSize, buttonSize)
        downFrameButton.isVisible = true
        downFrameButton.toolTipText = "Move selected frame down"
        downFrameButton.isEnabled = false
        panel.add(downFrameButton)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                scrollPane.setBounds(0, 0, width - 14, height - 46 - buttonSize)
                newFrameButton.setBounds(0, height - 47 - buttonSize, buttonSize, buttonSize)
                copyLastFrameButton.setBounds(buttonSize, height - 47 - buttonSize, buttonSize, buttonSize)
                deleteFrameButton.setBounds(buttonSize * 2, height - 47 - buttonSize, buttonSize, buttonSize)
                upFrameButton.setBounds(buttonSize * 3, height - 47 - buttonSize, buttonSize, buttonSize)
                downFrameButton.setBounds(buttonSize * 4, height - 47 - buttonSize, buttonSize, buttonSize)
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
