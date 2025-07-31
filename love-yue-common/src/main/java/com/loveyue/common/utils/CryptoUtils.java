package com.loveyue.common.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

/**
 * 数据加密工具。
 */
@SuppressWarnings("unused")
public class CryptoUtils {
    private static final Object lock = new Object();

    // 长 ID 取值的进制（0~9A~Z）
    private static final int LONG_UID_RADIX = 36;

    // 长 ID 中随机数上限（不包含）
    private static final long LONG_UID_RANDOM_MAX = 1679616;

    // 长 ID 中随机数上限 10（不包含）
    private static final long LONG_DECIDE_RANDOM_MAX = 10;

    // 62 进制字符集
    private static final String BASE62_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 62 进制（0~9A~Za~z）
    private static final int BASE62_RADIX = BASE62_CHARSET.length();

    // 短 ID 序列号
    private static long shortUniqueIdSeqNo = 0;

    // 前一个生成的短 ID
    private static String previousShortUniqueId = null;

    // 前一个生成的 长 id
    private static long decUniqueId = 0L;


    //定义36进制数字
    private static final String X36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    //拿到36进制转换10进制的值键对
    private static final HashMap<Character, Integer> thirtySixToTen = createMapThirtySixToTen();

    // 拿到10进制转换36进制的值键对
    private static final HashMap<Integer, Character> tenToThirtySix = createMapTenToThirtySix();

    private static HashMap<Character, Integer> createMapThirtySixToTen() {
        HashMap<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < X36.length(); i++) {
            //0--0,... ..., Z -- 35的对应存放进去
            map.put(X36.charAt(i), i);
        }
        return map;
    }


    private static HashMap<Integer, Character> createMapTenToThirtySix() {
        HashMap<Integer, Character> map = new HashMap<>();
        for (int i = 0; i < X36.length(); i++) {
            // 0--0,... ..., 35 -- Z的对应存放进去
            map.put(i, X36.charAt(i));
        }
        return map;
    }


    /**
     * 生成全局唯一的 ID（36 进制）。
     *
     * @return 全局唯一的 ID
     */
    public static String uniqueId() {

        long milliTime = System.currentTimeMillis();

        long nanoTime = Math.abs(System.nanoTime());

        String random = "000" + Long.toString(
                (new SecureRandom()).nextLong() % LONG_UID_RANDOM_MAX,
                LONG_UID_RADIX
        );

        return (
            Long.toString(milliTime * 1000000 + nanoTime % 1000000, LONG_UID_RADIX)
                + random.substring(random.length() - 4)
        );

    }

    /**
     * 生成全局唯一的 ID（10 进制）。
     *
     * @return 全局唯一的 ID
     */
    public static long uniqueDecId() {
        synchronized (lock) {
            long milliTime = System.currentTimeMillis();
            long nanoTime = Math.abs(System.nanoTime());
            long decId = milliTime * 1000000 + nanoTime % 1000000;

            while (decUniqueId == decId) {
                try {
                    lock.wait(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return 0L;
                }
                milliTime = System.currentTimeMillis();
                nanoTime = Math.abs(System.nanoTime());
                decId = milliTime * 1000000 + nanoTime % 1000000;
            }

            decUniqueId = decId;
            return decId;
        }
    }

    /**
     * 根据给定的键值生成 UUID 格式的字符串。
     *
     * @param key 键值
     * @return UUID 格式字符串
     */
    public static String hashUUID(String key) {

        String hash = md5(key);

        return String.format(
            "%s-%s-%s-%s-%s",
            hash.substring(0, 8),
            hash.substring(8, 12),
            hash.substring(12, 16),
            hash.substring(16, 20),
            hash.substring(20, 32)
        );

    }

    /**
     * 解码六十二进制值。
     *
     * @param string 六十二进制值
     * @return 数值
     */
    public static long decodeBase62(String string) {
        int length = string.length();
        long result = 0L;
        long power = 1;

        for (int i = length - 1; i >= 0; i--) {
            result += BASE62_CHARSET.indexOf(string.charAt(i)) * power;
            power *= BASE62_RADIX;
        }

        return result;
    }

    /**
     * 编码六十二进制值。
     *
     * @param number 数值
     * @return 六十二进制值
     */
    public static String encodeBase62(long number) {
        return number == 0 ? "" : (encodeBase62(number / BASE62_RADIX) + BASE62_CHARSET.charAt(Math.toIntExact(number % BASE62_RADIX)));
    }

    /**
     * 生成短 ID。
     * 取当前时间的毫秒（自 1970-01-01T00:00:00.000Z 起的毫秒数，即 Unix 纪元时间），
     * 拼接系统纳秒数的毫秒后的两位，再拼接 1~9 的连号，
     * 将得到的字符串翻转排序后的数值转为六十二进制字符串。
     *
     * @return 短 ID
     */
    public static synchronized String shortUniqueId() {
        shortUniqueIdSeqNo = (shortUniqueIdSeqNo % 9) + 1;

        long value = (System.currentTimeMillis() * 1000 + ((System.nanoTime() % 1000000) / 1000) * 10 + shortUniqueIdSeqNo);

        String uid = String
                .format("%9s", encodeBase62(LongUtils.parseLong((new StringBuilder(String.valueOf(value))).reverse().toString())))
                .replaceAll("\\s", "0");

        // 如果重复，直接递增序列号
        while (uid.equals(previousShortUniqueId)) {
            shortUniqueIdSeqNo = (shortUniqueIdSeqNo % 9) + 1;
            value = (System.currentTimeMillis() * 1000 + ((System.nanoTime() % 1000000) / 1000) * 10 + shortUniqueIdSeqNo);
            uid = String
                    .format("%9s", encodeBase62(LongUtils.parseLong((new StringBuilder(String.valueOf(value))).reverse().toString())))
                    .replaceAll("\\s", "0");
        }

        previousShortUniqueId = uid;
        return uid;
    }

    /**
     * 生成文件的 MD5 摘要。
     *
     * @param file 文件
     * @return MD5 摘要
     */
    public static String md5(File file) {
        try {
            return md5(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    /**
     * 生成输入流的 MD5 摘要。
     *
     * @param inputStream 输入流
     * @return MD5 摘要
     */
    public static String md5(InputStream inputStream) {
        try {
            String hexString = DigestUtils.md5DigestAsHex(inputStream);
            inputStream.close();
            return hexString;
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 生成字符串的 MD5 摘要。
     *
     * @param string 输入字符串
     * @return MD5 摘要
     */
    public static String md5(String string) {
        return digest(string, "MD5");
    }

    /**
     * 生成字符串的 SHA-1 摘要。
     *
     * @param string 输入字符串
     * @return SHA-1 摘要
     */
    public static String sha(String string) {
        return digest(string, "SHA-1");
    }

    /**
     * 生成字符串的 SHA-256 摘要。
     *
     * @param string 输入字符串
     * @return SHA-256 摘要
     */
    public static String sha256(String string) {
        return digest(string, "SHA-256");
    }

    /**
     * 生成字符串的 SHA-384 摘要。
     *
     * @param string 输入字符串
     * @return SHA-384 摘要
     */
    public static String sha354(String string) {
        return digest(string, "SHA-384");
    }

    /**
     * 生成字符串的 SHA-512 摘要。
     *
     * @param string 输入字符串
     * @return SHA-512 摘要
     */
    public static String sha512(String string) {
        return digest(string, "SHA-512");
    }

    /**
     * 计算字符串摘要。
     *
     * @param string    字符串
     * @param algorithm 摘要算法
     * @return 字符串摘要
     */
    private static String digest(String string, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(string == null ? (new byte[]{}) : string.getBytes());
            return StringUtils.toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    /**
     * 生成字符串的 Base64 编码。
     *
     * @param string 编码字符串
     * @return Base64 字符串
     */
    public static String encodeBase64(String string) {
        return encodeBase64(string.getBytes());
    }

    /**
     * 生成 Base64 编码。
     *
     * @param inputStream 输入流
     * @return Base64 字符串
     */
    public static String encodeBase64(
        InputStream inputStream
    ) throws IOException {
        return encodeBase64(IOUtils.toByteArray(inputStream));
    }

    /**
     * 生成 Base64 编码。
     *
     * @param bytes 字节数组
     * @return Base64 字符串
     */
    public static String encodeBase64(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    /**
     * 解码 Base64 字符串。
     *
     * @param string Base64 字符串
     * @return 解码后的字符串
     */
    public static String decodeBase64(String string) {
        return new String(Base64.getDecoder().decode(string.getBytes()));
    }

    /**
     * 36 to 10
     *
     * @param pStr 36进制字符串
     * @return 十进制
     */
    public static Long th36To10(String pStr) {
        if (Objects.equals(pStr, "")) return 0L;
        //目标十进制数初始化为0
        long decimal = 0L;
        //记录次方,初始为36进制长度 -1
        int power = pStr.length() - 1;
        //将36进制字符串转换成char[]
        char[] keys = pStr.toCharArray();
        long base = 36L;
        for (int i = 0; i < pStr.length(); i++) {
            //拿到36进制对应的10进制数
            long value = thirtySixToTen.get(keys[i]);
            //定义静态进制数
            decimal =  decimal + value * pow(base, power);
            //执行完毕 次方自减
            power--;
        }
        return decimal;
    }

    public static String deciamlToThirtySix(long iSrc) {
        String result = "";
        long key;
        int value;
        int base = 36;

        key = iSrc / base;
        value = (int)(iSrc - key * base);
        if (key != 0) {
            result = result + deciamlToThirtySix(key);
        }

        result = result + tenToThirtySix.get(value).toString();

        return result;
    }


    public static Long pow(long a , int b) {
        long power = 1;
        for (int c = 0; c < b; c++)
            power *= a;
        return power;
    }

        public static void main(String[] args) {
        long uId = th36To10("BTBP8WGJNOSD");
        String abc = "BRGFKZHE8M5KX8O5";
        Calendar c1 = Calendar.getInstance();
        c1.clear();
        c1.set(2020, Calendar.JANUARY, 0, 0, 0, 0);
        long mills = c1.getTimeInMillis();
    }
}
