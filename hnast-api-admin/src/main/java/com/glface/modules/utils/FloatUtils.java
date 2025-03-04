package com.glface.modules.utils;

public class FloatUtils {
    /**
     * 获取小数点位
     * @param dec
     * @return
     */
    public static int decimalPlaces(float dec){
        String str = String.valueOf(dec);
        String[] arr = str.split("\\.");
        return arr[1].length();
    }

    /**
     * 是否合规
     * @return
     */
    public static boolean isMatch(float dec){
        return dec>0&&decimalPlaces(dec)>1;
    }
}
