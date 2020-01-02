package controller

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import java.io.IOException

import javax.imageio.ImageIO
import javax.swing.*

/**
 * Дополнительное окно, позволяющее работать с размерами слоя
 */
object SlidersWindow : JFrame() {

    private val panel = JPanel().apply {
        layout = null
    }

    var flipCheckBox: JCheckBox = JCheckBox("Flip layer").apply {
        setSize(160, 24)
        isVisible = true
        addActionListener(flipCheckBoxListener)
        panel.add(this)
    }


    var visibleCheckBox: JCheckBox = JCheckBox("Visible").apply {
        setSize(160, 24)
        isVisible = true
        addActionListener(visibleCheckBoxListener)
        panel.add(this)
    }


    /**
     * Слайдер, отвечающий за размер слоя
     */
    internal val sizeSlider : JSlider = UIFactory.createSlider(panel, sizeSliderListener)

    val sizeButton: JButton = JButton("100").apply {
        setSize(60, 20)
        isVisible = true
        addActionListener(sizeButtonListener)
        panel.add(this)
    }

    val xButton: JButton = JButton("100").apply {
        setSize(60, 20)
        isVisible = true
        addActionListener(xButtonListener)
        panel.add(this)
    }

    val yButton: JButton = JButton("100").apply {
        setSize(60, 20)
        isVisible = true
        addActionListener(yButtonListener)
        panel.add(this)
    }

    /**
     * Слайдер, отвечающий за ширину слоя
     */
    internal val widthSlider : JSlider = UIFactory.createSlider(panel, widthSliderListener)
    /**
     * Слайдер, отвечающий за высоту слоя
     */
    internal val heightSlider : JSlider = UIFactory.createSlider(panel, heightSliderListener)

    private val sizeLabel : JLabel = UIFactory.createLabel("size", panel)

    private val widthLabel : JLabel = UIFactory.createLabel("width", panel)

    private val heightLabel : JLabel = UIFactory.createLabel("height", panel)


    init {
        iconImage = ImageIO.read(File("./art.png"))
        isUndecorated = true
        setSize(272, 132)
        title = "Layer size"
        isAlwaysOnTop = true
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        contentPane.add(panel)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                flipCheckBox.setLocation(0, 4)
                visibleCheckBox.setLocation(0, 4 + flipCheckBox.height)
                sizeSlider.setLocation(0, 4 + flipCheckBox.height + visibleCheckBox.height)
                sizeButton.setLocation(sizeSlider.x + sizeSlider.width + 4, sizeSlider.y)
                widthSlider.setLocation(0, 4 + sizeSlider.height + flipCheckBox.height + visibleCheckBox.height)
                xButton.setLocation(widthSlider.x + widthSlider.width + 4, widthSlider.y)
                heightSlider.setLocation(0, 4 + sizeSlider.height + widthSlider.height + flipCheckBox.height + visibleCheckBox.height)
                yButton.setLocation(heightSlider.x + heightSlider.width + 4, heightSlider.y)
                sizeLabel.setLocation(sizeSlider.x + sizeSlider.width, sizeSlider.y)
                widthLabel.setLocation(widthSlider.x + widthSlider.width, widthSlider.y)
                heightLabel.setLocation(heightSlider.x + heightSlider.width, heightSlider.y)
            }
        })
        isVisible = false

    }

    /**
     * Перегрузка метода setEnabled класса JFrame
     * Не изменяет активность самого окна, но изменяет активность всех элементов внутри окна
     */
    override fun setEnabled(b: Boolean) {
        flipCheckBox.isEnabled = b
        visibleCheckBox.isEnabled = b
        sizeSlider.isEnabled = b
        widthSlider.isEnabled = b
        heightSlider.isEnabled = b
    }
}
