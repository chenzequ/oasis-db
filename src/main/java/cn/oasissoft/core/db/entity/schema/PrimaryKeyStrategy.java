package cn.oasissoft.core.db.entity.schema;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 主键生成策略
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 15:30
 */
public enum PrimaryKeyStrategy {
    None, // 没有
    AutoIncrement(Integer.class, Long.class, Short.class, Byte.class), // 自增
    SnowId(Long.class), // 雪花id
    UUID(String.class) // UUID
    ;

    PrimaryKeyStrategy(Type... allowTypes) {
        // 为空时表示任意类型有效
        this.allowTypes = allowTypes;
    }

    private final Type[] allowTypes;

    /**
     * 判断类型是否是有效的主键类型
     *
     * @param type
     * @return
     */
    public boolean isValid(Type type) {
        if (allowTypes.length > 0) {
            return Arrays.stream(allowTypes).anyMatch(t -> t.equals(type));
        } else {
            return true;
        }
    }
}
