package controller

import java.awt.Font
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.util.ArrayList

import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

/**
 * Дополнительное окно, позволяющее работать со слоями
 */
object LayersWindow : JFrame() {

    private val panel = JPanel().apply {
        layout = null
    }

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
    internal var newLayerButton : JButton = UIFactory.createMiniButton(
            "create", "Create a new layer", panel, newLayerButtonListener)
    /**
     * Кнопочка удаления выбранного слоя
     */
    internal var deleteLayerButton : JButton = UIFactory.createMiniButton(
            "delete", "Delete selected layer", panel, deleteLayerButtonListener)
    /**
     * Кнопочка поднятия выбранного слоя наверх
     */
    internal var upLayerButton : JButton = UIFactory.createMiniButton(
            "up", "Move selected layer up", panel, upLayerButtonListener)
    /**
     * Кнопочка опускания выбранного слоя вниз
     */
    internal var downLayerButton : JButton = UIFactory.createMiniButton(
            "down", "Move selected layer down", panel, downLayerButtonListener)

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
        isUndecorated = true
        setSize(200, 250)
        title = "Layers"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        layerButtons = ArrayList()
        panel.add(scrollPane)
        contentPane.add(panel)


        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                scrollPane.setBounds(0, 0, width, height - UIFactory.MINI_BUTTON_SIZE)
                newLayerButton.setLocation(0, height - UIFactory.MINI_BUTTON_SIZE)
                deleteLayerButton.setLocation(UIFactory.MINI_BUTTON_SIZE, height - UIFactory.MINI_BUTTON_SIZE)
                upLayerButton.setLocation(UIFactory.MINI_BUTTON_SIZE * 2, height - UIFactory.MINI_BUTTON_SIZE)
                downLayerButton.setLocation(UIFactory.MINI_BUTTON_SIZE * 3, height - UIFactory.MINI_BUTTON_SIZE)
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
