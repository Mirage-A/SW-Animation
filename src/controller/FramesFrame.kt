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

class FramesFrame : JFrame() {
    private val buttonSize = 20
    internal var scrollPanel: JPanel
    internal var scrollPane: JScrollPane
    internal var newFrameButton: JButton
    var newEnabled = true
    internal var copyLastFrameButton: JButton
    var copyEnabled = true
    internal var deleteFrameButton: JButton
    var deleteEnabled = true
    internal var upFrameButton: JButton
    var upEnabled = true
    internal var downFrameButton: JButton
    var downEnabled = true
    //internal var loadFrameButton: JButton
    internal var btns: ArrayList<JButton>

    init {
        setSize(200, 250)
        title = "Frames"
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

        newFrameButton = JButton(ImageIcon("./icons/create.png"))
        newFrameButton.setBounds(0, height - 47 - buttonSize, buttonSize, buttonSize)
        newFrameButton.setSize(buttonSize, buttonSize)
        newFrameButton.isVisible = true
        newFrameButton.toolTipText = "Create a new frame"
        panel.add(newFrameButton)

        copyLastFrameButton = JButton(ImageIcon("./icons/createCopy.png"))
        copyLastFrameButton.setBounds(buttonSize, height - 47 - buttonSize, buttonSize, buttonSize)
        copyLastFrameButton.setSize(buttonSize, buttonSize)
        copyLastFrameButton.isVisible = true
        copyLastFrameButton.toolTipText = "Create a copy of selected frame"
        panel.add(copyLastFrameButton)

        deleteFrameButton = JButton(ImageIcon("./icons/delete.png"))
        deleteFrameButton.setBounds(buttonSize * 2, height - 47 - buttonSize, buttonSize, buttonSize)
        deleteFrameButton.setSize(buttonSize, buttonSize)
        deleteFrameButton.isVisible = true
        deleteFrameButton.toolTipText = "Delete selected frame"
        deleteFrameButton.isEnabled = false
        panel.add(deleteFrameButton)

        /*loadFrameButton = JButton(ImageIcon("./icons/copy.png"))
        loadFrameButton.setBounds(buttonSize * 3, height - 47 - buttonSize, buttonSize, buttonSize)
        loadFrameButton.setSize(buttonSize, buttonSize)
        loadFrameButton.isVisible = true
        loadFrameButton.toolTipText = "Create a copy of another frame"
        panel.add(loadFrameButton)*/

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
                //loadFrameButton.setBounds(buttonSize * 3, height - 47 - buttonSize, buttonSize, buttonSize)
            }
        })
        scrollPane.revalidate()
        isVisible = false
    }

    override fun setEnabled(b: Boolean) {
        scrollPanel.isEnabled = b
        scrollPane.isEnabled = b
        newFrameButton.isEnabled = b && newEnabled
        copyLastFrameButton.isEnabled = b && copyEnabled
        deleteFrameButton.isEnabled = b && deleteEnabled
        upFrameButton.isEnabled = b && upEnabled
        downFrameButton.isEnabled = b && downEnabled
        //loadFrameButton.isEnabled = b
        for (btn in btns) {
            btn.isEnabled = b
        }
    }
}
