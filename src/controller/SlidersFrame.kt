package controller

import java.io.File

import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider

/**
 * Дополнительное окно, позволяющее работать с размерами слоя
 */
class SlidersFrame : JFrame() {
    /**
     * Слайдер, отвечающий за размер слоя
     */
    internal var sizeSlider: JSlider
    /**
     * Слайдер, отвечающий за ширину слоя
     */
    internal var widthSlider: JSlider
    /**
     * Слайдер, отвечающий за высоту слоя
     */
    internal var heightSlider: JSlider

    init {
        val sliderWidth = 200
        val sliderHeight = 20
        val space = 0

        setSize(sliderWidth + space * 2 + 42, 42 + sliderHeight * 3 + space * 4)
        title = "Layer size"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE

        val panel = JPanel()
        panel.layout = null
        contentPane.add(panel)
        isVisible = false

        sizeSlider = JSlider(JSlider.HORIZONTAL, 25, 200, 100)
        widthSlider = JSlider(JSlider.HORIZONTAL, 25, 200, 100)
        heightSlider = JSlider(JSlider.HORIZONTAL, 25, 200, 100)

        sizeSlider.setBounds(space, 4 + space, sliderWidth, sliderHeight)
        widthSlider.setBounds(space, 4 + space * 2 + sliderHeight, sliderWidth, sliderHeight)
        heightSlider.setBounds(space, 4 + space * 3 + sliderHeight * 2, sliderWidth, sliderHeight)

        sizeSlider.isVisible = true
        widthSlider.isVisible = true
        heightSlider.isVisible = true

        panel.add(sizeSlider)
        panel.add(widthSlider)
        panel.add(heightSlider)
        try {
            val sizeLabel = JLabel(ImageIcon(ImageIO.read(File("./icons/size.png"))))
            sizeLabel.setBounds(sizeSlider.x + sizeSlider.width, sizeSlider.y, sliderHeight, sliderHeight)
            sizeLabel.isVisible = true
            panel.add(sizeLabel)

            val widthLabel = JLabel(ImageIcon(ImageIO.read(File("./icons/width.png"))))
            widthLabel.setBounds(widthSlider.x + widthSlider.width, widthSlider.y, sliderHeight, sliderHeight)
            widthLabel.isVisible = true
            panel.add(widthLabel)

            val heightLabel = JLabel(ImageIcon(ImageIO.read(File("./icons/height.png"))))
            heightLabel.setBounds(heightSlider.x + heightSlider.width, heightSlider.y, sliderHeight, sliderHeight)
            heightLabel.isVisible = true
            panel.add(heightLabel)
        } catch (ex: Exception) {
            ex.printStackTrace()
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
