package com.songlea.demo.kotlin

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.StampedLock
import kotlin.concurrent.thread
import kotlin.math.sqrt

/**
 * 可乐观的读锁
 */
class StampedLockTests {

    class Point(private var x: Double, private var y: Double) {

        private val stampedLock = StampedLock() // 定义StampedLock锁,

        // 写的方法
        fun move(deltaX: Double, deltaY: Double) {
            val stamp = stampedLock.writeLock()
            try {
                x += deltaX
                y += deltaY
            } finally {
                stampedLock.unlockWrite(stamp) // 退出临界区,释放写锁
            }
        }

        // 只读的方法
        fun distanceFromOrigin(): Double {
            // 试图尝试一次乐观读 返回一个类似于时间戳的邮戳整数stamp 这个stamp就可以作为这一个所获取的凭证
            var stamp = stampedLock.tryOptimisticRead()
            // 读取x和y的值,这时候我们并不确定x和y是否是一致的
            var currentX = x
            var currentY = y
            // 判断这个stamp是否在读过程发生期间被修改过
            if (!stampedLock.validate(stamp)) {
                stamp = stampedLock.readLock()
                try {
                    currentX = x
                    currentY = y
                } finally {
                    stampedLock.unlockRead(stamp)
                }
            }
            return sqrt(currentX * currentX + currentY * currentY)
        }
    }

    @Test
    fun test() {
        val point = Point(1.0, 2.0)
        val start = System.currentTimeMillis()
        // 协程
        runBlocking {
            for (i in 0..10000) {
                launch {
                    point.move(2.0, 3.0)
                    println("Now distance: ${point.distanceFromOrigin()}")
                }
            }
        }
        println("coroutine take time:${System.currentTimeMillis() - start}")
        // 线程
        val countDownLatch = CountDownLatch(10000)
        val start2 = System.currentTimeMillis()
        val point2 = Point(1.0, 2.0)
        for (i in 0..10000) {
            thread {
                point2.move(2.0, 3.0)
                println("Now distance: ${point2.distanceFromOrigin()}")
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        println("thread take time:${System.currentTimeMillis() - start2}")

    }
}