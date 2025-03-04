package com.glface.base.utils;

/**
 * 防止SQL注入工具类
 */
public class AntiSQLInjectionUtil {

    public final static String regex = "'|=|and|exec|execute|insert\\s|select\\s|delete\\s|update\\s|count|drop|\\*|%|chr|mid|master|truncate|" +
            "char|declare|sitename|net user|xp_cmdshell|;|or\\s|-|\\+|,|like'|and\\s|exec|execute|insert\\s|create\\s|drop|" +
            "table|from|grant|use|group_concat|column_name|" +
            "information_schema.columns|table_schema|union|where|select\\s|delete\\s|update\\s|order\\s|by\\s|count|\\*|" +
            "chr|mid|master|truncate|char|declare|or\\s|;|-|--|\\+|,|like\\s|//|/|%|#";

    /**
     * 把SQL关键字替换为空字符串
     *
     * @param param
     * @return
     */
    public static String filter(String param) {
        if (param == null) {
            return param;
        }
        return param.replaceAll("(?i)" + regex, ""); // (?i)不区分大小写替换
    }

    public static void main(String[] args) {
        //System.out.println(StringEscapeUtils.escapeSql("1' or '1' = '1; drop table test"));
        // //1'' or ''1'' = ''1; drop table test
        String str = "create11 * from test where 1 = 1 And name != 'sql' ";
        String outStr = AntiSQLInjectionUtil.filter(str);
        System.out.println(outStr);
    }

}
