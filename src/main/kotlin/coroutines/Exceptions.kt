import kotlinx.coroutines.*

// 1


fun main(){
    //mainSupervisorJob()
    mainSupervisorScope()
}

@OptIn(DelicateCoroutinesApi::class)
fun maintest() = runBlocking {

    //mainNotHandled()

    // Global Exception Handler
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception with suppressed${exception.suppressed?.contentToString()}")
    }

    // Parent Job
    val parentJob = GlobalScope.launch(handler) {
        // Child Job 1
        launch {
            try {
                delay(Long.MAX_VALUE)
            } catch (e: Exception) {
                println("${e.javaClass.simpleName} in Child Job 1")
            } finally {
                throw ArithmeticException()
            }
        }

        launch {
            delay(500)
            println("Finished")
        }

        launch {
            try {
                delay(Long.MAX_VALUE)
            } catch (e: Exception) {
                println("${e.javaClass.simpleName} in Child Job 2")
            } finally {
                throw RuntimeException()
            }
        }

        // Child Job 2
        launch {
            delay(100)
            throw IllegalStateException()
        }

        // Delaying the parentJob
        delay(Long.MAX_VALUE)
    }
    // Wait until parentJob completes
    parentJob.join()
}


fun mainSupervisorJob() = runBlocking {

    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception with suppressed${exception.suppressed?.contentToString()}")
    }
    // 1
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor + handler)) {
        // 2
        val firstChild = launch {
            println("First child throwing an exception")
            throw ArithmeticException()
        }
        // 3
        val secondChild = launch {
            println("First child is cancelled: ${firstChild.isCancelled}")
            try {
                delay(5000)
            } catch (e: CancellationException) {
                println("Second child cancelled because supervisor got cancelled.")
            }
        }
        // 4
        firstChild.join()
        println("Second child is active: ${secondChild.isActive}")
        supervisor.cancel()
        secondChild.join()
    }
}

fun mainSupervisorScope() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception with suppressed${exception.suppressed?.contentToString()}")
    }
    // 1
    supervisorScope() {
        // 2
        val firstChild = launch {
            println("First child throwing an exception")
            throw ArithmeticException()
        }
        // 3
        val secondChild = launch {
            println("First child is cancelled: ${firstChild.isCancelled}")
            try {
                delay(5000)
            } catch (e: CancellationException) {
                println("Second child cancelled because supervisorScope got cancelled.")
            }
        }
        // 4
        firstChild.join()
        println("Second child is active: ${secondChild.isActive}")
        this.cancel()
        secondChild.join()
    }
}


fun mainNotHandled() = runBlocking {

    try {
        launch{
            throw Exception()
        }
    } catch (e: Exception) {
        println("Caught $e")
    }

    supervisorScope {

    }
}


@OptIn(DelicateCoroutinesApi::class)
fun mainHandler() {
    runBlocking {
        // 1
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        // 2
        val job = GlobalScope.launch(exceptionHandler) {
            throw AssertionError("My Custom Assertion Error!")
        }
        // 3
        val deferred = GlobalScope.async(exceptionHandler) {
            // Nothing will be printed,
            // relying on user to call deferred.await()
            throw ArithmeticException()
        }
        // 4
        // This suspends current coroutine until all given jobs are complete.
        println("Before join")
        joinAll(job, deferred)
        println("Finish")
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun mainExceptions() = runBlocking {
    // 2
    val launchJob = GlobalScope.launch {
        println("1. Exception created via launch coroutine")
        throw IndexOutOfBoundsException()
    }
    // 3
    launchJob.join()
    println("2. Joined failed job")
    // 4
    val deferred = GlobalScope.async {
        println("3. Exception created via async coroutine")
        throw ArithmeticException()
    }
    // 5
    try {
        deferred.await()
        println("4. Unreachable, this statement is never executed")
    } catch (e: Exception) {
        println("5. Caught ${e.javaClass.simpleName}")
    }
}
