package entity

import java.lang.Integer.min
import java.lang.NullPointerException
import kotlin.math.max

class Crossed constructor(private val players: List<Player>){
    var rounds = arrayListOf<List<TableSeating>>()
    override fun toString(): String {
        var result = "Статистика пересечений:\n"
        for (player in players) {
            result+=player.nick + "\n"
            for (another in players) {
                if(another==player) continue
                val cross = getCross(player, another)
                result+="${another.nick} = ${getCross(player, another)};\n"
            }
            result += "\n"
        }
        return  result
    }

    var crossedArray = Array(players.size) {
        Array(players.size) {
            0
        }
    }

    fun addRound(round: List<TableSeating>) {
        rounds.add(round)
        round.forEach {
            for (i in 0..8) {
                for (j in (i+1)..9) {
                    val a = players.indexOf(it.players[i])
                    val b = players.indexOf(it.players[j])
                    crossedArray[a][b]++
                    crossedArray[b][a]++
                }
            }
        }
    }

    fun changeRound(index: Int, p1: Player, p2: Player) {
        val t1 = rounds[index].find { it.players.contains(p1) }
        val t2 = rounds[index].find { it.players.contains(p2) }
        if(t1==null) {
            println(rounds[index])
            println(p1)
            throw NullPointerException()
        }
        if(t2 == null) {
            println(rounds[index])
            println(p2)
            throw NullPointerException()
        }

        t1.players.forEach {
            if(it!=p1) {
                crossedArray[players.indexOf(p1)][players.indexOf(it)]--
                crossedArray[players.indexOf(it)][players.indexOf(p1)]--
                crossedArray[players.indexOf(p2)][players.indexOf(it)]++
                crossedArray[players.indexOf(it)][players.indexOf(p2)]++
            }
        }
        t2.players.forEach {
            if(it!=p2) {
                crossedArray[players.indexOf(p1)][players.indexOf(it)]++
                crossedArray[players.indexOf(it)][players.indexOf(p1)]++
                crossedArray[players.indexOf(p2)][players.indexOf(it)]--
                crossedArray[players.indexOf(it)][players.indexOf(p2)]--
            }
        }
        rounds[index] = rounds[index].map{
            TableSeating(it.referee, it.players.map { p ->
                if(p==p2) p1
                else if(p==p1) p2
                else p
            }.toTypedArray())
        }
    }

    fun ratingOfCrossing(): Pair<Pair<Int, Int>, List<Player>> {
        var result = 0
        var count = 0
        val list = arrayListOf<Player>()
        for (player in players) {
            var multiply = 1
            var max = 0
            var min = Int.MAX_VALUE
            for (anotherPlayer in players) {
                if(anotherPlayer == player) continue
                if(player.canPlayTogetherWith(anotherPlayer)) {
                    val a = players.indexOf(player)
                    val b = players.indexOf(anotherPlayer)
                    if(min == 0 && crossedArray[a][b]==0) multiply ++
                    max = max(max, crossedArray[a][b])
                    min = min(min, crossedArray[a][b])
                }
            }
            if(min == 0) min-=4*multiply
            if(result < max-min) {
                result = max-min
                count = 1
                list.clear()
                list.add(player)
            }
            else if(result == max-min) {
                count++
                list.add(player)
            }
        }
        return Pair(Pair(result, count), list)
    }

    fun getCross(player1: Player, player2: Player): Int {
        val a = players.indexOf(player1)
        val b = players.indexOf(player2)
        return crossedArray[a][b]
    }


}