package com.ktg.mes.fg.utils;

import java.text.SimpleDateFormat;

public class Test15
{

    private static volatile int Guid = 100;


    public static String getGuid()
    {
        Test15.Guid += 1;
        //获取时间戳
        long now = System.currentTimeMillis();
        System.out.println(now);
        //获取4位年份数字
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        //获取年份
        String time = dateFormat.format(now);
        System.out.println(time);
        String info = now + "";
        int ran = 0;
        if (Test15.Guid > 999)
        {
            Test15.Guid = 100;
        }
        ran = Test15.Guid;

        return "FG"+time + info.substring(2, info.length()) + ran;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            System.out.println(getGuid());
        }
    }
}