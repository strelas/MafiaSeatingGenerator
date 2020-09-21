package entity

data class Player constructor(val nick: String, val region: String, val skill: Int, val cannotMeet: Array<String>) {
    fun canPlayTogetherWith(another: Player): Boolean {
        return !(cannotMeet.map { it.toLowerCase() }.contains(another.nick.toLowerCase()) || another.cannotMeet.map { it.toLowerCase() }.contains(nick))
    }

    override fun toString(): String {
        return "$nick (skill: $skill, region: $region)"
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Player) return false
        return nick==other.nick
    }

    override fun hashCode(): Int {
        return nick.hashCode()
    }
}
