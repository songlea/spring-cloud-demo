package com.songlea.demo.kotlin.nio

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.CharBuffer

/**
 * Java NIO中的Buffer用于和通道Channel进行交互，数据是从通道读入缓冲区，从缓冲区写入到通道中的。
 * 缓冲区本质上是一块可以写入数据，然后可以从中读取数据的内存，这块内存被包装成NIO Buffer对象，并提供一组方法，用来方便的访问该块内存。
 */
@RunWith(JUnit4::class)
class BufferTests {

    /**
    读写数据的步骤：
    1、写入数据到Buffer：Buffer会记录下写了多少数据
    2、调用flip()方法：将Buffer从写模式切换到读模式，在读模式下可以读取之前写入到Buffer的所有数据
    3、从Buffer中读取数据
    4、调用clear()方法或者compact()方法：读完了所有数据，需要清空缓冲区，让它可以再次被写入，任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读取数据的后面。
    clear()：会清空整个缓冲区
    compact()：只会清空已经读过的数据

    Buffer的capacity,position和limit
    position与limit的含义取决于Buffer处在读模式还是写模式，而不管Buffer处在什么模式，capacity的含义总是一样的。
    capacity：作为一个内存块，Buffer有一个固定的大小值，你只能往里写capacity个byte,long,char等类型，一旦Buffer满了，需要将其清空才能继续写数据。
    position：当你写数据到Buffer中时，position表示当前的位置，初始的position值为0，当一个byte、long等数据写入到Buffer后，position会向前移动到下一个可插入数据的Buffer单元，position最大可为capacity-1；
    当读数据时，也是从某个特定位置读，当将Buffer从写模式切换到读模式，position会被重置为0，当从Buffer的position处读取数据时，position向前移动到下一个可读的位置。
    limit：在写模式下，Buffer的limit表示你最多能往Buffer中写多少数据，写模式下，limit等于Buffer的capacity；
    当切换Buffer到读模式，limit表示你最多能读到多少数据，因此，读模式时limit会被设置成写模式下的position值。

    Buffer的类型
    ByteBuffer
    MappedByteBuffer
    CharBuffer
    DoubleBuffer
    FloatBuffer
    IntBuffer
    LongBuffer
    ShortBuffer
     */
    @Test
    fun testBuffer() {
        val randomAccessFile = RandomAccessFile(File("D:/nio-test.data"), "rw")
        val fileChannel = randomAccessFile.channel

        /**
         * 想获得一个Buffer对象首先要进行分配
         */
        // create buffer with capacity of 48 bytes
        val buffer = ByteBuffer.allocate(48)

        /**
         * 向Buffer中写数据
         *  1、从Channel写到Buffer
         *  2、通过Buffer的put()方法写到Buffer里
         */
        // read into buffer
        var bytesRead = fileChannel.read(buffer)

        val charBuffer = CharBuffer.allocate(48)
        charBuffer.put("test")
        // 切换为读模式
        charBuffer.flip()
        while (charBuffer.hasRemaining()) {
            /**
             * 从Buffer中读取数据
             * 1、从Buffer读取数据到Channel
             * 2、使用get()方法从Buffer中读取数据
             */
            println(charBuffer.get())
        }
        /**
         * rewind()方法：Buffer.rewind()将position设回0，所以你可以重读Buffer中的所有数据，limit保持不变。
         */
        charBuffer.rewind()
        // 再次读取
        while (charBuffer.hasRemaining()) {
            println(charBuffer.get())
        }

        /**
         * mark()与reset()方法:
         * 通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。
         */
        charBuffer.mark()
        charBuffer.reset()

        /**
         * clear()与compact()方法
         * 如果调用的是clear()方法，position将被设回0，limit被设置成 capacity的值。换句话说，Buffer 被清空了。Buffer中的数据并未清除，
         * 只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
         * 如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。
         *
         * compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，
         * 设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。
         */
        charBuffer.clear()

        while (bytesRead != -1) {
            // make buffer ready for read
            buffer.flip()

            while (buffer.hasRemaining()) {
                // read 1 byte at a time
                println(buffer.get().toChar())
            }
            // make buffer ready for writing
            buffer.clear()

            bytesRead = fileChannel.read(buffer)
        }

        fileChannel.close()
    }


}