package logic

class Frame() {
    var layers : ArrayList<Layer> = ArrayList()
    var curLayer : Int = -1

    constructor(origin : Frame) : this(){
        curLayer = origin.curLayer
        for (originLayer in origin.layers) {
            layers.add(Layer(originLayer))
        }
    }
}