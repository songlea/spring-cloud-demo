package com.songlea.demo.kotlin

import kotlinx.coroutines.*
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

}
