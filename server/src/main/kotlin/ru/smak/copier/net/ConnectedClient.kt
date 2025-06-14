package ru.smak.copier.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.smak.net.Communicator
import java.nio.channels.AsynchronousSocketChannel

class ConnectedClient(socket: AsynchronousSocketChannel) {

    private val communicator = Communicator(socket)
    private val clientScope = CoroutineScope(Dispatchers.IO)

    init{
        connectedClients.add(this)
        communicator.start { message -> parse(message) }
    }

    private fun parse(message: String){
        sendToAll(message, echo = false)
    }

    fun stop(){
        communicator.stop()
    }

    private fun sendToAll(message: String, echo: Boolean = false){
        connectedClients.forEach {
            if (echo || it != this) clientScope.launch {
                it.communicator.sendMessage(message)
            }
        }
    }

    companion object {
        private val connectedClients = mutableListOf<ConnectedClient>()
    }
}