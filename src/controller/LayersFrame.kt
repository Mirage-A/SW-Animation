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

class LayersFrame : JFrame() {
    private val buttonSize = 20
    internal var scrollPanel: JPanel
    internal var scrollPane: JScrollPane
    internal var newLayerButton: JButton
    var newEnabled = true
    internal var deleteLayerButton: JButton
    var deleteEnabled = true
    internal var renameLayerButton: JButton
    var renameEnabled = true
    internal var upLayerButton: JButton
    var upEnabled = true
    internal var downLayerButton: JButton
    var downEnabled = true
    internal var btns: ArrayList<JButton>
    internal val basicFont = JButton().font
    internal val selectedFont = Font(basicFont.fontName, Font.BOLD, basicFont.size + 4)

    init {
        setSize(200, 250)
        title = "Layers"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        btns = ArrayList()
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

        newLayerButton = JButton(ImageIcon("./icons/create.png"))
        newLayerButton.setBounds(0, height - 47 - buttonSize, buttonSize, buttonSize)
        newLayerButton.setSize(buttonSize, buttonSize)
        newLayerButton.isVisible = true
        newLayerButton.toolTipText = "Create a new layer"
        newLayerButton.isEnabled = false
        panel.add(newLayerButton)

        deleteLayerButton = JButton(ImageIcon("./icons/delete.png"))
        deleteLayerButton.setBounds(buttonSize, height - 47 - buttonSize, buttonSize, buttonSize)
        deleteLayerButton.setSize(buttonSize, buttonSize)
        deleteLayerButton.isVisible = true
        deleteLayerButton.toolTipText = "Delete selected layer"
        deleteLayerButton.isEnabled = false
        panel.add(deleteLayerButton)

        renameLayerButton = JButton(ImageIcon("./icons/rename.png"))
        renameLayerButton.setBounds(buttonSize * 2, height - 47 - buttonSize, buttonSize, buttonSize)
        renameLayerButton.setSize(buttonSize, buttonSize)
        renameLayerButton.isVisible = true
        renameLayerButton.toolTipText = "Rename selected layer"
        renameLayerButton.isEnabled = false
        panel.add(renameLayerButton)

        upLayerButton = JButton(ImageIcon("./icons/up.png"))
        upLayerButton.setBounds(buttonSize * 3, height - 47 - buttonSize, buttonSize, buttonSize)
        upLayerButton.setSize(buttonSize, buttonSize)
        upLayerButton.isVisible = true
        upLayerButton.toolTipText = "Move selected layer up"
        upLayerButton.isEnabled = false
        panel.add(upLayerButton)

        downLayerButton = JButton(ImageIcon("./icons/down.png"))
        downLayerButton.setBounds(buttonSize * 4, height - 47 - buttonSize, buttonSize, buttonSize)
        downLayerButton.setSize(buttonSize, buttonSize)
        downLayerButton.isVisible = true
        downLayerButton.toolTipText = "Move selected layer down"
        downLayerButton.isEnabled = false
        panel.add(downLayerButton)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                scrollPane.setBounds(0, 0, width - 14, height - 46 - buttonSize)
                newLayerButton.setBounds(0, height - 47 - buttonSize, buttonSize, buttonSize)
                deleteLayerButton.setBounds(buttonSize, height - 47 - buttonSize, buttonSize, buttonSize)
                renameLayerButton.setBounds(buttonSize * 2, height - 47 - buttonSize, buttonSize, buttonSize)
                upLayerButton.setBounds(buttonSize * 3, height - 47 - buttonSize, buttonSize, buttonSize)
                downLayerButton.setBounds(buttonSize * 4, height - 47 - buttonSize, buttonSize, buttonSize)
            }
        })
        scrollPane.revalidate()
        isVisible = false
    }

    override fun setEnabled(b: Boolean) {
        scrollPanel.isEnabled = b
        scrollPane.isEnabled = b
        newLayerButton.isEnabled = b && newEnabled
        deleteLayerButton.isEnabled = b && deleteEnabled
        renameLayerButton.isEnabled = b && renameEnabled
        upLayerButton.isEnabled = b && upEnabled
        downLayerButton.isEnabled = b && downEnabled
        for (btn in btns) {
            btn.isEnabled = b
        }
    }
}
