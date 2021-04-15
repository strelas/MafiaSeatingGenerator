package entity

import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun generateJson(file: File) {
    val reader = FileReader(file)
    val text = reader.readLines()
    val players = text.map {
        val nick = it.split(". ").last()
        Player(nick, "", 1, arrayOf(), "")
    }
    reader.close()
    val writer = FileWriter(File("list.json"))
    writer.write(Gson().toJson(players))
    writer.close()
}