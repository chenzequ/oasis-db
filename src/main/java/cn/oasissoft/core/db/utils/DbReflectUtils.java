package cn.oasissoft.core.db.utils;

import cn.oasissoft.core.db.ex.OasisDbException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反射相关辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 14:57
 */
public final class DbReflectUtils {

    /**
     * 获取全部属性
     *
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class<?> tClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = tClass;
        while (!Object.class.equals(currentClass)) {
            fields.addAll(0, Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取全部 private 属性
     *
     * @param tClass
     * @return
     */
    public static List<Field> getAllPrivateFields(Class<?> tClass) {
        List<Field> result = new ArrayList<>();
        for (Field field : getAllDeclaredFields(tClass)) {
            if (field.getModifiers() == Modifier.PRIVATE) {
                result.add(field);
            }
        }
        return result;
    }

    public static Map<String, Field> getAllPrivateFieldsMap(Class<?> tClass) {
        List<Field> fields = getAllPrivateFields(tClass);
        Map<String, Field> map = new HashMap<>(fields.size());
        fields.forEach(f -> map.put(f.getName(), f));
        return map;
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void setValue(Field field, Object target, Object propertyValue) {
        field.setAccessible(true);
        try {
            if (propertyValue != null) {
                Type type = field.getType();
                if (LocalDateTime.class.equals(type)) {
                    if (propertyValue.getClass().equals(LocalDateTime.class)) {
                        field.set(target, propertyValue);
                    } else if (Timestamp.class.equals(propertyValue.getClass())) {
                        Timestamp ts = (Timestamp) propertyValue;
                        field.set(target, ts.toLocalDateTime());
                    } else {
                        throw new OasisDbException(String.format("value [%s] convert to LocalDateTime error.", propertyValue));
                    }
                } else if (Short.class.equals(type)) {
                    field.set(target, Short.valueOf(propertyValue.toString()));
                } else if (Byte.class.equals(type)) {
                    field.set(target, Byte.valueOf(propertyValue.toString()));
                } else if (Float.class.equals(type)) {
                    field.set(target, Float.valueOf(propertyValue.toString()));
                } else if (BigInteger.class.equals(type)) {
                    field.set(target, new BigInteger(propertyValue.toString()));
                } else if (LocalDate.class.equals(type)) {
                    field.set(target, ((java.sql.Date) propertyValue).toLocalDate());
                } else if (LocalTime.class.equals(type)) {
                    field.set(target, ((java.sql.Time) propertyValue).toLocalTime());
                } else if (Date.class.equals(type)) {
                    if (propertyValue.getClass().equals(LocalDateTime.class)) {
                        LocalDateTime ldt = (LocalDateTime) propertyValue;
                        field.set(target, Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
                    } else {
                        throw new OasisDbException(String.format("value [%s]-[%s] convert to Date error.", propertyValue, propertyValue.getClass()));
                    }
                } else if (((Class) type).isEnum()) {
                    field.set(target, ((Class) type).getEnumConstants()[(int) propertyValue]);
                } else {
                    field.set(target, propertyValue);
                }
            } else {
                field.set(target, null);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getValue(Field field, Object target) {
        try {
            Object value = field.get(target);
            if (value == null) {
                return null;
            }
            if (field.getType().isEnum()) {
                Enum enumValue = (Enum) value;
                return enumValue.ordinal();
            } else {
                return value;
            }
        } catch (IllegalAccessException e) {
            throw new OasisDbException(String.format("property [%s] read value error.", field.getName()));
        }
    }

}
