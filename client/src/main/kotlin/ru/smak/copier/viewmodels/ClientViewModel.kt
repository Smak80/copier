package ru.smak.copier.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import ru.smak.copier.net.Client
import kotlin.random.Random

class ClientViewModel : ViewModel() {
    val points = mutableStateListOf<Pair<Offset?, Color>>()
    val mainColor = Color(
        Random.nextInt(256),
        Random.nextInt(256),
        Random.nextInt(256),
        255,
    )

    private val client = Client("localhost", 5200)

    private fun parse(message: String){
        val values = message.split(";")
        if (values.size == 3){
            val offset = if (values[0] == "null" || values[1] == "null") null else{
                val x = values[0].toFloatOrNull()
                val y = values[1].toFloatOrNull()
                if (x!= null && y != null )
                    Offset(x, y)
                else null
            }
            val color = Color(values[2].toULongOrNull() ?: 0UL)
            addOffset(offset, color)
        }
    }

    private fun send(offset: Offset?){
        client.sendMessage("${offset?.x};${offset?.y};${mainColor.value}")
    }

    private fun addOffset(offset: Offset?){
        points.add(offset to mainColor)
    }

    private fun addOffset(offset: Offset?, color: Color = mainColor){
        points.add(offset to color)
    }

    fun addPos(pos: Offset) {
        addOffset(pos)
    }

    fun appendPos(pos: Offset) {
        val last = points.lastOrNull()
        last?.first?.let { lst ->
            addOffset(Offset(lst.x + pos.x, lst.y + pos.y))
        }
    }

    fun clearPos(){
        addOffset(null)
    }
}