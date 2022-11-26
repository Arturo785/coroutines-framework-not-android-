package flows

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(DelicateCoroutinesApi::class)
fun main() {
    //mainSharedFlow()
    //mainSharedFlow2()
   // mainCorrectEmission()
    mainStateFlow()
}

fun mainOperators() {
    val flowOfStrings = flow {
        emit("")

        for (number in 0..100) {
            emit("Emitting: $number")
        }
    }

    GlobalScope.launch {
        flowOfStrings
            .map { it.split(" ") }
            .map { it[1] }
            .catch {
                it.printStackTrace()
                // send the fallback value or values
                emit("Fallback")
            }
            .flowOn(Dispatchers.Default)
            .collect { println(it) }

        println("The code still works!")
    }
    Thread.sleep(1000)
}

fun mainBasic() {
    val flowOfStrings = flow {
        for (number in 0..100) {
            emit("Emitting: $number")
        }
    }

    GlobalScope.launch {
        flowOfStrings.collect { value ->
            println(value)
        }
    }

    Thread.sleep(1000)
}

fun mainSharedFlow() {
    // val sharedFlow = MutableSharedFlow<Int>().asSharedFlow()// this changes the behavior
    val sharedFlow = MutableSharedFlow<Int>(2)

    // 1
    sharedFlow.onEach {
        println("Emitting: $it")
    }.launchIn(GlobalScope) // 2

    sharedFlow.onEach {
        println("Hello: $it")
    }.launchIn(GlobalScope)

    // 3
    sharedFlow.tryEmit(5)
    sharedFlow.tryEmit(7)
    sharedFlow.tryEmit(3)

    // 4
    Thread.sleep(1000)
}

fun mainSharedFlow2() {
    // val sharedFlow = MutableSharedFlow<Int>().asSharedFlow()// this changes the behavior
    val sharedFlow = MutableSharedFlow<Int>(2)

    // 1
    sharedFlow.onEach {
        println("Emitting: $it")
    }.launchIn(GlobalScope) // 2

    sharedFlow.onEach {
        println("Hello: $it")
    }.launchIn(GlobalScope)

    GlobalScope.launch {
        // 3
        launch {
            sharedFlow.emit(5)
            sharedFlow.emit(7)
            sharedFlow.emit(3)
        }
    }

    // 4
    Thread.sleep(1000)
}

//
fun mainCorrectEmission() {
    // 1
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    val sharedFlow = MutableSharedFlow<Int>()

    sharedFlow.onEach {
        println("Emitting: $it")
    }.launchIn(coroutineScope)

    // 2 if using global scope it does not work I think because it shares the same instance as the original thread
    coroutineScope.launch {
        sharedFlow.emit(5)
        sharedFlow.emit(3)
        sharedFlow.emit(1)

        // 3
        coroutineScope.cancel()
    }

    // keeps the program alive
    // 4
    while (coroutineScope.isActive) {

    }
}

fun mainStateFlow() {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val stateFlow = MutableStateFlow("Author: Filip") // here

    println(stateFlow.value) // 1

    coroutineScope.launch {
        stateFlow.collect { // 2
            println(it)
        }
    }

    stateFlow.value = "Author: Luka" // 1

    stateFlow.tryEmit("FPE: Max") // 2

    coroutineScope.launch {
        stateFlow.emit("TE: Godfred") // 3
    }

    Thread.sleep(50)
    coroutineScope.cancel()

    while (coroutineScope.isActive) {

    }
}