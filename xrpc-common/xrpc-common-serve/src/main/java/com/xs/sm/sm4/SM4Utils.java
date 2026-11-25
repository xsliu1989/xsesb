package com.xs.sm.sm4;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;

/**
 * 对称加密
 */
public class SM4Utils {


    public static String sm4In_Base64(String key, String data) {


        SM4 sm41 = SmUtil.sm4(key.getBytes());

        //加密为base64
        String base64Pass = sm41.encryptBase64(data);


        return base64Pass;
    }


    public static String sm4Out_Base64(String key, String data) {


        SM4 sm41 = SmUtil.sm4(key.getBytes());

        //加密为base64
        String base64Pass = sm41.decryptStr(data);


        return base64Pass;
    }


        /**
         * sm4 对称加密 ,需要1个加密key
         * @param
         */
    public static String sm4In(String key, String data) {


        SM4 sm41 = SmUtil.sm4(key.getBytes());
        //加密为Hex
        String hexPass = sm41.encryptHex(data);
        System.err.println("Hex形式的密文:"+hexPass);


        return  hexPass;


    }




    /**
     * sm4 对称加密 ,需要1个加密key
     * @param
     */
    public static String sm4Out(String key, String data) {

        SM4 sm41 = SmUtil.sm4(key.getBytes());

        //hex解密
        String s = sm41.decryptStr(data);

        return s;
    }
}
