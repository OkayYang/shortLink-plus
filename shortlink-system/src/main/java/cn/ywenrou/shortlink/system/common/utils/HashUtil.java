package cn.ywenrou.shortlink.system.common.utils;

import cn.hutool.core.lang.hash.MurmurHash;

public class HashUtil {
    private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int SIZE = CHARS.length;

    private static String convertDecToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(CHARS[(int)(num % SIZE)]);
            num /= SIZE;
        } while (num > 0);
        return sb.reverse().toString();
    }

    public static String hashToBase62(String str) {
        int hash = MurmurHash.hash32(str);
        return convertDecToBase62(hash < 0 ? Integer.MAX_VALUE - (long)hash : hash);
    }
}