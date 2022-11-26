package coroutines

import kotlinx.coroutines.suspendCancellableCoroutine



import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun main() {
    runBlocking {
        try {
            val data = getDataAsync() // just a regular suspend fun call
            println("Data received: $data")
        } catch (e: Exception) {
            println("Caught ${e.javaClass.simpleName}")
        }
    }
}

// Callback Wrapping using Coroutine
suspend fun getDataAsync(): String {
    return suspendCancellableCoroutine { continuation ->
        getData(object : AsyncCallback {
            override fun onSuccess(result: String) {
                continuation.resume(result) // resumes the coroutine successfully
            }

            override fun onError(e: Exception) {
                continuation.resumeWithException(e) // should be caught by the calling block
            }
        })
    }
}

// Method to simulate a long running task
fun getData(asyncCallback: AsyncCallback) {
    val triggerError = false

    try {
        Thread.sleep(3000)
        if (triggerError) {
            throw IOException()
        } else {
            // Send success
            asyncCallback.onSuccess("[Beep.Boop.Beep]")
        }
    } catch (e: Exception) {
        // send error
        asyncCallback.onError(e)
    }
}

// Callback
interface AsyncCallback {
    fun onSuccess(result: String)
    fun onError(e: Exception)
}