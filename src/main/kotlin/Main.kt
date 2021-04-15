import com.google.gson.Gson
import entity.AllRoundsSeating
import entity.Player
import entity.generateJson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun main(args: Array<String>) {
    if(args.none { !it.startsWith("--") }) {
        print("cannot find path of json file")
        return
    }
    try {
        val fileReader = FileReader(File(args.filter { !it.startsWith("--") }[0]))
        val json = Gson().fromJson<Array<Player>>(fileReader.readText(), Array<Player>::class.java)
        val output = File("output.txt")
        output.createNewFile()
        val seating = AllRoundsSeating(11, json.toList().sortedBy { it.nick }, args.contains("--simple"))
        seating.generateCsvForMWT(File("forMWT.csv"))
        val writer = FileWriter(output)
        writer.write(seating.toString())
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}