package com.xs.util;

import java.io.UnsupportedEncodingException;

public class StringUtil_废弃 {
    /**
     * 按字节截取字符串 ，指定截取起始字节位置与截取字节长度
     *
     * @param orignal要截取的字符串
     * @param start截取开始位置；
     * @param count截取的长度
     * @return 截取后的字符串
     *
     * @throws UnsupportedEncodingException
     *             使用了JAVA不支持的编码格式
     */
    public static String substringByte(String orignal, int start, int count) {

        // 如果目标字符串为空，则直接返回，不进入截取逻辑；
        if (orignal == null || "".equals(orignal))
            return orignal;

        // 截取Byte长度必须>0
        if (count <= 0)
            return orignal;

        // 截取的起始字节数必须比
        if (start < 0)
            start = 0;

        // 目标char Pull buff缓存区间；
        StringBuffer buff = new StringBuffer();

        try {
            // 截取字节起始字节位置大于目标String的Byte的length则返回空值
            if (start >= getStringByteLenths(orignal))
                return null;
            int len = 0;
            char c;
            // 遍历String的每一个Char字符，计算当前总长度
            // 如果到当前Char的的字节长度大于要截取的字符总长度，则跳出循环返回截取的字符串。
            for (int i = 0; i < orignal.toCharArray().length; i++) {
                c = orignal.charAt(i);

                // 当起始位置为0时候
                if (start == 0) {
                    len += String.valueOf(c).getBytes("GBK").length;
                    if (len <= count)
                        buff.append(c);
                    else
                        break;
                } else {
                    // 截取字符串从非0位置开始
                    len += String.valueOf(c).getBytes("GBK").length;
                    if (len >= start && len <= start + count) {
                        buff.append(c);
                    }
                    if (len > start + count)
                        break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 返回最终截取的字符结果;
        // 创建String对象，传入目标char Buff对象
        return new String(buff);
    }

    /**
     * 计算当前String字符串所占的总Byte长度
     *
     * @param args
     *            要截取的字符串
     * @return 返回值int型，字符串所占的字节长度，如果args为空或者“”则返回0
     * @throws UnsupportedEncodingException
     */
    public static int getStringByteLenths(String args)
            throws UnsupportedEncodingException {
        return args != null && args != "" ? args.getBytes("UTF8").length : 0;
    }
}
