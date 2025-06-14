package ru.smak.copier.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.smak.copier.net.ConnectedClient
import ru.smak.net.ActionCompletionHandler
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import kotlin.coroutines.suspendCoroutine

class Server(
    port: Int = 5200
) {
    private val serverSocket = AsynchronousServerSocketChannel.open()
    private val serverScope = CoroutineScope(Dispatchers.IO)

    init {
        serverSocket.bind(InetSocketAddress(port))

        runBlocking {
            while (true) {
                val socket = suspendCoroutine {
                    serverSocket.accept(
                        null, ActionCompletionHandler(it)
                    )
                }
                ConnectedClient(socket)
            }
            serverSocket.close()
        }
    }
}