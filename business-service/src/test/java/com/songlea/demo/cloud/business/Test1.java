package com.songlea.demo.cloud.business;

import java.util.Arrays;

public class Test1 {

    String str = new String("good");

    char[] ch = {'a', 'b', 'c'};

    public void change(String str, char[] ch) {
        str = "test ok";
        ch[0] = 'g';
    }

    public static void main(String[] args) {

        // 题1 output: true false false true
        /*
        String str1 = "aihuishou";
        String str2 = str1;
        String str3 = new String("aihuishou");
        String str4 = new String("aihuishou");
        System.out.println(str1 == str2);
        System.out.println(str1 == str3);
        System.out.println(str4 == str3);
        System.out.println(str1.equals(str4));
        */

        // 题2 output:
        // static A
        // static B
        // I'm A class
        // HelloA
        // I'm B class
        // HelloB
        // new HelloB();

        // 题3 output: 52
        // System.out.println("5" + 2);

        // 题4 output: good and gbc
        /*
        Test1 test1 = new Test1();
        test1.change(test1.str, test1.ch);
        System.out.print(test1.str + " and ");
        System.out.print(test1.ch);
        */

        int[] arr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int[] result = Test1.shuffle(arr);
        System.out.println(Arrays.toString(result));
    }

    // 时间复杂度： o(n)
    public static int[] shuffle(int[] array) {
        int i = array.length;
        while (i > 0) {
            int j = (int) Math.floor(Math.random() * i--);
            int bak = array[i];
            array[i] = array[j];
            array[j] = bak;
        }
        return array;
    }

}

class HelloA {

    public HelloA() {
        System.out.println("HelloA");
    }

    {
        System.out.println("I'm A class");
    }

    static {
        System.out.println("static A");
    }
}

class HelloB extends HelloA {

    public HelloB() {
        System.out.println("HelloB");
    }

    {
        System.out.println("I'm B class");
    }

    static {
        System.out.println("static B");
    }
}
