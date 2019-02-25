package controller

import java.awt.Font
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
 * Дополнительное окно, позволяющее работать со слоями
 */
object LayersWindow : JFrame() {
    /**
     * Размер маленьких кнопочек
     */
    private val buttonSize = 20
    /**
     * Панель окна
     */
    internal var scrollPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }
    /**
     * Прокручиваемая панелька, вложенная в scrollPanel и содержащая кнопки с выбором слоя
     */
    internal var scrollPane = JScrollPane(scrollPanel).apply {
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        isVisible = true
    }
    /**
     * Кнопочка создания нового слоя
     */
    internal var newLayerButton = JButton(ImageIcon("./icons/create.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Create a new layer"
        isEnabled = false
    }
    /**
     * Кнопочка удаления выбранного слоя
     */
    internal var deleteLayerButton = JButton(ImageIcon("./icons/delete.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Delete selected layer"
        isEnabled = false
    }
    /**
     * Кнопочка поднятия выбранного слоя наверх
     */
    internal var upLayerButton = JButton(ImageIcon("./icons/up.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Move selected layer up"
        isEnabled = false
    }
    /**
     * Кнопочка опускания выбранного слоя вниз
     */
    internal var downLayerButton = JButton(ImageIcon("./icons/down.png")).apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = "Move selected layer down"
        isEnabled = false
    }

    private val panel = JPanel().apply {
        layout = null
        add(scrollPane)
        add(newLayerButton)
        add(deleteLayerButton)
        add(upLayerButton)
        add(downLayerButton)
    }
    /**
     * Активны ли данные кнопки (без учета того, активно ли все окно)
     */
    internal var newEnabled = true
    internal var deleteEnabled = true
    internal var renameEnabled = true
    internal var upEnabled = true
    internal var downEnabled = true
    /**
     * Кнопки переключения слоя
     */
    internal var layerButtons: ArrayList<JButton>
    /**
     * Шрифт кнопки, отвечающей за неактивный слой
     */
    internal val basicFont = JButton().font
    /**
     * Шрифт кнопки, отвечающей за активный слой
     */
    internal val selectedFont = Font(basicFont.fontName, Font.BOLD, basicFont.size + 4)

    init {
        setSize(200, 250)
        title = "Layers"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        layerButtons = ArrayList()
        contentPane.add(panel)


        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                scrollPane.setBounds(0, 0, width - 14, height - 38 - buttonSize)
                newLayerButton.setBounds(0, height - 39 - buttonSize, buttonSize, buttonSize)
                deleteLayerButton.setBounds(buttonSize, height - 39 - buttonSize, buttonSize, buttonSize)
                upLayerButton.setBounds(buttonSize * 2, height - 39 - buttonSize, buttonSize, buttonSize)
                downLayerButton.setBounds(buttonSize * 3, height - 39 - buttonSize, buttonSize, buttonSize)
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
        newLayerButton.isEnabled = b && newEnabled
        deleteLayerButton.isEnabled = b && deleteEnabled
        upLayerButton.isEnabled = b && upEnabled
        downLayerButton.isEnabled = b && downEnabled
        for (btn in layerButtons) {
            btn.isEnabled = b
        }
    }
}
