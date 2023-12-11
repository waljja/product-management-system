package com.honortone.api;

import com.honortone.api.utils.SAPUtil;

import java.util.Date;
import java.util.List;

public class SapTest01 {
    public static void main(String[] args) {

        System.out.println(new Date());
        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = sapUtil.getInfo("1100","650-TATN133-00R1","","313");
        //String[] ss = list.get(0);
        //System.out.println(ss[3]);
        for (String[] s : list){
            System.out.println(s.toString());
        }
    }
}
