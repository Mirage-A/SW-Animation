package controller

import view.MainPanel
import java.awt.event.ActionListener
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.ChangeListener

object UIFactory {

    const val MINI_BUTTON_SIZE = 20
    const val BUTTON_WIDTH = 160
    const val BUTTON_HEIGHT = 24

    fun createButton(x: Int, y: Int, text: String, listener: ActionListener) = JButton().apply {
        setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
        isVisible = true
        this.text = text
        addActionListener(listener)
        MainPanel.add(this)
    }

    fun createCheckbox(x: Int, y: Int, text: String, listener: ActionListener) = JCheckBox(text).apply {
        setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
        isVisible = true
        MainPanel.add(this)
        addActionListener(listener)
    }

    fun createMiniButton(iconName: String, toolTip: String = "", panel: JPanel, listener: ActionListener) = JButton().apply {
        setSize(MINI_BUTTON_SIZE, MINI_BUTTON_SIZE)
        isVisible = true
        toolTipText = toolTip
        isEnabled = false
        addActionListener(listener)
        panel.add(this)
        try {
            icon = ImageIcon("./icons/$iconName.png")
        }
        catch(ex: IOException) {
            JOptionPane.showMessageDialog(null, "Cannot find file: icons/$iconName.png")
        }
    }

    private const val SLIDER_WIDTH = 200
    private const val SLIDER_HEIGHT = 20
    private const val SLIDER_MIN_VALUE = 10
    private const val SLIDER_MAX_VALUE = 200
    private const val SLIDER_DEFAULT_VALUE = 100

    fun createSlider(panel: JPanel, listener: ChangeListener) = JSlider(JSlider.HORIZONTAL, SLIDER_MIN_VALUE, SLIDER_MAX_VALUE, SLIDER_DEFAULT_VALUE).apply {
        setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
        addChangeListener(listener)
        isVisible = true
        panel.add(this)
    }

    fun createLabel(iconName: String, panel: JPanel) = JLabel().apply {
        try {
            icon = ImageIcon(ImageIO.read(File("./icons/$iconName.png")))
        }
        catch (ex: IOException) {
            JOptionPane.showMessageDialog(null, "File not found: icons/$iconName.png")
        }
        isVisible = true
        panel.add(this)
    }

}