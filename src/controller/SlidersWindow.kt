package controller

import java.io.File
import java.io.IOException

import javax.imageio.ImageIO
import javax.swing.*

/**
 * Дополнительное окно, позволяющее работать с размерами слоя
 */
object SlidersWindow : JFrame() {

    private val sliderWidth = 200
    private val sliderHeight = 20
    private val space = 0
    /**
     * Слайдер, отвечающий за размер слоя
     */
    internal var sizeSlider = JSlider(JSlider.HORIZONTAL, 25, 200, 100).apply {
        setBounds(space, 4 + space, sliderWidth, sliderHeight)
        isVisible = true
    }
    /**
     * Слайдер, отвечающий за ширину слоя
     */
    internal var widthSlider = JSlider(JSlider.HORIZONTAL, 25, 200, 100).apply {
        setBounds(space, 4 + space * 2 + sliderHeight, sliderWidth, sliderHeight)
        isVisible = true
    }
    /**
     * Слайдер, отвечающий за высоту слоя
     */
    internal var heightSlider = JSlider(JSlider.HORIZONTAL, 25, 200, 100).apply {
        setBounds(space, 4 + space * 3 + sliderHeight * 2, sliderWidth, sliderHeight)
        isVisible = true
    }

    private val sizeLabel = JLabel().apply {
        setBounds(sizeSlider.x + sizeSlider.width, sizeSlider.y, sliderHeight, sliderHeight)
        isVisible = true
    }

    private val widthLabel = JLabel().apply {
        setBounds(widthSlider.x + widthSlider.width, widthSlider.y, sliderHeight, sliderHeight)
        isVisible = true
    }

    private val heightLabel = JLabel().apply {
        setBounds(heightSlider.x + heightSlider.width, heightSlider.y, sliderHeight, sliderHeight)
        isVisible = true
    }

    private val panel = JPanel().apply {
        layout = null
        add(sizeSlider)
        add(widthSlider)
        add(heightSlider)
        add(sizeLabel)
        add(widthLabel)
        add(heightLabel)
    }

    init {
        setSize(sliderWidth + space * 2 + 42, 42 + sliderHeight * 3 + space * 4)
        title = "Layer size"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        contentPane.add(panel)
        isVisible = false
        try {
            sizeLabel.icon = ImageIcon(ImageIO.read(File("./icons/size.png")))
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(null, "File not found: icons/size.png")
        }
        try {
            widthLabel.icon = ImageIcon(ImageIO.read(File("./icons/width.png")))
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(null, "File not found: icons/width.png")
        }
        try {
            heightLabel.icon = ImageIcon(ImageIO.read(File("./icons/height.png")))
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(null, "File not found: icons/height.png")
        }

    }

    /**
     * Перегрузка метода setEnabled класса JFrame
     * Не изменяет активность самого окна, но изменяет активность всех элементов внутри окна
     */
    override fun setEnabled(b: Boolean) {
        sizeSlider.isEnabled = b
        widthSlider.isEnabled = b
        heightSlider.isEnabled = b
    }
}
