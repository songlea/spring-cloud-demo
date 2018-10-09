package com.songlea.demo.cloud.business;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

public class Test2 {

    private final static BigDecimal ONE_GB = new BigDecimal(1024 * 1024 * 1024);

    public static void main(String[] args) {
        BigDecimal decimal = new BigDecimal("28783333376");
        String gCapacity = decimal.divide(ONE_GB, 0, RoundingMode.CEILING).toPlainString() + "G";
        System.out.println(gCapacity);

        System.out.println(convertByteToG(28783333376d));
        System.out.println(getNum(233));

        System.out.println(parseObject2Integer(""));
    }

    public static Integer parseObject2Integer(Object object) {
        if (object == null || StringUtils.isBlank(object.toString()))
            return null;
        return Integer.valueOf(object.toString());
    }


    private static String convertByteToG(double byteCapacity) {
        double realGCapacity = byteCapacity / (double) (1024 * 1024 * 1024);

        int index = 0;
        double gCapacity = Math.pow(2, index++);
        while (gCapacity < realGCapacity) {
            gCapacity = Math.pow(2, index++);
        }

        return MessageFormat.format("{0}G", String.valueOf((int) gCapacity));
    }

    private static int getNum(int a) {
        int n = a - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
    }
}
