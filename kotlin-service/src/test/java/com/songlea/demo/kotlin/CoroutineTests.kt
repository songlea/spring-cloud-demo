package com.songlea.demo.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CoroutineTests {

    @Test
    fun first() {
        // 本质上，协程是轻量级的线程。它们在某些 CoroutineScope 上下文中与 launch 协程构建器一起启动
        GlobalScope.launch {
            delay(1000L)
            println("world!")
        }
        println("Hello,")
        Thread.sleep(2000L)
    }

    @Test
    fun testRunBlocking() {
        GlobalScope.launch {
            // 在后台启动一个新的协程并继续
            delay(1000L)
            println("World!")
        }
        println("Hello,") // 主线程中的代码会立即执行
        // 调用了 runBlocking 的主线程会一直阻塞直到 runBlocking 内部的协程执行完毕
        runBlocking {
            // 但是这个表达式阻塞了主线程
            delay(2000L)  // 我们延迟 2 秒来保证 JVM 的存活
        }
    }

    @Test
    fun testJoin() {
        runBlocking {
            val job = launch {
                // 在 runBlocking 作用域中启动一个新协程
                delay(1000L)
                println("World!")
            }
            println("Hello,")
            // 可以等待协程执行完成
            job.join()
        }
    }

    @Test
    fun testSuspend() {
        suspend fun doWorld() {
            delay(1000L)
            println("world!")
        }

        runBlocking {
            launch {
                doWorld()
            }
            println("Hello,")
        }
    }

    @Test
    fun testCancelAndJoin() {
        // 如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的，isActive可以显式的检查取消状态
        runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // 等待一段时间
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // 取消该作业并等待它结束
            println("main: Now I can quit.")
        }
    }

    @Test
    fun testFinally() {
        runBlocking {
            val job = launch {
                try {
                    repeat(1000) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                } finally {
                    println("job: I'm running finally")
                }
            }
            delay(1300L) // 延迟一段时间
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // 取消该作业并且等待它结束
            println("main: Now I can quit.")
        }
    }

    @Test
    fun testChannel() {
        runBlocking {
            val channel = Channel<Int>()
            launch {
                //  这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
                for (x in 1..5) {
                    channel.send(x * x)
                }
                // 我们结束发送
                channel.close()
            }
            for (y in channel) {
                println(y)
            }
            println("Done!")
        }
    }

    suspend fun foo(): List<Int> {
        delay(1000)
        return listOf(1, 2, 3)
    }

    @Test
    fun testSuspend2() = runBlocking {
        foo().forEach { value -> println(value) }
    }
}
