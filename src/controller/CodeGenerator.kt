package controller

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

import javax.swing.JOptionPane

class CodeGenerator {
    companion object {
        fun generate() {
            val bodyAnimationsFolder = File("./bodyanimations")
            val legsAnimationsFolder = File("./legsanimations")
            val file = File("./HumanoidDrawerTmp.kt")
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

            val ss = StringSelection(String(Files.readAllBytes(file.toPath())))
            Toolkit.getDefaultToolkit().systemClipboard.setContents(ss, null)
            JOptionPane.showMessageDialog(null, "Compilation completed!\nThe code has been copied to clipboard.", "Shattered World model.Animation controller.CodeGenerator", JOptionPane.INFORMATION_MESSAGE)


        }

        private fun isBodyPart(layerName: String): Boolean {
            return (layerName == "neck") or (layerName == "handtop") or (layerName == "handbottom") or (layerName == "cloak") or
                    layerName.startsWith("onehanded") or (layerName == "twohanded") or (layerName == "shield") or (layerName == "bow") or (layerName == "staff") or
                    layerName.startsWith("head") or layerName.startsWith("body")
        }
    }
}
