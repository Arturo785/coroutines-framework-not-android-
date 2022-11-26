import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.*


/*join()
 Job blocks all the threads until the coroutine in which it is written or have context finished its work.
 Only when the coroutine gets finishes, lines after the join() function will be executed.*/

fun main() {

    //  mainWithLazy()
    // mainWithParents()
    GlobalScope.launch {

        println("The job started")
        delay(200)
        println("The job returned")

    }
   // a.join()
    println("I went to other part")
    //mainRepeat()
    Thread.sleep(3000) // this is to wait and the program does not finish

}

fun mainWithLazy() {
    val job1 = GlobalScope.launch(start = CoroutineStart.LAZY) {
        delay(200)
        println("Pong")
        delay(200)
    }

    GlobalScope.launch {
        delay(200)
        println("Ping")
        job1.join() // does not start until join is called which is in here
        println("Ping")  // gets executed after the job finishes thanks to join
        delay(200)
    }
}


fun mainWithParents() {
    with(GlobalScope) {
        val parentJob = launch {
            delay(200)
            println("I’m the parent") // this is not executed until all the children are completed
            delay(200)
        }
        launch(context = parentJob) {
            delay(200)
            println("I’m a child")
            delay(200)
        }
        launch(context = parentJob) {
            delay(200)
            println("I’m the second child")
            delay(200)
        }
        launch() {
            delay(200)
            println("I’m a independent")
            delay(200)
        }
        if (parentJob.children.iterator().hasNext()) {
            println("The Job has children!")
        } else {
            println("The Job has NO children")
        }
        Thread.sleep(1000)
    }
}

fun mainRepeat() {
    var isDoorOpen = false

    println("Unlocking the door... please wait.\n")
    GlobalScope.launch {
        delay(3000)

        isDoorOpen = true
    }

    GlobalScope.launch {
        repeat(4) {
            println("Trying to open the door...\n")
            delay(800)

            if (isDoorOpen) {
                println("Opened the door!\n")
            } else {
                println("The door is still locked\n")
            }
        }
    }

    Thread.sleep(5000)
}

