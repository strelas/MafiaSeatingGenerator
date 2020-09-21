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
}