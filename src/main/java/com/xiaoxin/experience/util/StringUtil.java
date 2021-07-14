package com.xiaoxin.experience.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * @author xiaoxin
 */
public class StringUtil
{
    private StringUtil(){}

    /**
     * toLowerCase
     *
     * @param str String
     * @return String
     */
    public static String toLowerCase(String str)
    {
        if (null == str)
        {
            return null;
        }
        return str.toLowerCase(Locale.getDefault());
    }

    public static String toUpperCase(String str)
    {
        if (null == str)
        {
            return null;
        }
        return str.toUpperCase(Locale.getDefault());
    }

    /**
     * 检查字符串是否合法，字符串为null,unknown，或者长度为0都认为非法
     *
     * @param str 字符串
     * @return 字符串是否合法
     */
    public static boolean validate(String str)
    {
        return (StringUtils.isNotBlank(str) && !"null".equalsIgnoreCase(str) && !"unknown".equalsIgnoreCase(str));
    }

    /**
     * 给字符串去掉空格
     *
     * @param arg 字符串
     * @return String
     */
    public static String trim(String arg)
    {
        if (null == arg)
        {
            return null;
        }
        else
        {
            return arg.trim();
        }
    }

    public static String numToString(long number, int length)
    {
        return alignString(Long.toString(number), length);
    }

    public static String alignString(String str, int length)
    {
        return alignString(str, length, false);
    }

    public static String alignString(String str, char repeat, int length)
    {
        return alignString(str, repeat, length, false);
    }

    public static String alignString(String str, int length, boolean tooLargeException)
    {
        return alignString(str, '0', length, tooLargeException);
    }

    public static String alignString(String str, char repeat, int length, boolean tooLargeException)
    {
        int zeroLength = length - str.length();
        if (zeroLength < 0)
        {
            if (tooLargeException)
            {
                throw new IllegalArgumentException("string[" + str + "] is too long than the length[" + length + "]");
            }
            return str;
        }

        if (zeroLength == 0)
        {
            return str;
        }

        return StringUtils.repeat(repeat, zeroLength) + str;
    }

    /**
     * 将null转为空字符串
     *
     * @param str 传入字符串
     */
    public static String toString(String str)
    {
        return null == str ? "" : str;
    }

    /**
     * 拼装字符串
     *
     * @param stringArr 需要拼接的字符串数组
     */
    public static String appendStr(String... stringArr)
    {
        if (null == stringArr || 0 == stringArr.length)
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String str : stringArr)
        {
            sb.append(toString(str));
        }
        return sb.toString();
    }

    public static int toInt(String str, int defValue)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            return defValue;
        }
    }

    public static boolean equals(String left, String right)
    {
        return toString(left).equals(toString(right));
    }

    /**
     * 替换字符串里最后一次出现的 子字符串
     *
     * @param string  原生字符串
     * @param match   匹配的字符串
     * @param replace 要替换的字符串
     */
    public static String replaceLast(String string, String match, String replace)
    {
        if (StringUtils.isBlank(string) || null == replace)
        {
            return string;
        }

        StringBuilder sBuilder = new StringBuilder(string);
        int lastIndexOf = sBuilder.lastIndexOf(match);
        if (-1 == lastIndexOf)
        {
            return string;
        }

        return sBuilder.replace(lastIndexOf, lastIndexOf + match.length(), replace).toString();
    }
}
