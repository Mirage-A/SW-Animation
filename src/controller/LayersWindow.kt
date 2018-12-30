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
class LayersWindow : JFrame() {
    /**
     * Размер маленьких кнопочек
     */
    private val buttonSize = 20
    /**
     * Панель окна
     */
    internal var scrollPanel: JPanel
    /**
     * Прокручиваемая панелька, вложенная в scrollPanel и содержащая кнопки с выбором слоя
     */
    internal var scrollPane: JScrollPane
    /**
     * Кнопочка создания нового слоя
     */
    internal var newLayerButton: JButton
    /**
     * Кнопочка удаления выбранного слоя
     */
    internal var deleteLayerButton: JButton
    /**
     * Кнопочка поднятия выбранного слоя наверх
     */
    internal var upLayerButton: JButton
    /**
     * Кнопочка опускания выбранного слоя вниз
     */
    internal var downLayerButton: JButton
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
        val panel = JPanel()
        panel.layout = null
        contentPane.add(panel)

        scrollPanel = JPanel()
        scrollPanel.layout = BoxLayout(scrollPanel, BoxLayout.Y_AXIS)
        scrollPane = JScrollPane(scrollPanel)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.isVisible = true
        panel.add(scrollPane)

        newLayerButton = JButton(ImageIcon("./icons/create.png"))
        newLayerButton.setSize(buttonSize, buttonSize)
        newLayerButton.isVisible = true
        newLayerButton.toolTipText = "Create a new layer"
        newLayerButton.isEnabled = false
        panel.add(newLayerButton)

        deleteLayerButton = JButton(ImageIcon("./icons/delete.png"))
        deleteLayerButton.setSize(buttonSize, buttonSize)
        deleteLayerButton.isVisible = true
        deleteLayerButton.toolTipText = "Delete selected layer"
        deleteLayerButton.isEnabled = false
        panel.add(deleteLayerButton)

        upLayerButton = JButton(ImageIcon("./icons/up.png"))
        upLayerButton.setSize(buttonSize, buttonSize)
        upLayerButton.isVisible = true
        upLayerButton.toolTipText = "Move selected layer up"
        upLayerButton.isEnabled = false
        panel.add(upLayerButton)

        downLayerButton = JButton(ImageIcon("./icons/down.png"))
        downLayerButton.setSize(buttonSize, buttonSize)
        downLayerButton.isVisible = true
        downLayerButton.toolTipText = "Move selected layer down"
        downLayerButton.isEnabled = false
        panel.add(downLayerButton)

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
