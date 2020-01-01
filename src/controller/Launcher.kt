package controller

import javax.swing.JOptionPane
import kotlin.system.exitProcess

fun main() {
    try {
        MainWindow
    }
    catch(ex: Exception) {
        JOptionPane.showMessageDialog(
                null,
                "Unexpected error occurred:\n" + ex.message,
                "Error :(",
                JOptionPane.ERROR_MESSAGE
        )
        exitProcess(0)
    }
}