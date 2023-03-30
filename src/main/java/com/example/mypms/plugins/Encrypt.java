package com.example.mypms.plugins;

import org.apache.commons.codec.digest.DigestUtils;

public class Encrypt {
    public static String md5(String s){
        return DigestUtils.md5Hex(s);
    }

    public static String sha256(String s){
        return DigestUtils.sha256Hex(s);
    }

    public static String sha512(String s){
        return DigestUtils.sha512Hex(s);
    }
}
