package model

import java.io.Serializable

class Frame() : Serializable{
    var layers : ArrayList<Layer> = ArrayList()
    var curLayer : Int = -1

    constructor(origin : Frame) : this(){
        curLayer = origin.curLayer
        for (originLayer in origin.layers) {
            layers.add(Layer(originLayer))
        }
    }
}