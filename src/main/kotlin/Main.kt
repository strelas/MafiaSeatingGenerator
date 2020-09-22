import com.google.gson.Gson
import entity.AllRoundsSeating
import entity.Player
import java.io.File
import java.io.FileReader

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        print("cannot find path of json file")
        return
    }
    try {
        val fileReader = FileReader(File(args[0]))
        val json = Gson().fromJson<Array<Player>>(fileReader.readText(), Array<Player>::class.java)
        println(AllRoundsSeating(11, json.toList().sortedBy { it.nick }))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}