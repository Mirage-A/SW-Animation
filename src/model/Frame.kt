package model

import java.io.Serializable

/**
 * Кадр анимации
 */
class Frame() : Serializable{
    /**
     * Список слоёв на кадре
     */
    var layers : ArrayList<Layer> = ArrayList()
    /**
     * Текущий выбранный слой
     */
    var curLayer : Int = -1

    constructor(origin : Frame) : this(){
        curLayer = origin.curLayer
        for (originLayer in origin.layers) {
            layers.add(Layer(originLayer))
        }
    }

    /**
     * Отражает кадр (кнопка Mirror animation)
     */
    fun mirror() {
        for (mLayer in layers) {
            mLayer.mirror()
        }
        swapLayers("rightleg", "leftleg")
        swapLayers("rightlegtop", "leftlegtop")
        swapLayers("rightlegbottom", "leftlegbottom")
        swapLayers("righthandtop", "lefthandtop")
        swapLayers("righthandbottom", "lefthandbottom")
    }

    /**
     * Меняет местами слои с данными именами
     */
    private fun swapLayers(name1 : String, name2 : String) {
        var index1 = -1
        var index2 = -1
        for (index in layers.indices) {
            val layerName = layers[index].getName()
            if (layerName == name1) {
                index1 = index
            }
            if (layerName == name2) {
                index2 = index
            }
        }
        if (index1 != -1 && index2 != -1) {
            val tmp = layers[index1]
            layers[index1] = layers[index2]
            layers[index2] = tmp
        }
    }
}