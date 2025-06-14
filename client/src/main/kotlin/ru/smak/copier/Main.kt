package ru.smak.copier

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.smak.copier.ui.Content

fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication){
        Content(modifier = Modifier.fillMaxSize())
    }
}