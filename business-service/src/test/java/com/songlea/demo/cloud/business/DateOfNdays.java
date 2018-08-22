package com.songlea.demo.cloud.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 高斯日记
 * 大数学家高斯有个好习惯：无论如何都要记日记
 * 他的日记有个与众不同的地方，他从不注明年月日，而是用一个整数代替，比如：4210，
 * 后来人们知道，那个整数就是日期，它表示那一天是高斯出生后的第几天，
 * 在高斯发现的一个重要定理的日记上标注着：5343，因此可算出那天是：1791年12月15日。
 * 高斯获得博士学位的那天日记上标着：8113,请你算出高斯获得博士学位的年月日
 */
public class DateOfNdays {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1791, Calendar.DECEMBER, 15);
        calendar.add(Calendar.DATE, -5343);
        calendar.add(Calendar.DATE, 8113);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    }

}
