package controller

import java.awt.event.ActionListener
import java.io.IOException
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JOptionPane

object UIFactory {

    private const val buttonSize = 20

    fun createButton(iconName: String, toolTip: String = "", listener: ActionListener) = JButton().apply {
        setSize(buttonSize, buttonSize)
        isVisible = true
        toolTipText = toolTip
        isEnabled = false
        addActionListener(listener)
        try {
            icon = ImageIcon("./icons/$iconName.png")
        }
        catch(ex: IOException) {
            JOptionPane.showMessageDialog(null, "Cannot find file: icons/$iconName.png")
        }
    }
}