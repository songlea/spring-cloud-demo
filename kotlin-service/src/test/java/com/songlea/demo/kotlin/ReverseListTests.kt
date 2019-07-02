package com.songlea.demo.kotlin

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ReverseListTests {

    class NodeList(var value: Int) {
        var next: NodeList? = null
    }

    @Test
    fun reverse() {

        // 反转单向链表
        fun reverseList(head: NodeList): NodeList? {
            var pre: NodeList? = null
            var curr: NodeList? = head
            while (curr != null) {
                val nextTemp = curr.next
                curr.next = pre
                pre = curr
                curr = nextTemp
            }
            return pre
        }

        val head = NodeList(9)
        val nodeList2 = NodeList(10)
        val nodeList3 = NodeList(11)
        val nodeList4 = NodeList(12)
        head.next = nodeList2
        nodeList2.next = nodeList3
        nodeList3.next = nodeList4

        var nodeList: NodeList? = head
        while (nodeList != null) {
            print("${nodeList.value}->")
            nodeList = nodeList.next
        }

        // 换行
        println()

        var result = reverseList(head)
        while (result != null) {
            print("${result.value}->")
            result = result.next
        }

    }
}