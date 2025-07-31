package com.loveyue.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * 字符串工具。
 */
@SuppressWarnings("unused")
public class StringUtils {

    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

    private static final char[] HEX_CHARSET = "0123456789abcdef".toCharArray();

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("\\d*");

    private static final Pattern START_STR_PATTERN = Pattern.compile("[a-zA-Z]+");

    // 校验邮箱地址
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[1+]\\d{6,10}$");

    private StringUtils() {
    }

    /**
     * 检查字符串是否为空白。
     *
     * @param string 检查对象字符串
     * @param trim   是否去除首尾空白字符
     * @return 字符串是否为空白
     */
    public static boolean isBlank(String string, boolean trim) {
        return string != null && (trim ? string.trim() : string).isEmpty();
    }

    /**
     * 检查字符串是否为空白。
     *
     * @param string 检查对象字符串
     * @return 字符串是否为空白
     */
    public static boolean isBlank(String string) {
        return isBlank(string, false);
    }

    /**
     * 检查给定的字符序列是否为空或仅由空白字符组成。
     *
     * @param cs 要检查的字符序列
     * @return 如果字符序列为空或仅由空白字符组成，则返回 true；否则返回 false
     */
    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查给定的字符序列是否不为空且至少包含一个非空白字符。
     *
     * @param cs 要检查的字符序列
     * @return 如果字符序列不为空且至少包含一个非空白字符，则返回 true；否则返回 false
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 检查字符串是否为空。
     *
     * @param string 检查对象字符串
     * @param trim   是否去除首尾空白字符
     * @return 字符串是否为空
     */
    public static boolean isEmpty(String string, boolean trim) {
        return string == null || isBlank(string, trim);
    }

    /**
     * 检查字符串是否为空。
     *
     * @param string 检查对象字符串
     * @return 字符串是否为空
     */
    public static boolean isEmpty(String string) {
        return isEmpty(string, false);
    }

    /**
     * 去除首尾空白字符。
     *
     * @param string 输入字符串
     * @return 去除首尾空白字符后的字符串
     */
    public static String trim(String string) {
        return trim(string, "");
    }

    /**
     * 去除首尾空白字符。
     *
     * @param string       输入字符串
     * @param defaultValue 当为空指针或空字符串时的默认值
     * @return 去除首尾空白字符后的字符串
     */
    public static String trim(String string, String defaultValue) {

        if (isEmpty(string, true)) {
            return defaultValue;
        }

        return string.trim();
    }

    /**
     * 重复字符串。
     *
     * @param string 字符串
     * @param times  重复次数
     * @return 新的字符串
     */
    public static String repeat(String string, int times) {
        return (new String(new char[times])).replace("\0", string);
    }

    /**
     * 整数补零。
     *
     * @param integer 整数
     * @param length  位数
     * @return 补位后的字符串
     */
    public static String pad(int integer, int length) {
        return padLeft(String.valueOf(integer), length, '0');
    }

    /**
     * 整数补位。
     *
     * @param integer 整数
     * @param length  位数
     * @param padding 补位字符
     * @return 补位后的字符串
     */
    public static String pad(int integer, int length, char padding) {
        return padLeft(String.valueOf(integer), length, padding);
    }

    /**
     * 字符串补位。
     *
     * @param string 字符串
     * @param length 位数
     * @return 补位后的字符串
     */
    public static String padLeft(String string, int length) {
        return padLeft(string, length, ' ');
    }

    /**
     * 字符串补位。
     *
     * @param string  字符串
     * @param length  位数
     * @param padding 补位字符
     * @return 补位后的字符串
     */
    public static String padLeft(String string, int length, char padding) {

        if (length <= string.length()) {
            return string;
        }

        return last(repeat(String.valueOf(padding), length) + string, length);
    }

    /**
     * 截取字符串中最后指定个数的字符。
     *
     * @param string 输入字符串
     * @param chars  截取字符数
     * @return 截取后的字符串
     */
    public static String last(String string, int chars) {

        int startAt = string.length() - chars;

        if (startAt < 0) {
            startAt = 0;
        }

        return string.substring(startAt, startAt + chars);
    }

    /**
     * 将对象转为 JSON 字符串。
     *
     * @param object 对象
     * @param pretty 是否格式化
     * @return JSON 字符串
     */
    public static String toJSON(Object object, boolean pretty) {

        ObjectWriter writer = (new ObjectMapper()).writer();

        if (pretty) {
            writer = writer.withDefaultPrettyPrinter();
        }

        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    /**
     * 将输入流转为字符串。
     *
     * @param stream 输入流
     * @return 字符串
     */
    public static String fromInputStream(InputStream stream) {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
            return writer.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 将对象转为 JSON 字符串。
     *
     * @param object 对象
     * @return JSON 字符串
     */
    public static String toJSON(Object object) {
        if (object == null) return null;
        return toJSON(object, false);
    }

    /**
     * 将 JSON 转为对象。
     *
     * @param <T>  范型
     * @param json JSON 字符串
     * @param type 类型
     * @return 转换后的对象
     */
    public static <T> T fromJSON(
            String json,
            Class<T> type
    ) throws IOException {

        if (json == null) {
            return null;
        }

        return (new ObjectMapper())
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(json, type);
    }

    /**
     * 将 JSON 转为对象。
     *
     * @param <T>  范型
     * @param json JSON 字符串
     * @param type 类型
     * @return 转换后的对象
     */
    public static <T> T fromJSON(
            String json,
            TypeReference<T> type,
            T defaultValue
    ) {

        if (isEmpty(json)) {
            return defaultValue;
        }

        try {
            return (new ObjectMapper())
                    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(json, type);
        } catch (IOException e) {
            return defaultValue;
        }

    }

    /**
     * 将json array反序列化为对象
     *
     * @param json              json
     * @param jsonTypeReference jsonTypeReference
     */
    public static <T> T decode(String json, TypeReference<T> jsonTypeReference) {
        try {
            return new ObjectMapper().readValue(json, jsonTypeReference);
        } catch (IOException e) {
            logger.error("Failed to decode JSON: {}", json, e);
        }
        return null;
    }

    /**
     * 将 JSON 转为对象。
     *
     * @param <T>    范型
     * @param stream 输入流
     * @param type   类型
     * @return 转换后的对象
     */
    public static <T> T fromJSON(
            InputStream stream,
            Class<T> type
    ) throws IOException {
        return (new ObjectMapper())
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(stream, type);
    }

    /**
     * 根据字节数组生成十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String toHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARSET[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARSET[v & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * 将对象转为 Map。
     *
     * @param object 输入值
     * @return 转换后的值
     */
    public static Object toMap(Object object) {
        if (DataTypeUtil.isPrimitiveType(object)) {
            return object;
        }

        if (object.getClass().isArray()) {
            object = Arrays.asList((Object[]) object);
        }

        if (object instanceof Iterable) {
            return convertIterableToMap((Iterable<?>) object);
        }

        if (object instanceof Map<?, ?>) {
            return convertMapToMap((Map<?, ?>) object);
        }

        return convertBeanToMap(object);
    }

    private static List<Object> convertIterableToMap(Iterable<?> iterable) {
        List<Object> list = new ArrayList<>();
        for (Object o : iterable) {
            list.add(toMap(o));
        }
        return list;
    }

    private static Map<String, Object> convertMapToMap(Map<?, ?> source) {
        Map<String, Object> map = new HashMap<>();
        Set<?> keys = source.keySet();
        for (Object key : keys) {
            map.put(key.toString(), toMap(source.get(key)));
        }
        return map;
    }

    private static Map<String, Object> convertBeanToMap(Object object) {
        Map<String, Object> map = new HashMap<>();

        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException e) {
            return Collections.emptyMap();
        }

        for (PropertyDescriptor property : info.getPropertyDescriptors()) {
            processProperty(property, object, map);
        }

        return map;
    }

    private static void processProperty(PropertyDescriptor property, Object object, Map<String, Object> map) {
        Method reader = property.getReadMethod();
        if (reader == null) {
            return;
        }

        Object propertyValue;
        try {
            propertyValue = reader.invoke(object);
        } catch (ReflectiveOperationException e) {
            return;
        }

        String propertyName = property.getName();
        if ("class".equals(propertyName)) {
            return;
        }

        map.put(propertyName, toMap(propertyValue));
    }

    /**
     * URL 内容编码。
     *
     * @param string 输入字符串
     * @return 编码后的字符串
     */
    public static String encodeURIComponent(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    /**
     * 取得字符串中所有符合给定格式的片段。
     *
     * @param pattern 格式
     * @param input   输入字符串
     * @return 符合给定格式的片段列表
     */
    public static List<String> findAll(final Pattern pattern, final String input) {

        Matcher matcher = pattern.matcher(input);

        List<String> matched = new ArrayList<>();

        while (matcher.find()) {
            matched.add(matcher.group(0));
        }

        return matched;
    }

    /**
     * 将对象转为 URL Encoded 字符串。
     *
     * @param object 输入值
     * @return URL Encoded 字符串
     */
    public static String toURLEncoded(Object object) {
        return String.join("&", toNameValuePairs(null, object));
    }

    private static final String[] CHARS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    /**
     * 获取字符数组的副本
     *
     * @return 字符数组的副本
     */
    public static String[] getChars() {
        return CHARS.clone();
    }

    /**
     * 生成短的8位uuid。二维码用
     */
    public static String generateShortUuid() {
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(CHARS[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    /**
     * 将对象转为键值对列表。
     *
     * @param parentKey 上级名称
     * @param object    输入值
     * @return 键值对列表
     */
    private static List<String> toNameValuePairs(
            String parentKey,
            Object object
    ) {
        List<String> keyValueList = new ArrayList<>();

        if (object == null) {
            return keyValueList;
        }

        if (DataTypeUtil.isPrimitiveType(object)) {
            keyValueList.add(parentKey + "=" + encodeURIComponent(object.toString()));
            return keyValueList;
        }

        Map<?, ?> map = (Map<?, ?>) toMap(object);
        if (map == null) {
            return keyValueList;
        }

        String normalizedParentKey = normalizeParentKey(parentKey);
        processMapEntries(map, normalizedParentKey, keyValueList);

        return keyValueList;
    }

    private static String normalizeParentKey(String parentKey) {
        if (parentKey != null && !parentKey.isEmpty()) {
            return parentKey + ".";
        }
        return "";
    }

    private static void processMapEntries(Map<?, ?> map, String parentKey, List<String> keyValueList) {
        Set<?> keys = map.keySet();

        for (Object key : keys) {
            String name = parentKey + key.toString();
            Object value = map.get(key);

            if (value == null) {
                continue;
            }

            processMapValue(key, name, value, keyValueList);
        }
    }

    private static void processMapValue(Object key, String name, Object value, List<String> keyValueList) {
        if (value instanceof Map) {
            keyValueList.addAll(toNameValuePairs(name, value));
        } else if (value instanceof Iterable) {
            processIterableValue(name, (Iterable<?>) value, keyValueList);
        } else {
            keyValueList.add(key + "=" + encodeURIComponent(value.toString()));
        }
    }

    private static void processIterableValue(String name, Iterable<?> iterable, List<String> keyValueList) {
        for (Object item : iterable) {
            keyValueList.addAll(toNameValuePairs(name, item));
        }
    }

    /**
     * 根据位数取字符串
     *
     * @param str       str
     * @param byteCount byteCount
     */
    public static String substringByCount(String str, int byteCount) {
        StringBuilder buff = new StringBuilder();
        if (str != null && !str.isEmpty() && byteCount > 0) {
            char c;
            int sumByteCount = 0;
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                sumByteCount += String.valueOf(c).getBytes(StandardCharsets.UTF_8).length;
                if (sumByteCount > byteCount) {
                    break;
                }
                buff.append(c);
            }
        }

        return buff.toString();
    }

    /**
     * 根据字节位数取字符串
     *
     * @param str       str
     * @param start     start
     * @param byteCount byteCount
     */
    public static String substringByCount(String str, int start, int byteCount) {
        StringBuilder buff = new StringBuilder();
        if (str != null && !str.isEmpty()) {
            start = substringByCount(str, start).length();
            if (byteCount > 0) {
                char c;
                int sumByteCount = 0;
                for (int i = start; i < str.length(); i++) {
                    c = str.charAt(i);
                    sumByteCount += String.valueOf(c).getBytes(StandardCharsets.UTF_8).length;
                    if (sumByteCount > byteCount) {
                        break;
                    }
                    buff.append(c);
                }
            }
        }
        return buff.toString();
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword keyword
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isEmpty(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static boolean isIDNumber(String idNumber) {
        if (idNumber == null || idNumber.isEmpty()) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        boolean matches = isMatches(idNumber);

        //判断第18位校验值
        if (matches && idNumber.length() == 18) {
            try {
                char[] charArray = idNumber.toCharArray();
                //前十七位加权因子
                int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                //这是除以11后，可能产生的11位余数对应的验证码
                String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                int sum = 0;
                for (int i = 0; i < idCardWi.length; i++) {
                    int current = Integer.parseInt(String.valueOf(charArray[i]));
                    int count = current * idCardWi[i];
                    sum += count;
                }
                char idCardLast = charArray[17];
                int idCardMod = sum % 11;
                if (idCardY[idCardMod].equalsIgnoreCase(String.valueOf(idCardLast))) {
                    return true;
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("身份证最后一位:{}错误,正确的应该是:{}",
                                String.valueOf(idCardLast).toUpperCase(),
                                idCardY[idCardMod].toUpperCase());
                    }
                    return false;
                }

            } catch (Exception e) {
                logger.error("身份证号码验证异常: {}", idNumber, e);
                return false;
            }
        }


        return matches;
    }

    private static boolean isMatches(String idNumber) {
        // 简化的身份证号码格式验证
        if (idNumber == null || (idNumber.length() != 15 && idNumber.length() != 18)) {
            return false;
        }
        
        // 18位身份证：前6位地区码 + 8位出生日期 + 3位顺序码 + 1位校验码
        if (idNumber.length() == 18) {
            return idNumber.matches("^[1-9]\\d{5}[12]\\d{7}[0-9Xx]$");
        }
        
        // 15位身份证：前6位地区码 + 6位出生日期 + 3位顺序码
        return idNumber.matches("^[1-9]\\d{5}\\d{8}$");
    }

    /*方法二：推荐，速度最快
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[-+]?\\d+$");
    
    public static boolean isInteger(String str) {
        if (str == null) return false;
        return INTEGER_PATTERN.matcher(str).matches();
    }

    /**
     * 首字母小写
     */
    public static String lowerFirst(String name) {
        //substring
        char[] chars = name.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 下划线命名转驼峰命名
     *
     * @param underscore underscore
     */
    public static String underscoreToCamelCase(String underscore) {
        String[] ss = underscore.split("_");
        if (ss.length == 1) {
            return underscore;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            sb.append(upperFirstCase(ss[i]));
        }

        return sb.toString();
    }

    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
    
    /**
     * 驼峰 转下划线
     *
     * @param camelCase camelCase
     */
    public static String toLine(String camelCase) {
        Matcher matcher = HUMP_PATTERN.matcher(camelCase);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 首字母 转小写
     *
     * @param str str
     */
    private static String lowerFirstCase(String str) {
        return lowerFirst(str);
    }

    /**
     * 首字母 转大写
     *
     * @param str str
     */
    private static String upperFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str str
     */
    public static boolean isNumeric(String str) {
        Matcher isNum = NUMERIC_PATTERN.matcher(str);
        return isNum.matches();
    }

    /**
     * 取得首字母ASCII码
     *
     * @param st st
     */
    public static int getAsc(String st) {
        byte[] gc = st.getBytes();
        return gc[0];
    }

    /**
     * 返回开头的字符串（不含数字空格等）ABCD 12 返回 ABCD
     *
     * @param str str
     */
    public static String getStartStr(String str) {
        Matcher isStr = START_STR_PATTERN.matcher(str);

        if (isStr.find()) {
            return str.replaceAll("(\\w+?)[\\d\\s].*", "$1");
        } else {
            return "";
        }
    }

    /**
     * 返回第一段数字 ABCD12C3 返回12
     */
    public static Integer getStartFigure(String str) {
        str = str.replaceAll("([_a-zA-Z]*|^)(\\d+)(.*)", "$2");
        if (isNumeric(str)) {
            return Integer.parseInt(str);
        } else {
            return 0;
        }
    }
    
    public static boolean checkEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }


    public static boolean checkMobile(String in) {
        return MOBILE_PATTERN.matcher(in).matches();
    }
}
