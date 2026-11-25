package com.xs.sm.sm3;

import cn.hutool.crypto.SmUtil;

public class SM3Utils {

    /**
     * sm3 摘要签名算法
     * @param data
     */
    public static String sm3(String data) {
        String s = SmUtil.sm3(data);
        return s;
    }

}
