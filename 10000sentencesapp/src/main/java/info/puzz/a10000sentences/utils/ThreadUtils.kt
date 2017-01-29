package info.puzz.a10000sentences.utils

fun main(args : List<String>) {
    println("args=$args")
}

class ThreadUtils {

    companion object {
        fun sleep(millis: Long) {
            try {
                Thread.sleep(millis)
            } catch (ignore: Exception) {}
        }
    }
}