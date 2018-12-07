import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.Scanner

import javax.swing.JOptionPane

object Compiler {

    @JvmStatic
    fun main(args: Array<String>) {
        val animationsFolder = File("./animations")
        val names = animationsFolder.list()
        val file = File("./Animations.java")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        val lineSeparator = System.getProperty("line.separator")
        try {
            val out = FileWriter(file)
            out.write("package com.pasan.shatteredworld;" + lineSeparator +
                    "import android.graphics.Bitmap;" + lineSeparator +
                    "import android.graphics.Canvas;" + lineSeparator +
                    "import android.graphics.Matrix;" + lineSeparator +
                    "import android.graphics.Point;" + lineSeparator + lineSeparator +
                    "public class Animations {" + lineSeparator)

            out.write("public static final int WEAPON_ONEHANDED=0;" + lineSeparator +
                    "public static final int WEAPON_SWORD_AND_SHIELD=1;" + lineSeparator +
                    "public static final int WEAPON_DUAL=2;" + lineSeparator +
                    "public static final int WEAPON_TWOHANDED=3;" + lineSeparator +
                    "public static final int WEAPON_BOW=4;" + lineSeparator +
                    "public static final int WEAPON_STAFF=5;" + lineSeparator)

            for (i in names!!.indices) {
                val scan = Scanner(names[i])
                scan.nextInt()
                val src = scan.next()
                var s = ""
                for (j in 0 until src.length) {
                    if (Character.isLowerCase(src[j])) {
                        s += Character.toUpperCase(src[j])
                    } else if (j != 0) {
                        s += "_" + src[j]
                    } else {
                        s += src[j]
                    }
                }
                out.write("public static final int $s=$i;$lineSeparator")
            }

            out.write("public static void drawFrame(Canvas canvas, int drawX, int drawY, int animationID, byte moveDirection, int weaponType, int frameID, int moveFrameID, Bitmap head, Bitmap neck, Bitmap body, Bitmap handtop, Bitmap handbottom, Bitmap legtop, Bitmap legbottom, Bitmap cloak, Bitmap weapon1, Bitmap weapon2){$lineSeparator")
            out.write("switch(animationID){$lineSeparator")
            for (animationID in 0 until animationsFolder.list()!!.size) {
                out.write("case $animationID:{$lineSeparator")
                out.write("switch(moveDirection){$lineSeparator")
                val moveDirectionsFolder = findFile(animationsFolder, animationID)
                for (moveDirection in 0 until moveDirectionsFolder!!.list()!!.size) {
                    out.write("case $moveDirection:{$lineSeparator")
                    val weaponsFolder = findFile(moveDirectionsFolder!!, moveDirection)
                    out.write("switch(weaponType){$lineSeparator")
                    for (weaponType in 0 until weaponsFolder!!.list()!!.size) {
                        out.write("case $weaponType:{$lineSeparator")
                        val framesFolder = findFile(weaponsFolder!!, weaponType)
                        val frames = framesFolder!!.listFiles()
                        out.write("switch(frameID){$lineSeparator")
                        for (curFrame in frames!!.indices) {
                            out.write("case $curFrame:{$lineSeparator")

                            val frame = File(framesFolder.absolutePath + "/frame" + curFrame + ".swanim")
                            val `in` = Scanner(frame)
                            val layersKol = `in`.nextInt()
                            var drawLegs = false
                            if (layersKol > 0) {
                                out.write("Matrix m;$lineSeparator")
                                out.write("Bitmap bitmap;$lineSeparator")
                                val s = String(Files.readAllBytes(frame.toPath()))
                                if (s.contains("leftleg")) {
                                    drawLegs = true
                                    out.write("Point p = getStartingPoint(moveDirection,moveFrameID);")
                                } else {
                                    drawLegs = false
                                    out.write("Point p = new Point(0,0);")
                                }
                            }

                            for (curLayer in 0 until layersKol) {
                                var imageName = `in`.next()
                                imageName = imageName.substring(0, imageName.length - 4)
                                val x = `in`.nextInt()
                                val y = `in`.nextInt()
                                val width = `in`.nextInt()
                                val height = `in`.nextInt()
                                `in`.nextInt()//Пропускаем увеличение размера
                                val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()


                                if (isBodyPart(imageName)) {
                                    if (imageName.startsWith("head")) {
                                        imageName = "head"
                                    } else if (imageName.startsWith("body")) {
                                        imageName = "body"
                                    } else if ((imageName == "onehandedright") or (imageName == "twohanded") or (imageName == "bow") or (imageName == "staff")) {
                                        imageName = "weapon1"
                                    } else if ((imageName == "onehandedleft") or (imageName == "shield")) {
                                        imageName = "weapon2"
                                    }
                                    out.write("m = new Matrix();$lineSeparator")
                                    out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                                    out.write("m.setTranslate(drawX+(" + (x - width / 2) + "+p.x)*GameConsts.scrH/960,drawY+(" + (y - height / 2) + "+p.y)*GameConsts.scrH/960);" + lineSeparator)
                                    out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                                    out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                                } else if (imageName.startsWith("leftleg")) {
                                    out.write("drawLeftLeg(canvas,drawX,drawY,moveDirection,moveFrameID,legtop,legbottom);")
                                } else if (imageName.startsWith("rightleg")) {
                                    out.write("drawRightLeg(canvas,drawX,drawY,moveDirection,moveFrameID,legtop,legbottom);")
                                } else {
                                    out.write("m = new Matrix();$lineSeparator")
                                    out.write("bitmap = Bitmap.createScaledBitmap(BitmapLoader.getBitmap($imageName),$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                                    out.write("m.setTranslate(drawX+(" + (x - width / 2) + "+p.x)*GameConsts.scrH/960,drawY+(" + (y - height / 2) + "+p.y)*GameConsts.scrH/960);" + lineSeparator)
                                    out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                                    out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                                }
                            }
                            `in`.close()


                            out.write("break;$lineSeparator}$lineSeparator")
                        }
                        out.write("}$lineSeparator")
                        out.write("break;$lineSeparator}$lineSeparator")
                    }
                    out.write("}$lineSeparator")
                    out.write("break;$lineSeparator}$lineSeparator")
                }
                out.write("}$lineSeparator")
                out.write("break;$lineSeparator}$lineSeparator")
            }
            out.write("}$lineSeparator")//Закрываем switch(animationID)
            out.write("}$lineSeparator")//Закрываем метод


            val moveAnimationsFolder = File("./animationsbottommove")
            val moveAnimationsFolder1 = File("./animationsbottomnomove")

            out.write("public static void drawLeftLeg(Canvas canvas, int drawX, int drawY, byte moveDirection, int moveFrameID, Bitmap legtop, Bitmap legbottom){$lineSeparator")
            out.write("switch(moveDirection){$lineSeparator")
            for (moveDirection in 0..7) {
                out.write("case $moveDirection:{$lineSeparator")
                out.write("switch(moveFrameID){$lineSeparator")
                val framesFolder = findFile(moveAnimationsFolder, moveDirection)
                val framesFolder1 = findFile(moveAnimationsFolder1, moveDirection)
                val frame1 = framesFolder1!!.listFiles()!![0]
                out.write("case -1:{$lineSeparator")
                var `in` = Scanner(frame1)
                var layersKol = `in`.nextInt()
                if (layersKol > 0) {
                    out.write("Matrix m;$lineSeparator")
                    out.write("Bitmap bitmap;$lineSeparator")
                }
                for (curLayer in 0 until layersKol) {
                    var imageName = `in`.next()
                    imageName = imageName.substring(0, imageName.length - 4)
                    val x = `in`.nextInt()
                    val y = `in`.nextInt()
                    val width = `in`.nextInt()
                    val height = `in`.nextInt()
                    `in`.nextInt()//Пропускаем увеличение размера
                    val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()

                    if (imageName.startsWith("leftlegtop")) {
                        imageName = "legtop"
                        out.write("m = new Matrix();$lineSeparator")
                        out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                        out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                    } else if (imageName.startsWith("leftlegbottom")) {
                        imageName = "legbottom"
                        out.write("m = new Matrix();$lineSeparator")
                        out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                        out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                    }
                }
                `in`.close()
                out.write("break;$lineSeparator}$lineSeparator")


                val frames = framesFolder!!.listFiles()
                for (curFrame in frames!!.indices) {
                    out.write("case $curFrame:{$lineSeparator")
                    val frame = File(framesFolder.absolutePath + "/frame" + curFrame + ".swanim")
                    `in` = Scanner(frame)
                    layersKol = `in`.nextInt()
                    if (layersKol > 0) {
                        out.write("Matrix m;$lineSeparator")
                        out.write("Bitmap bitmap;$lineSeparator")
                    }
                    for (curLayer in 0 until layersKol) {
                        var imageName = `in`.next()
                        imageName = imageName.substring(0, imageName.length - 4)
                        val x = `in`.nextInt()
                        val y = `in`.nextInt()
                        val width = `in`.nextInt()
                        val height = `in`.nextInt()
                        `in`.nextInt()//Пропускаем увеличение размера
                        val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()

                        if (imageName.startsWith("leftlegtop")) {
                            imageName = "legtop"
                            out.write("m = new Matrix();$lineSeparator")
                            out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                            out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                        } else if (imageName.startsWith("leftlegbottom")) {
                            imageName = "legbottom"
                            out.write("m = new Matrix();$lineSeparator")
                            out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                            out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                        }
                    }
                    `in`.close()
                    out.write("break;$lineSeparator}$lineSeparator")
                }
                out.write("}" + lineSeparator + "break;" + lineSeparator + "}" + lineSeparator)
            }
            out.write("}")
            out.write("}")//Закрываем метод


            out.write("public static void drawRightLeg(Canvas canvas, int drawX, int drawY, byte moveDirection, int moveFrameID, Bitmap legtop, Bitmap legbottom){$lineSeparator")
            out.write("switch(moveDirection){$lineSeparator")
            for (moveDirection in 0..7) {
                out.write("case $moveDirection:{$lineSeparator")
                out.write("switch(moveFrameID){$lineSeparator")
                val framesFolder = findFile(moveAnimationsFolder, moveDirection)
                val framesFolder1 = findFile(moveAnimationsFolder1, moveDirection)

                val frame1 = framesFolder1!!.listFiles()!![0]
                out.write("case -1:{$lineSeparator")
                var `in` = Scanner(frame1)
                var layersKol = `in`.nextInt()
                if (layersKol > 0) {
                    out.write("Matrix m;$lineSeparator")
                    out.write("Bitmap bitmap;$lineSeparator")
                }
                for (curLayer in 0 until layersKol) {
                    var imageName = `in`.next()
                    imageName = imageName.substring(0, imageName.length - 4)
                    val x = `in`.nextInt()
                    val y = `in`.nextInt()
                    val width = `in`.nextInt()
                    val height = `in`.nextInt()
                    `in`.nextInt()//Пропускаем увеличение размера
                    val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()

                    if (imageName.startsWith("rightlegtop")) {
                        imageName = "legtop"
                        out.write("m = new Matrix();$lineSeparator")
                        out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                        out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                    } else if (imageName.startsWith("rightlegbottom")) {
                        imageName = "legbottom"
                        out.write("m = new Matrix();$lineSeparator")
                        out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                        out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                        out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                    }
                }
                `in`.close()
                out.write("break;$lineSeparator}$lineSeparator")


                val frames = framesFolder!!.listFiles()
                for (curFrame in frames!!.indices) {
                    out.write("case $curFrame:{$lineSeparator")
                    val frame = File(framesFolder.absolutePath + "/frame" + curFrame + ".swanim")
                    `in` = Scanner(frame)
                    layersKol = `in`.nextInt()
                    if (layersKol > 0) {
                        out.write("Matrix m;$lineSeparator")
                        out.write("Bitmap bitmap;$lineSeparator")
                    }
                    for (curLayer in 0 until layersKol) {
                        var imageName = `in`.next()
                        imageName = imageName.substring(0, imageName.length - 4)
                        val x = `in`.nextInt()
                        val y = `in`.nextInt()
                        val width = `in`.nextInt()
                        val height = `in`.nextInt()
                        `in`.nextInt()//Пропускаем увеличение размера
                        val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()

                        if (imageName.startsWith("rightlegtop")) {
                            imageName = "legtop"
                            out.write("m = new Matrix();$lineSeparator")
                            out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                            out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                        } else if (imageName.startsWith("rightlegbottom")) {
                            imageName = "legbottom"
                            out.write("m = new Matrix();$lineSeparator")
                            out.write("bitmap = Bitmap.createScaledBitmap($imageName,$width*GameConsts.scrH/960,$height*GameConsts.scrH/960,false);$lineSeparator")
                            out.write("m.setTranslate(drawX+" + (x - width / 2) + "*GameConsts.scrH/960,drawY+" + (y - height / 2) + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("m.preRotate(" + -angle + "f," + width / 2 + "*GameConsts.scrH/960," + height / 2 + "*GameConsts.scrH/960);" + lineSeparator)
                            out.write("canvas.drawBitmap(bitmap,m,null);$lineSeparator")
                        }
                    }
                    `in`.close()
                    out.write("break;$lineSeparator}$lineSeparator")
                }
                out.write("}" + lineSeparator + "break;" + lineSeparator + "}" + lineSeparator)
            }
            out.write("}")
            out.write("}")//Закрываем метод


            out.write("public static Point getStartingPoint(byte moveDirection, int moveFrameID){$lineSeparator")
            out.write("switch(moveDirection){$lineSeparator")
            for (moveDirection in 0..7) {
                out.write("case $moveDirection:{$lineSeparator")
                out.write("switch(moveFrameID){$lineSeparator")
                val framesFolder = findFile(moveAnimationsFolder, moveDirection)
                val framesFolder1 = findFile(moveAnimationsFolder1, moveDirection)
                val frame1 = framesFolder!!.listFiles()!![0]
                out.write("case -1:{$lineSeparator")
                var `in` = Scanner(frame1)
                var layersKol = `in`.nextInt()
                for (curLayer in 0 until layersKol) {
                    var imageName = `in`.next()
                    imageName = imageName.substring(0, imageName.length - 4)
                    val x = `in`.nextInt()
                    val y = `in`.nextInt()
                    val width = `in`.nextInt()
                    val height = `in`.nextInt()
                    `in`.nextInt()//Пропускаем увеличение размера
                    val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()

                    if (imageName.startsWith("bodypoint")) {
                        out.write("return new Point($x,$y);$lineSeparator")
                    }
                }
                `in`.close()
                out.write("}$lineSeparator")


                val frames = framesFolder.listFiles()
                for (curFrame in frames!!.indices) {
                    out.write("case $curFrame:{$lineSeparator")
                    val frame = File(framesFolder.absolutePath + "/frame" + curFrame + ".swanim")
                    `in` = Scanner(frame)
                    layersKol = `in`.nextInt()
                    for (curLayer in 0 until layersKol) {
                        var imageName = `in`.next()
                        imageName = imageName.substring(0, imageName.length - 4)
                        val x = `in`.nextInt()
                        val y = `in`.nextInt()
                        val width = `in`.nextInt()
                        val height = `in`.nextInt()
                        `in`.nextInt()//Пропускаем увеличение размера
                        val angle = Math.toDegrees(`in`.nextInt().toDouble() / 1000000).toFloat()

                        if (imageName.startsWith("bodypoint")) {
                            out.write("return new Point($x,$y);$lineSeparator")
                        }
                    }
                    `in`.close()
                    out.write("}$lineSeparator")
                }
                out.write("}$lineSeparator}$lineSeparator")
            }
            out.write("}$lineSeparator")
            out.write("return new Point(0,0);$lineSeparator")
            out.write("}$lineSeparator")//Закрываем метод


            out.write("}")//Закрываем класс
            out.close()

            val ss = StringSelection(String(Files.readAllBytes(file.toPath())))
            Toolkit.getDefaultToolkit().systemClipboard.setContents(ss, null)
            JOptionPane.showMessageDialog(null, "Compilation completed!\nThe code has been copied to clipboard.", "Shattered World logic.Animation Compiler", JOptionPane.INFORMATION_MESSAGE)


        } catch (ex: Exception) {
            ex.printStackTrace()
            JOptionPane.showMessageDialog(null, "ERROR", "ERROR", JOptionPane.ERROR_MESSAGE)
        }

    }

    private fun isBodyPart(layerName: String): Boolean {
        return (layerName == "neck") or (layerName == "handtop") or (layerName == "handbottom") or (layerName == "cloak") or
                layerName.startsWith("onehanded") or (layerName == "twohanded") or (layerName == "shield") or (layerName == "bow") or (layerName == "staff") or
                layerName.startsWith("head") or layerName.startsWith("body")
    }

    private fun findFile(folder: File, index: Int): File? {
        val files = folder.listFiles()
        for (i in files!!.indices) {
            if (files[i].name.startsWith(index.toString() + " ")) {
                return files[i]
            }
        }
        println(index.toString() + " " + folder.absolutePath)
        return null
    }

}
