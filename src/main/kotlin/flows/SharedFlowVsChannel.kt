package flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

fun main() {
    val _sharedFlow = MutableSharedFlow<Int>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    val channel = Channel<Int>()
    val flowFromChannel = channel.receiveAsFlow()

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    coroutineScope.launch{
        delay(2000)
        sharedFlow.collect{
            println("$it from sharedFlow")
        }
    }

    coroutineScope.launch {
        delay(2000)
        flowFromChannel.collect{
            println("$it from channel")
        }
    }

    coroutineScope.launch {
        repeat(1000){
            _sharedFlow.emit(it)
            delay(1000)
        }
    }

    coroutineScope.launch {
        repeat(1000){
            channel.send(it)
            delay(1000)
        }
    }
    Thread.sleep(10000)
}