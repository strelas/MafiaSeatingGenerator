package entity

import java.io.File
import java.io.FileWriter
import java.lang.Exception

class AllRoundsSeating constructor(
    private val numberOfRounds: Int,
    private val players: List<Player>,
    private val simpleAlgorithm: Boolean
) {
    private val rounds: List<List<TableSeating>>
        get() = crossed.rounds
    private var splittedPlayers: List<List<List<Player>>>
    private var crossed = Crossed(players)
    val rating: Pair<Int, Int>
        get() = crossed.ratingOfCrossing().first

    override fun toString(): String {
        var result = ""
        rounds.forEachIndexed { index, list ->
            list.forEachIndexed { index1, tableSeating ->
                result += "Игра ${index + 1}, стол ${tableSeating.referee}.\n"
                tableSeating.players.forEachIndexed { index3, player ->
                    result += "${index3 + 1}: $player\n"
                }
                result += "\n"
            }
        }
        for (i in rounds[0].indices) {
            for (j in rounds.indices) {
                result += "Игра ${j+1}, стол ${rounds[j][i].referee}.\n"
                rounds[j][i].players.forEachIndexed { k, player ->
                    result += "${k + 1}: $player\n"
                }
                result += "\n"
            }
        }
        result += "\n${playersString()}\n"
        result += "\n$crossed"
        return result
    }

    fun generateCsvForMWT(outputFile: File) {
        var result = ""
        outputFile.createNewFile()
        for (round in rounds) {
            for (i in 0..9) {
                for (j in round.indices) {
                    result += round[j].players[i].fiimNick
                    if (j != round.lastIndex) result += ";"
                }
                result += "\n"
            }
            result += "\n"
        }
        val writer = FileWriter(outputFile)
        writer.write(result)
        writer.close()
    }

    private fun playersString(): String {
        var result = "По игрокам:\n"
        for (player in players) {
            result += player.nick + "\n"
            for (i in rounds.indices) {
                val table = rounds[i].indexOfFirst { it.players.contains(player) }
                val place = rounds[i][table].players.indexOf(player)
                result += "Игра: ${i + 1} Стол:${table + 1} Место:${place + 1}\n"
            }
            result += "\n"
        }
        return result
    }

    init {
        val result = arrayListOf<ArrayList<Player>>()
        result.add(arrayListOf())
        result.add(arrayListOf())
        result.add(arrayListOf())
        players.forEach {
            result[it.skill - 1].add(it)
        }
        splittedPlayers = result.map { splitGroupByRegion(it) }
        for (i in 1..numberOfRounds) {
            addRound()
        }

        var flag: Boolean
        while (true) {
            flag = false
            for (i in 0 until numberOfRounds) {
                flag = flag || decreaseCrossedRating(i)
            }
            if (!flag) break
        }
    }

    private fun addRound() {
        shuffle()
        crossed.addRound(fixMeeting(distributePlayers()))
    }

    private fun decreaseCrossedRating(index: Int): Boolean {
        val oldRating = crossed.ratingOfCrossing()
        for (toReplace in if (simpleAlgorithm) oldRating.second else players) {

            for (playerI in if (simpleAlgorithm) players.indices else players.indexOf(toReplace) + 1..players.lastIndex) {
                val player = players[playerI]

                if (toReplace == player) continue
                if (toReplace.skill != player.skill) continue
                //if(toReplace.region!=player.region) continue
                val a = rounds[index].indexOfFirst { it.players.contains(toReplace) }
                val b = rounds[index].indexOfFirst { it.players.contains(player) }
                if (a == b) continue
                if (!(rounds[index][a].playerCanPlayThere(player, toReplace) && rounds[index][b].playerCanPlayThere(
                        toReplace,
                        player
                    ))
                ) continue
                crossed.swapPlayers(index, toReplace, player)
                val newRating = crossed.ratingOfCrossing()
                if (newRating.first.first < oldRating.first.first) {
                    println("newRating: ${newRating.first.first}.${newRating.first.second}")
                    return true
                }
                if (newRating.first.first == oldRating.first.first && newRating.first.second < oldRating.first.second) {
                    println("newRating: ${newRating.first.first}.${newRating.first.second}")
                    return true
                }
                crossed.swapPlayers(index, toReplace, player)

            }
        }
        return false
    }

    private fun distributePlayers(): List<TableSeating> {
        val result = arrayListOf<TableSeating>()
        val tables = Array(players.size / 10) { arrayListOf<Player>() }
        val shuffledNumbers = Array(players.size / 10) {
            it
        }.toList().shuffled()
        getInlinePlayers().forEachIndexed { index, player ->
            tables[shuffledNumbers[index % (players.size / 10)]].add(player)
        }
        tables.forEachIndexed { index, arrayList ->
            result.add(TableSeating("${index + 1}", arrayList.shuffled().toTypedArray()))
        }
        return result
    }

    private fun fixMeeting(round: List<TableSeating>): List<TableSeating> {
        val result = arrayListOf<TableSeating>()
        result.addAll(round)
        for (tableSeating in result) {
            while (!tableSeating.isMeetingCorrect()) {
                val toReplace = tableSeating.players.find { !tableSeating.playerCanPlayThere(it, it) }
                var flag = false
                for (t2 in result) {
                    for (p2 in t2.players) {
                        if (p2.skill != toReplace!!.skill) continue
                        if (t2 == tableSeating) continue
                        if (t2.playerCanPlayThere(toReplace, p2) && tableSeating.playerCanPlayThere(p2, toReplace)) {
                            tableSeating.players = tableSeating.players
                                .map { if (it == toReplace) p2 else it }
                                .toTypedArray()
                            t2.players = t2.players
                                .map { if (it == p2) toReplace else it }
                                .toTypedArray()
                            flag = true
                            break
                        }
                    }
                    if (flag) break
                }
            }
        }
        return result
    }


    private fun shuffle() {
        splittedPlayers = splittedPlayers.map {
            it
                .shuffled()
                .map { p ->
                    p.shuffled()
                }
        }
    }

    private fun getInlinePlayers(): List<Player> {
        val result = arrayListOf<Player>()
        splittedPlayers.forEach { it1 ->
            it1.forEach { it2 ->
                it2.forEach {
                    result.add(it)
                }
            }
        }
        return result
    }

    private fun splitGroupByRegion(players: List<Player>): List<List<Player>> {
        val sorted = players.sortedBy { it.region }
        val result = arrayListOf<ArrayList<Player>>()
        val last: String? = null
        for (i in sorted.indices) {
            if (sorted[i].region != last) {
                result.add(arrayListOf())
            }
            result.last().add(sorted[i])
        }
        return result
    }
}