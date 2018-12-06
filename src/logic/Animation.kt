package logic

import java.io.Serializable

abstract class Animation : Serializable {
    var frames : ArrayList<Frame> = ArrayList()
    var curFrame : Int = -1
    var name : String = "NO_NAME"
}