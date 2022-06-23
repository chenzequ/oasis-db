package cn.oasissoft.core.db.utils;

import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.ex.OasisDbException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/18 20:27
 */
public class MapUtils {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static <T> List<T> getArrayValue(Map map, Class<T> tClass, String key) {
        if (map.containsKey(key)) {
            return (List<T>) map.get(key);
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }

    public static <T> T getValue(Map map, Class<T> tClass, String key) {
        if (map.containsKey(key)) {
            return (T) map.get(key);
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }

    public static <T> T getValue(Map map, Class<T> tClass, String key, T defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                return (T) value;
            }
        }
        return defaultValue;
    }

    public static <T> T tryGetValue(Map map, Class<T> tClass, String key, Function<Object, T> convertFunc) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value == null) {
                return null;
            } else {
                return convertFunc.apply(map.get(key));
            }
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }

    public static <T> T tryGetValue(Map map, Class<T> tClass, String key, Function<Object, T> convertFunc, T defaultValue) {
        if (map.containsKey(key)) {
            try {
                Object value = map.get(key);
                if (value == null) {
                    return defaultValue;
                } else {
                    return convertFunc.apply(map.get(key));
                }
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static LocalDateTime getLocalDateTime(Map map, String key) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof Timestamp) {
                    return ((Timestamp) value).toLocalDateTime();
                } else {
                    return (LocalDateTime) value;
                }
            } else {
                return null;
            }
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }

    public static LocalDate getLocalDate(Map map, String key) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                // ???
                if (value instanceof Timestamp) {
                    return ((Timestamp) value).toLocalDateTime().toLocalDate();
                } else {
                    return (LocalDate) value;
                }
            } else {
                return null;
            }
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }

    public static LocalDateTime getLocalDateTime(Map map, String key, LocalDateTime defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof Timestamp) {
                    return ((Timestamp) value).toLocalDateTime();
                } else {
                    return (LocalDateTime) value;
                }
            }
        }
        return defaultValue;
    }

    public static LocalDate getLocalDate(Map map, String key, LocalDate defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof Timestamp) {
                    return ((Timestamp) value).toLocalDateTime().toLocalDate();
                } else {
                    return (LocalDate) value;
                }
            }
        }
        return defaultValue;
    }

    public static LocalDateTime tryGetLocalDateTime(Map map, String key, DateTimeFormatter formatter) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            return localDateTimeConvert(value, formatter);
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }

    public static LocalDate tryGetLocalDate(Map map, String key, DateTimeFormatter formatter) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            return localDateConvert(value, formatter);
        } else {
            throw new OasisDbDefineException(String.format("key [%s] 没有找到", key));
        }
    }


    /**
     * 将未知数据类型转换成LocalDateTime对象
     *
     * @param value
     * @param formatter
     * @return
     */
    public static LocalDateTime localDateTimeConvert(Object value, DateTimeFormatter formatter) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            if ("".equals(value)) {
                throw new OasisDbException(value + ":转换成日期格式错误");
            } else {
                DateTimeFormatter dtFormatter = formatter == null ? DEFAULT_DATE_TIME_FORMATTER : formatter;
                try {
                    return LocalDateTime.parse(value.toString(), dtFormatter);
                } catch (Exception e) {
                    throw new OasisDbException(value + ":转换成日期格式错误");
                }
            }
        } else if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        } else if (value instanceof Date) {
            return LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else {
            throw new OasisDbException(value + ":转换成日期格式错误");
        }
    }

    /**
     * 将未知数据类型转换成LocalDate对象
     *
     * @param value
     * @param formatter
     * @return
     */
    public static LocalDate localDateConvert(Object value, DateTimeFormatter formatter) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            if ("".equals(value)) {
                throw new OasisDbException(value + ":转换成日期格式错误");
            } else {
                DateTimeFormatter dtFormatter = formatter == null ? DEFAULT_DATE_FORMATTER : formatter;
                try {
                    return LocalDate.parse(value.toString(), dtFormatter);
                } catch (Exception e) {
                    throw new OasisDbException(value + ":转换成日期格式错误");
                }
            }
        } else if (value instanceof LocalDate) {
            return (LocalDate) value;
        } else if (value instanceof Date) {
            return (LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault())).toLocalDate();
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime().toLocalDate();
        } else {
            throw new OasisDbException(value + ":转换成日期格式错误");
        }
    }
}
