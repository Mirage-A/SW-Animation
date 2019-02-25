package model

import java.io.File
import java.util.*

object Model {

    /**
     * Текущая анимация
     */
    var animation : Animation = Animation()

    /**
     * Файл с текущей анимацией
     */
    var animationFile : File? = null

    /**
     * Текущая директория с анимацией
     */
    var animationDirectory : File = File("./animations")

    var settingsFile: File = File("./settings.txt")

    init {
        if (settingsFile.exists()) {
            animationDirectory = File(Scanner(settingsFile).nextLine())
        }
    }


    /**
     * Сериализует текущую анимацию и сохраняет ее в файл
     */
    fun serialize() {
        if (animationFile != null) {
            if (!animationFile!!.parentFile.parentFile.exists()) {
                animationFile!!.parentFile.parentFile.mkdir()
            }
            if (!animationFile!!.parentFile.exists()) {
                animationFile!!.parentFile.mkdir()
            }
            if (!animationFile!!.exists()) {
                animationFile!!.createNewFile()
            }
            animation.serialize(animationFile!!)
        }
    }
}