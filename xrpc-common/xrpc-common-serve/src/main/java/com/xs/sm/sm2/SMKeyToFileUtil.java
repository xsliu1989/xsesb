package com.xs.sm.sm2;

import cn.hutool.crypto.SecureUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SMKeyToFileUtil {

    public static void main(String[] args) {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");

        PublicKey aPublic = pair.getPublic();
        PrivateKey aPrivate = pair.getPrivate();
        //公钥 key 和私钥 key 转文件
        SM2Utils.exportPublicKey(aPublic,"F:/sm2/public_key.pem");
        SM2Utils.exportPrivateKey(aPrivate,"F:/sm2/private_key.pem");
    }

}
