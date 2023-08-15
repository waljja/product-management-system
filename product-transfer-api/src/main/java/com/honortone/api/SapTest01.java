package com.honortone.api;

import com.honortone.api.utils.SAPUtil;

import java.util.List;

public class SapTest01 {
    public static void main(String[] args) {

        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = sapUtil.getInfo("5000","009-0001453-00DR3","0001439431","313");
        //String[] ss = list.get(0);
        //System.out.println(ss[3]);
        for (String[] s : list){
            System.out.println(s[3]);
        }
    }
}
