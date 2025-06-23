package uitls;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 数据类型工具类
 * @Date 2025/6/19
 * @Author LoveYue
 */
public class DataTypeUtil {

    private DataTypeUtil() {
    }

    private static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
            Boolean.TYPE,
            Byte.TYPE,
            Character.TYPE,
            Double.TYPE,
            Float.TYPE,
            Integer.TYPE,
            Long.TYPE,
            Short.TYPE
    ));

    /**
     * 判断是否是基本数据类型
     *
     * @param value 对象
     * @return 是否是基本数据类型
     */
    public static boolean isPrimitiveType(Object value) {
        return value == null || PRIMITIVE_TYPES.contains(value.getClass());
    }
}
