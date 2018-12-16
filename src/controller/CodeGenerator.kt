package controller

import model.*
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
import kotlin.collections.ArrayList

/**
 * Генератор файла с кодом, который потом вставляется в проект Shattered World - Client
 * для воспроизведения анимаций внутри игры
 */
class CodeGenerator {
    companion object {
        private val warnings = ArrayList<String>()
        fun generate() {
            warnings.clear()
            val bodyAnimationsFolder = File("./animations/BODY")
            val legsAnimationsFolder = File("./animations/LEGS")
            val bodyList = loadAllAnimations(bodyAnimationsFolder)
            val legsList = loadAllAnimations(legsAnimationsFolder)
            checkBodyAnimations(bodyList)
            checkLegsAnimations(legsList)
            val file = File("HumanoidDrawerTmp.kt") // TODO убрать Tmp
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
                    "    }\n" +
                    "\n" +
                    "    fun curValue(startPoint: Point, endPoint: Point, progress: Float) : Point {\n" +
                    "        return Point(curValue(startPoint.x, endPoint.x, progress), curValue(startPoint.y, endPoint.y, progress))\n" +
                    "    }\n" +
                    "\n")

            /**
             * Тут будет генерация методов отрисовки
             * Основной метод - draw, в нем when по типу анимации body, moveDirection-у и weaponType-у
             * Для каждой анимации:
             * 1) Вызываем метод getBodyPoint(), который через when по типу анимации legs, moveDirection-у и времени
             * возвращает координаты bodyPoint-а, т.е. точки, которая считается началом координат при отрисовке тела
             * (тело может двигаться по мере анимации legs)
             * 2) Делаем when по времени и отрисовываем слои:
             * а) Слой leftleg - вызываем drawLeftLeg(), также с тройным when-ом
             * по типу анимации legs, moveDirection-у и времени
             * б) Слой rightleg - вызываем drawRightLeg(), аналогично
             * в) Другой слой - просто отрисовываем (не забываем про смещение на bodyPoint)
             * Итого 4 метода: draw, getBodyPoint, drawLeftLeg, drawRightLeg
             */

            /**
             * Метод getBodyPoint
             */
            out.write("    fun getBodyPoint(legsTimePassedSinceStart: Long) : Point {\n")
            out.write(generateLegsWhens(legsList) {startFrame, endFrame ->
                "                        return curValue(Point(" + getBodyPoint(startFrame).first + "f, " + getBodyPoint(startFrame).second + "f), " +
                        "Point(" + getBodyPoint(endFrame).first + "f, " + getBodyPoint(endFrame).second + "f), progress)\n"
            })
            out.write("    }\n") // Конец getBodyPoint

            /**
             * Метод drawLeftLeg
             */
            out.write("    fun drawLeftLeg(batch: SpriteBatch, x: Float, y: Float, legsTimePassedSinceStart: Long) {\n")
            out.write(generateLegsWhens(legsList) {startFrame, endFrame ->
                generateLeftLegDraw(startFrame, endFrame)
            })
            out.write("    }\n") // Конец drawLeftLeg

            /**
             * Метод drawRightLeg
             */
            out.write("    fun drawRightLeg(batch: SpriteBatch, x: Float, y: Float, legsTimePassedSinceStart: Long) {\n")
            out.write(generateLegsWhens(legsList) {startFrame, endFrame ->
                generateRightLegDraw(startFrame, endFrame)
            })
            out.write("    }\n") // Конец drawRightLeg

            out.write("}\n") // Конец класса
            out.close()

            showWarnings()

            JOptionPane.showMessageDialog(null, "Code generation completed!\nGenerated file : " + file.absolutePath, "Shattered World Animation Code Generator", JOptionPane.INFORMATION_MESSAGE)

        }

        /**
         * Генерирует код отрисовки leftLeg
         */
        private fun generateLeftLegDraw(startFrame: Frame, endFrame: Frame) : String {
            val topStart = findLayer(startFrame, "leftlegtop")
            val bottomStart = findLayer(startFrame, "leftlegbottom")
            val topEnd = findLayer(endFrame, "leftlegtop")
            val bottomEnd = findLayer(endFrame, "leftlegbottom")
            return if ((topStart == null) or (bottomStart == null) or (topEnd == null) or (bottomEnd == null)) {
                "" //TODO обработать отсутствие необходимых слоев в анимации legs
            }
            else {
                if (startFrame.layers.indexOf(topStart) < startFrame.layers.indexOf(bottomStart)) {
                    "                        " + generateLayerDraw(topStart!!, topEnd!!) +
                            "                        " + generateLayerDraw(bottomStart!!, bottomEnd!!)
                }
                else {
                    "                        " + generateLayerDraw(bottomStart!!, bottomEnd!!) +
                            "                        " + generateLayerDraw(topStart!!, topEnd!!)
                }
            }
        }

        /**
         * Генерирует код отрисовки rightLeg
         */
        private fun generateRightLegDraw(startFrame: Frame, endFrame: Frame) : String {
            val topStart = findLayer(startFrame, "rightlegtop")
            val bottomStart = findLayer(startFrame, "rightlegbottom")
            val topEnd = findLayer(endFrame, "rightlegtop")
            val bottomEnd = findLayer(endFrame, "rightlegbottom")
            return if ((topStart == null) or (bottomStart == null) or (topEnd == null) or (bottomEnd == null)) {
                "" //TODO обработать отсутствие необходимых слоев в анимации legs
            }
            else {
                if (startFrame.layers.indexOf(topStart) < startFrame.layers.indexOf(bottomStart)) {
                    "                        " + generateLayerDraw(topStart!!, topEnd!!) +
                            "                        " + generateLayerDraw(bottomStart!!, bottomEnd!!)
                }
                else {
                    "                        " + generateLayerDraw(bottomStart!!, bottomEnd!!) +
                            "                        " + generateLayerDraw(topStart!!, topEnd!!)
                }
            }
        }

        /**
         * Находит слой в кадре по названию
         * @return Слой с данным названием (если он присутствует в кадре) или null иначе
         */
        private fun findLayer(frame : Frame, layerName: String) : Layer? {
            for (layer in frame.layers) {
                if (layer.layerName == layerName) {
                    return layer
                }
            }
            return null
        }

        /**
         * Генерирует строку кода, отрисовывающую переход слоя от startLayer к endLayer
         * Сгенерированный код использует переменные progress, x, y
         */
        private fun generateLayerDraw(startLayer: Layer, endLayer : Layer) : String {
            var layerName = startLayer.layerName
            if ((layerName == "leftleg") or (layerName == "rightleg") or (layerName == "bodypoint")) {
                return ""
            }
            layerName = when (true) {
                layerName.startsWith("head") -> layerName.substring(0, 5)
                (layerName == "leftlegtop") or (layerName == "rightlegtop") -> "legtop"
                (layerName == "leftlegbottom") or (layerName == "rightlegbottom") -> "legbottom"
                (layerName == "onehandedright") or (layerName == "twohanded") or (layerName == "bow") or
                        (layerName == "staff") -> "weapon1"
                (layerName == "onehandedleft") or (layerName == "shield") -> "weapon2"
                else -> layerName
            }
            return "batch.draw(textures[\"" + layerName +"\"]!!.getTexture(), " +
                    "x + curValue("+startLayer.x+"f, "+endLayer.x+"f, progress), " +
                    "y + curValue("+startLayer.y+"f, "+endLayer.y+"f, progress), " +
                    "curValue("+startLayer.basicWidth+"f, "+endLayer.basicWidth+"f, progress)/2, " +
                    "curValue("+startLayer.basicHeight+"f, "+endLayer.basicHeight+"f, progress)/2, " +
                    "curValue("+startLayer.basicWidth+"f, "+endLayer.basicWidth+"f, progress), " +
                    "curValue("+startLayer.basicHeight+"f, "+endLayer.basicHeight+"f, progress), " +
                    "curValue("+startLayer.scale * startLayer.scaleX+"f, "+endLayer.scale * endLayer.scaleX+"f, progress), " +
                    "curValue("+startLayer.scale * startLayer.scaleY+"f, "+endLayer.scale * endLayer.scaleY+"f, progress), " +
                    "curValue(" + startLayer.angle + "f, " + endLayer.angle + "f, progress), " +
                    "0, 0, " +
                    startLayer.basicWidth + ", " +
                    startLayer.basicHeight + ", false, false)\n"
        }

        /**
         * Генерирует строку кода, отрисовывающую переход слоя от startLayer к endLayer
         * Сгенерированный код использует переменные progress, bodyX, bodyY
         */
        private fun generateBodyLayerDraw(startLayer: Layer, endLayer : Layer) : String {
            var layerName = startLayer.layerName
            if ((layerName == "leftleg") or (layerName == "rightleg") or (layerName == "bodypoint")) {
                return ""
            }
            layerName = when (true) {
                layerName.startsWith("head") -> layerName.substring(0, 5)
                (layerName == "leftlegtop") or (layerName == "rightlegtop") -> "legtop"
                (layerName == "leftlegbottom") or (layerName == "rightlegbottom") -> "legbottom"
                (layerName == "onehandedright") or (layerName == "twohanded") or (layerName == "bow") or
                        (layerName == "staff") -> "weapon1"
                (layerName == "onehandedleft") or (layerName == "shield") -> "weapon2"
                else -> layerName
            }
            return "batch.draw(textures[\"" + layerName +"\"]!!.getTexture(), " +
                    "bodyX + curValue("+startLayer.x+"f, "+endLayer.x+"f, progress), " +
                    "bodyY + curValue("+startLayer.y+"f, "+endLayer.y+"f, progress), " +
                    "curValue("+startLayer.basicWidth+"f, "+endLayer.basicWidth+"f, progress)/2, " +
                    "curValue("+startLayer.basicHeight+"f, "+endLayer.basicHeight+"f, progress)/2, " +
                    "curValue("+startLayer.basicWidth+"f, "+endLayer.basicWidth+"f, progress), " +
                    "curValue("+startLayer.basicHeight+"f, "+endLayer.basicHeight+"f, progress), " +
                    "curValue("+startLayer.scale * startLayer.scaleX+"f, "+endLayer.scale * endLayer.scaleX+"f, progress), " +
                    "curValue("+startLayer.scale * startLayer.scaleY+"f, "+endLayer.scale * endLayer.scaleY+"f, progress), " +
                    "curValue(" + startLayer.angle + "f, " + endLayer.angle + "f, progress), " +
                    "0, 0, " +
                    startLayer.basicWidth + ", " +
                    startLayer.basicHeight + ", false, false)\n"
        }

        /**
         * Функция, которая генерирует when-ы по legsAction-у, moveDirection-у и timePassedSinceStart-у и внутри
         * самого вложенного when-а вставляет код, возвращаемый лямбдой.
         * @param legsAnimations Анимации legs, по которым делается первый when
         * @param getInnerCode Лямбда, которая по двум кадрам и прогрессу перехода между ними
         * возвращает строку кода, которую нужно вставить в самый вложенный when.
         * Код, возвращаемый лямбдой, может использовать переменную progress типа Float.
         * @return Сгенерированный код со всеми when-ами, который вставляется, например, в getBodyPoint.
         * ВАЖНО! Внутри сгенерированного кода используется переменная legsTimePassedSinceStart типа Long,
         * которая должна быть объявлена в сгенерированной функции
         */
        private fun generateLegsWhens(legsAnimations : Collection<Animation>, getInnerCode : (startFrame : Frame, endFrame : Frame) -> String) : String {
            val code = StringBuilder("")
            if (legsAnimations.isNotEmpty()) {
                code.append("        when (legsAction) {\n")
                for (legsAnim in legsAnimations) {
                    code.append("            LegsAction." + legsAnim.name + " -> {\n")
                    if (legsAnim.isRepeatable) {
                        code.append("                val timePassed = legsTimePassedSinceStart % " + legsAnim.duration + "L\n")
                    } else {
                        code.append("                val timePassed = Math.min(legsTimePassedSinceStart, " + legsAnim.duration + "L)\n")
                    }
                    code.append("                when (moveDirection) {\n")
                    for (moveDirection in MoveDirection.values()) {
                        val frames = legsAnim.data[moveDirection]!![WeaponType.ONE_HANDED]!!
                        code.append("                    MoveDirection." + moveDirection.toString() + " -> {\n")
                        val interval = legsAnim.duration.toFloat() / (frames.size - 1f)
                        if (frames.isNotEmpty()) {
                            if (frames.size == 1) {
                                code.append("                        val progress = 1f\n")
                                code.append(getInnerCode(frames[0], frames[0]))
                            }
                            else {
                                code.append("                        when (true) {\n")
                                for (i in 1 until frames.size) {
                                    code.append("                            timePassed < " + (interval * i + 1) + " -> {\n")
                                    code.append("                                val progress = (timePassed - " + (interval * i) + "f) / " + interval + "f")
                                    code.append(getInnerCode(frames[i - 1], frames[i]) + "\n")
                                    code.append("                            }\n") // Конец блока timePassed < TIME -> {}
                                }
                                code.append("                            else -> {\n")
                                code.append("                                val progress = 1f\n")
                                code.append(getInnerCode(frames[frames.size - 1], frames[frames.size - 1]))
                                code.append("                            }\n") // Конец блока else -> {}
                                code.append("                        }\n") // Конец when (true)
                            }
                        }
                        code.append("                    }\n") // Конец блока MoveDirection.name -> {}
                    }
                    code.append("                }\n") // Конец when (moveDirection)
                    code.append("            }\n") // Конец блока LegsAction.name -> {}
                }
                code.append("        }\n") // Конец when (legsAction)
            }
            return code.toString()
        }

        /**
         * Просматривает слои кадра и возвращает координаты центра слоя bodypoint
         * Если такого слоя нет, возвращает (0f, 0f)
         */
        private fun getBodyPoint(frame : Frame) : Pair<Float, Float> {
            for (layer in frame.layers) {
                if (layer.layerName == "bodypoint") {
                    return Pair(layer.x, layer.y)
                }
            }
            return Pair(0f, 0f)
        }

        /**
         * Загружает и десериализует все анимации из заданной директории и возвращает их список
         * Если какой-то файл не является корректной анимацией, добавляется warning
         */
        private fun loadAllAnimations(folder : File) : ArrayList<Animation> {
            val animationsList = ArrayList<Animation>()
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
        private fun checkBodyAnimations(animationsList : ArrayList<Animation>) {
            val isDeleted = Array(animationsList.size) {false}
            for (index in animationsList.indices) {
                val anim = animationsList[index]
                if (anim.type != AnimationType.BODY) {
                    warnings.add("Body animation " + anim.name + " is not correct body animation. Delete it or move it to the folder " + anim.type.toString() + ". Animation is ignored.")
                    isDeleted[index] = true
                }
            }
            var animationsDeleted = 0
            for (index in isDeleted.indices) {
                if (isDeleted[index]) {
                    animationsList.removeAt(index - animationsDeleted++)
                }
            }
        }

        /**
         * Проверяет все анимации из списка
         * Если анимация не является корректной анимацией legs, то она удаляется и добавляется warning
         */
        private fun checkLegsAnimations(animationsList : ArrayList<Animation>) {
            val isDeleted = Array(animationsList.size) {false}
            for (index in animationsList.indices) {
                val anim = animationsList[index]
                if (anim.type != AnimationType.LEGS) {
                    warnings.add("Legs animation " + anim.name + " is not correct legs animation. Delete it or move it to the folder " + anim.type.toString() + ". Animation is ignored.")
                    isDeleted[index] = true
                }
            }
            var animationsDeleted = 0
            for (index in isDeleted.indices) {
                if (isDeleted[index]) {
                    animationsList.removeAt(index - animationsDeleted++)
                }
            }
        }

        /**
         * Отображает окошко с warning-ами
         */
        private fun showWarnings() {
            if (warnings.isNotEmpty()) {
                val warningText = StringBuilder()
                for (warning in warnings) {
                    warningText.append("\n")
                    warningText.append(warning)
                }
                JOptionPane.showMessageDialog(null, warningText, "WARNINGS: " + warnings.size, JOptionPane.WARNING_MESSAGE)
            }
        }

        /**
         * Тестовая точка входа в программу
         */
        @JvmStatic
        fun main(args: Array<String>) {
            generate()
        }
    }
}
