package controller

import model.Animation
import model.AnimationType
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.ObjectInputStream
import java.lang.StringBuilder
import java.nio.file.Files
import java.util.*

import javax.swing.JOptionPane

/**
 * Генератор файла с кодом, который потом вставляется в проект Shattered World - Client
 * для воспроизведения анимаций внутри игры
 */
class CodeGenerator {
    companion object {
        fun generate() {
            val warnings = ArrayList<String>()
            val bodyAnimationsFolder = File("./animations/BODY")
            val legsAnimationsFolder = File("./animations/LEGS")
            val bodyList = loadAllAnimations(bodyAnimationsFolder, warnings)
            val legsList = loadAllAnimations(legsAnimationsFolder, warnings)
            checkBodyAnimations(bodyList, warnings)
            checkLegsAnimations(legsList, warnings)
            val file = File("./HumanoidDrawerTmp.kt") // TODO убрать Tmp
            if (!file.exists()) {
                file.createNewFile()
            }
            val out = FileWriter(file)

            out.write("package com.mirage.view.scene.objects.humanoid\n" +
                    "\n" +
                    "import com.mirage.view.TextureLoader\n" +
                    "import com.badlogic.gdx.graphics.g2d.SpriteBatch\n" +
                    "import com.mirage.view.scene.objects.AnimatedObjectDrawer\n" +
                    "import java.util.HashMap\n" +
                    "\n" +
                    "\n" +
                    "/**\n" +
                    " * Генерируемый с помощью Animation Editor-а класс для работы со скелетной анимацией гуманоидов\n" +
                    " * (анимация остальных существ задается покадрово классом SpriteAnimation)\n" +
                    " */\n" +
                    "class HumanoidDrawer : AnimatedObjectDrawer {\n" +
                    "    /**\n" +
                    "     * Действие, которое анимируется (ожидание, бег, атака и т.д.)\n" +
                    "     */\n" +
                    "    var action: Action = Action.IDLE\n" +
                    "    /**\n" +
                    "     * Направление движения\n" +
                    "     */\n" +
                    "    var moveDirection: MoveDirection = MoveDirection.RIGHT\n" +
                    "    /**\n" +
                    "     * Тип оружия гуманоида (одноручное, двуручное, два одноручных, одноручное и щит, лук и т.д.)\n" +
                    "     */\n" +
                    "    var weaponType: WeaponType = WeaponType.ONE_HANDED\n" +
                    "\n" +
                    "    /**\n" +
                    "     * Словарь из текстур экипировки данного гуманоида\n" +
                    "     * Должен содержать ключи head[0-7], body, handtop, handbottom, legtop, legbottom, cloak, weapon1, weapon2\n" +
                    "     */\n" +
                    "    var textures: MutableMap<String, AnimatedTexture>\n" +
                    "\n" +
                    "    constructor() {\n" +
                    "        textures = HashMap()\n" +
                    "        for (i in 0..7) {\n" +
                    "            textures[\"head\$i\"] = StaticTexture(TextureLoader.load(\"equipment/head/0000\$i.png\"))\n" +
                    "        }\n" +
                    "        textures[\"body\"] = StaticTexture(TextureLoader.load(\"equipment/body/0000.png\"))\n" +
                    "        textures[\"handtop\"] = StaticTexture(TextureLoader.load(\"equipment/handtop/0000.png\"))\n" +
                    "        textures[\"handbottom\"] = StaticTexture(TextureLoader.load(\"equipment/handbottom/0000.png\"))\n" +
                    "        textures[\"legtop\"] = StaticTexture(TextureLoader.load(\"equipment/legtop/0000.png\"))\n" +
                    "        textures[\"legbottom\"] = StaticTexture(TextureLoader.load(\"equipment/legbottom/0000.png\"))\n" +
                    "        textures[\"cloak\"] = StaticTexture(TextureLoader.load(\"equipment/cloak/0000.png\"))\n" +
                    "        textures[\"neck\"] = StaticTexture(TextureLoader.load(\"equipment/neck/0000.png\"))\n" +
                    "        textures[\"weapon1\"] = StaticTexture(TextureLoader.load(\"equipment/onehanded/0000.png\"))\n" +
                    "        textures[\"weapon2\"] = StaticTexture(TextureLoader.load(\"equipment/onehanded/0000.png\"))\n" +
                    "    }\n" +
                    "    constructor(textures: MutableMap<String, AnimatedTexture>) {\n" +
                    "        this.textures = textures\n" +
                    "    }\n" +
                    "\n" +
                    "    constructor(textures: MutableMap<String, AnimatedTexture>, action: Action, moveDirection: MoveDirection, weaponType: WeaponType) {\n" +
                    "        this.textures = textures\n" +
                    "        this.action = action\n" +
                    "        this.moveDirection = moveDirection\n" +
                    "        this.weaponType = weaponType\n" +
                    "    }\n" +
                    "\n" +
                    "\n" +
                    "    fun curValue(startValue: Float, endValue : Float, progress : Float) : Float {\n" +
                    "        return startValue + (endValue - startValue) * progress\n" +
                    "    }\n")



            out.write("}\n")
            out.close()

            if (warnings.isNotEmpty()) {
                val warningText = StringBuilder().append("WARNINGS: " + warnings.size)
                for (warning in warnings) {
                    warningText.append("\n")
                    warningText.append(warning)
                }
                JOptionPane.showMessageDialog(null, warningText)
            }

            val ss = StringSelection(String(Files.readAllBytes(file.toPath())))
            Toolkit.getDefaultToolkit().systemClipboard.setContents(ss, null)
            JOptionPane.showMessageDialog(null, "Code generation completed!\nThe code has been copied to clipboard.", "Shattered World Animation Code Generator", JOptionPane.INFORMATION_MESSAGE)


        }

        private fun isBodyPart(layerName: String): Boolean {
            return (layerName == "neck") or (layerName == "handtop") or (layerName == "handbottom") or (layerName == "cloak") or
                    layerName.startsWith("onehanded") or (layerName == "twohanded") or (layerName == "shield") or (layerName == "bow") or (layerName == "staff") or
                    layerName.startsWith("head") or layerName.startsWith("body")
        }

        /**
         * Загружает и десериализует все анимации из заданной директории и возвращает их список
         * Если какой-то файл не является корректной анимацией, добавляется warning
         */
        private fun loadAllAnimations(folder : File, warnings : ArrayList<String>) : LinkedList<Animation> {
            val animationsList = LinkedList<Animation>()
            if (folder.exists()) {
                val filesList = folder.listFiles()
                for (file in filesList) {
                    try {
                        val fis = FileInputStream(file)
                        val oin = ObjectInputStream(fis)
                        val obj = oin.readObject()
                        animationsList.add(obj as Animation)
                    }
                    catch(ex : Exception) {
                        warnings.add("File " + file.absolutePath + " is not correct animation. Delete it. Animation is ignored.")
                    }
                }
            }
            return animationsList
        }

        /**
         * Проверяет все анимации из списка
         * Если анимация не является корректной анимацией body, то она удаляется и добавляется warning
         */
        private fun checkBodyAnimations(animationsList : LinkedList<Animation>, warnings : ArrayList<String>) {
            for (anim in animationsList) {
                if (anim.type != AnimationType.BODY) {
                    warnings.add("Body animation " + anim.name + " is not correct body animation. Delete it or move it to the folder " + anim.type.toString() + ". Animation is ignored.")
                    animationsList.remove(anim)
                }
                else {
                    val frames = anim.frames
                    if (frames.isEmpty()) {
                        warnings.add("Body animation " + anim.name + " is empty.")
                    }
                    else {
                        val layers = frames[0].layers
                        var leftLegFound = false
                        var rightLegFound = false
                        for (layer in layers) {
                            if (layer.imageName == "leftleg.png") {
                                leftLegFound = true
                            }
                            else if (layer.imageName == "rightleg.png") {
                                rightLegFound = true
                            }
                        }
                        if (!leftLegFound) {
                            warnings.add("Body animation " + anim.name + " does not contain leftleg.png layer. Animation is ignored.")
                        }
                        if (!rightLegFound) {
                            warnings.add("Body animation " + anim.name + " does not contain rightleg.png layer. Animation is ignored.")
                        }
                        if (!leftLegFound || !rightLegFound) {
                            animationsList.remove(anim)
                        }
                    }
                }
            }
        }

        /**
         * Проверяет все анимации из списка
         * Если анимация не является корректной анимацией legs, то она удаляется и добавляется warning
         */
        private fun checkLegsAnimations(animationsList : LinkedList<Animation>, warnings : ArrayList<String>) {

        }
    }
}
