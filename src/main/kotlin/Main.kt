import com.google.gson.Gson
import entity.AllRoundsSeating
import entity.Player
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        print("cannot find path of json file")
        return
    }
    try {
        val fileReader = FileReader(File(args[0]))
        val json = Gson().fromJson<Array<Player>>(fileReader.readText(), Array<Player>::class.java)
        val output = File("output.txt")
        output.createNewFile()
        val seating = AllRoundsSeating(10, json.toList().sortedBy { it.nick })
        val writer = FileWriter(output)
        writer.write(seating.toString())
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}