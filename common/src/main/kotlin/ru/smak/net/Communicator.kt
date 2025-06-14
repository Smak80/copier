package ru.smak.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import kotlin.coroutines.suspendCoroutine

class Communicator(
    private val socket: AsynchronousSocketChannel
) {
    var isRunning = false
        private set
    private var parse: ((String)->Unit)? = null
    private val communicatorScope = CoroutineScope(Dispatchers.IO)
    private val messageScope = CoroutineScope(Dispatchers.IO)

    private val messageFlow = MutableStateFlow("")

    private fun startMessageAccepting(){
        messageScope.launch {
            messageFlow.collect { message ->
                _sendMessage(message)
            }
        }
        communicatorScope.launch {
            while (isRunning){
                try {
                    var capacity = Int.SIZE_BYTES
                    repeat(2) {
                        val buf = ByteBuffer.allocate(capacity)
                        suspendCoroutine {
                            socket.read(buf, null, ActionCompletionHandler(it))
                        }
                        buf.flip()
                        if (it == 0) capacity = buf.getInt()
                        else {
                            val message = Charsets.UTF_8.decode(buf).toString()
                            parse?.invoke(message)
                        }
                        buf.clear()
                    }
                } catch (_: Throwable){
                    break
                }
            }
        }
    }

    fun sendMessage(message: String) = messageScope.launch{
        messageFlow.emit(message)
    }

    private suspend fun _sendMessage(message: String){
        val ba = message.toByteArray()
        val buf = ByteBuffer.allocate(ba.size + Int.SIZE_BYTES)
        buf.putInt(ba.size)
        buf.put(ba)
        buf.flip()
        suspendCoroutine {
            socket.write(buf, null, ActionCompletionHandler(it))
        }
        buf.clear()
    }

    fun start(parser: (String)->Unit){
        if (!socket.isOpen) throw Exception("Connection closed")
        parse = parser
        if (!isRunning){
            isRunning = true
            startMessageAccepting()
        }
    }

    fun stop(){
        isRunning = false
        socket.close()
    }

}