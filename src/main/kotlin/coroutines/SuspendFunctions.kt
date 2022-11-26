package coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

fun main() {
    getUserFromNetworkCallback("101") { user, error ->
        user?.run(::println)
        error?.printStackTrace()
    }
    GlobalScope.launch {
        val user = getUserSuspend("101")

        println("$user from coroutine")
    }

    println("main end")
    Thread.sleep(1500)
}


fun getUserFromNetworkCallback(
        userId: String,
        onUserReady: (User) -> Unit) {
    thread {
        Thread.sleep(1000)

        val user = User(userId, "Filip")
        onUserReady(user)
    }
    println("end")
}

fun getUserFromNetworkCallback(
        userId: String,
        onUserResponse: (User?, Throwable?) -> Unit) {
    thread {

        try {
            Thread.sleep(1000)
            val user = User(userId, "Filip")

            onUserResponse(user, null)
        } catch (error: Throwable) {
            onUserResponse(null, error)
        }
    }
}

suspend fun getUserSuspend(userId: String): User {
    delay(1000)

    return User(userId, "Filip")
}

data class User(val id: String, val name: String)