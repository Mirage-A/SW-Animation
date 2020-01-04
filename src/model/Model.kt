package model

import java.io.File
import java.util.*
import javax.swing.JOptionPane

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

    var rootAnimationDirectory: File = File("./animations")

    var settingsFile: File = File("./settings.txt")

    init {
        if (settingsFile.exists()) {
            val path = Scanner(settingsFile).nextLine()
            animationDirectory = File(path)
            rootAnimationDirectory = File(path)
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


    /**
     * Создает отраженную копию всех кадров для соотвествующего moveDirection-а
     */
    fun mirrorAnimation() {
        if (animation.type != AnimationType.BODY && animation.type != AnimationType.LEGS) {
            JOptionPane.showMessageDialog(null, "Only humanoid animations can be mirrored")
            return
        }
        val mirroredMD = animation.curMoveDirection.mirrored()
        if (mirroredMD == animation.curMoveDirection) {
            JOptionPane.showMessageDialog(null, "This move direction can't be mirrored")
        }
        else {
            val option = JOptionPane.showOptionDialog(
                    null,
                    "Animation for $mirroredMD move direction will be OVERRIDDEN to mirrored animation for ${animation.curMoveDirection} move direction?",
                    "Generating mirrored animation",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    arrayOf("Generate for all weapon types", "Generate for ${animation.curWeaponType}", "Cancel"),
                    null
            )
            fun generateForWeaponType(weaponType: WeaponType) {
                val mirroredFrames = animation.data[mirroredMD]!![weaponType]!!
                mirroredFrames.clear()
                val curFrames = animation.data[animation.curMoveDirection]!![weaponType]!!
                for (frame in curFrames) {
                    mirroredFrames.add(Frame(frame))
                }
                for (mFrame in mirroredFrames) {
                    mFrame.mirror(animation.curMoveDirection)
                }
            }
            if (option == JOptionPane.YES_OPTION) {
                for (wt in WeaponType.values()) {
                    generateForWeaponType(wt)
                }
            }
            else if (option == JOptionPane.NO_OPTION) {
                generateForWeaponType(animation.curWeaponType)
            }

        }
    }
}