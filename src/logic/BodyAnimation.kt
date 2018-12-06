package logic

class BodyAnimation : Animation() {
    private var frames : MutableMap<MoveDirection, Map<WeaponType, Frame>> = HashMap()
}