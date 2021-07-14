package com.xiaoxin.experience.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaoxin
 */
public class StringUtil
{
    private static final char HYPHEN = '-';

    private static final Pattern humpPattern = Pattern.compile("[A-Z]");

    private static final Pattern ipPattern = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");

    public static String getIpFromUrl(String url)
    {
        if (StringUtils.isBlank(url))
        {
            return "";
        }

        String host = "";
        Matcher matcher = ipPattern.matcher(url);
        if (matcher.find())
        {
            host = matcher.group();
        }
        return host;
    }

    /**
     * 中划线格式字符串转换为驼峰格式字符串
     */
    public static String hyphenToCamel(String param)
    {
        if (StringUtils.isBlank(param))
        {
            return "";
        }

        if (!param.contains(String.valueOf(HYPHEN)))
        {
            return param;
        }

        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            char c = param.charAt(i);
            if (c == HYPHEN)
            {
                if (++i < len)
                {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰格式字符串转换为中划线格式字符串
     */
    public static String camelToHyphen(String str)
    {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(sb, HYPHEN + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

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

    public static String encodeEmoji(String orgStr)
    {
        if (StringUtils.isBlank(orgStr))
        {
            return orgStr;
        }
        String temp = orgStr;
        Pattern pattern = Pattern.compile("[^\u0000-\uffff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(orgStr);
        while (matcher.find())
        {
            StringBuilder sb = new StringBuilder("[em:");
            String mStr = matcher.group();
            for (int i = 0; i < mStr.length(); i++)
            {
                int emoji = mStr.charAt(i);
                if (i < mStr.length() - 1)
                {
                    sb.append(emoji).append('-');
                }
                else
                {
                    sb.append(emoji).append(']');
                }
            }
            temp = temp.replaceAll(mStr, sb.toString());
        }
        return temp;
    }

    public static String decodeEmoji(String orgStr)
    {
        if (StringUtils.isBlank(orgStr))
        {
            return orgStr;
        }
        String temp = orgStr;
        Pattern pattern = Pattern.compile("\\[em:[\\d\\-]+\\]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(orgStr);
        while (matcher.find())
        {
            StringBuilder sb = new StringBuilder("\\[em:");
            StringBuilder emojiSb = new StringBuilder();
            String mStr = matcher.group();
            String[] emojis = mStr.substring(4, mStr.length() - 1).split("-");
            for (int i = 0; i < emojis.length; i++)
            {
                int emoji = Integer.parseInt(emojis[i]);
                emojiSb.append((char)emoji);
                if (i < emojis.length - 1)
                {
                    sb.append(emoji).append("\\-");
                }
                else
                {
                    sb.append(emoji).append("\\]");
                }
            }
            temp = temp.replaceAll(sb.toString(), emojiSb.toString());
        }
        return temp;
    }

    public static String handleHtmlTag(String html)
    {
        if (StringUtils.isBlank(html))
        {
            return html;
        }

        return html.replace("<", "&lt;").replace(">", "&gt;");
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
