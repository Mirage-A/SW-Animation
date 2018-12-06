package logic

import java.io.Serializable

abstract class Animation : Serializable {
    var frames : List<Frame> = ArrayList()
}