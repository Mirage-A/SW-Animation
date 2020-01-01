package model


/**
 * Кадр анимации
 */
class Frame() {
    /**
     * Список слоёв на кадре
     */
    var layers : ArrayList<Layer> = ArrayList()
    /**
     * Текущий выбранный слой
     */
    @Transient var curLayer : Int = -1

    constructor(origin : Frame) : this(){
        curLayer = origin.curLayer
        for (originLayer in origin.layers) {
            layers.add(Layer(originLayer))
        }
    }

    /**
     * Отражает кадр (кнопка Mirror animation)
     */
    fun mirror(md: MoveDirection) {
        for (mLayer in layers) {
            mLayer.mirror(md)
        }
        if (md == MoveDirection.UP || md == MoveDirection.DOWN) layers.reverse()
    }

    /**
     * Меняет местами слои с данными именами.
     * Можно передать одинаковые имена, тогда поменяются местами 2 первых слоя с таким именем.
     */
    private fun swapLayers(name1 : String, name2 : String) {
        var index1 = -1
        var index2 = -1
        for (index in layers.indices) {
            val layerName = layers[index].imageName
            if (layerName == name1) {
                index1 = index
            }
            else if (layerName == name2) {
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