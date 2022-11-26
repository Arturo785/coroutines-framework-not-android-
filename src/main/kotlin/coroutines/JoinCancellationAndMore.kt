import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main(){
    //mainJoin()
    //mainNoJoin()
    mainCancelAndJoin()
}

fun mainJoin() = runBlocking {
    val job = launch {
        println("Crunching numbers [Beep.Boop.Beep]...")
        delay(1000L)
    }

    // waits for job's completion
    job.join()
    println("main: Now I can quit.")
}

fun mainNoJoin() = runBlocking {
    val job = launch {
        println("Crunching numbers [Beep.Boop.Beep]...")
        delay(1000L)
    }

    // waits for job's completion
    //job.join()
    println("main: Now I can quit.")
}

fun mainCancelAndJoin() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("$i. Crunching numbers [Beep.Boop.Beep]…")
            delay(500L)
        }
    }
    delay(1300L) // delay a bit
    println("main: I am tired of waiting!")
    // cancels the job and waits for job’s completion
    job.cancelAndJoin()
    println("main: Now I can quit.")
}