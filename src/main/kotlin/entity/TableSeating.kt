package entity

data class TableSeating constructor(val referee: String, var players: Array<Player>){

    fun isMeetingCorrect(): Boolean {
        for(player in players) {
            if(!playerCanPlayThere(player, player)) return false
        }
        return true
    }

    fun playerCanPlayThere(player: Player, instead: Player): Boolean {
        if(player.cannotMeet.contains(referee)) return false
        for(p in players) {
            if(p == instead) continue
            if(!player.canPlayTogetherWith(p)) {
                return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableSeating

        if (referee != other.referee) return false
        if (!players.contentEquals(other.players)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = referee.hashCode()
        result = 31 * result + players.contentHashCode()
        return result
    }
}