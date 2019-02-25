package controller

import javax.swing.ImageIcon
import javax.swing.JButton

object UIFactory {

    private const val buttonSize = 20

    fun createButton(iconName: String, toolTip: String = "") = JButton().apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = toolTip
        isEnabled = false
        icon = ImageIcon("./icons/$iconName.png")
    }
}