package uitls;

import org.springframework.util.CollectionUtils;

import java.util.*;

import static uitls.StringUtils.trim;

/**
 * 字符串工具。
 */
@SuppressWarnings("unused")
public class LongUtils {

    private LongUtils(){}


    /**
     * 检查Long是否为空。
     *
     * @param lng 检查对象字符串
     * @return 字符串是否为空
     */
    public static boolean isEmpty(Long lng) {
        return lng == null || lng == 0L;

    }


    /**
     * Long 数组转换为 ,分割的字符串
     */
    public static String join(Collection<Long> longSet) {

        Set<String> tmpSet = new HashSet<>();

        if (CollectionUtils.isEmpty(longSet)) {
            return null;
        }

        longSet.forEach(lng ->
            tmpSet.add(lng.toString()));

        return String.join(",", tmpSet);
    }

    /**
     * Long 数组转换为 ,分割的字符串
     */
    public static String join(List<Long> longList, String delimit) {

        List<String> tmpList = new ArrayList<>();

        if (CollectionUtils.isEmpty(longList)) {
            return null;
        }

        longList.forEach(lng ->
            tmpList.add(lng.toString()));
        String returnStr = String.join(delimit, tmpList);
        if (!returnStr.isEmpty()) return delimit + returnStr + delimit;
        else return delimit;
    }


    public static Set<Long> change2Str(Set<String> tmpEntityIDs) {

        Set<Long> entityIDs = new HashSet<>();


        tmpEntityIDs.forEach(entityId ->
            entityIDs.add(LongUtils.parseLong(entityId)));

        return entityIDs;
    }


    public static List<Long> change2Str(List<String> tmpEntityIDs) {

        List<Long> entityIDs = new ArrayList<>();


        tmpEntityIDs.forEach(entityId ->
            entityIDs.add(LongUtils.parseLong(entityId)));

        return entityIDs;
    }

    public static List<Long> change2Str(String[] tmpEntityIDs) {

        List<Long> entityIDs = new ArrayList<>();

        Arrays.asList(tmpEntityIDs)
            .forEach(entityId ->
                entityIDs.add(LongUtils.parseLong(entityId)));

        return entityIDs;
    }

    public static Long[] change2LongArr(String[] strArr) {
        List<String> strList = Arrays.asList(strArr);
        strList.remove("");
        List<Long> longList = change2Str(strList);

        return longList.toArray(new Long[0]);
    }

    public static List<Long> change2LongArr(String str, String delimit) {
        if (StringUtils.isEmpty(str)) return new ArrayList<>();

        String[] strList = str.split(delimit);

        List<Long> longList = new ArrayList<>();
        for (String item : strList) {
            Long m = parseLong(item);
            if (m != null) {
                longList.add(m);
            }
        }

        return longList;
    }

    public static Long parseLong(String lng) {
        if (lng == null || lng.isEmpty()) {
            return null;
        }

        return Long.parseLong(trim(lng));
    }

    public static String toString(Long longValue) {
        return longValue == null ? null : longValue.toString();
    }
}
