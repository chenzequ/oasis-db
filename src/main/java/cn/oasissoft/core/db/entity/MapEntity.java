package cn.oasissoft.core.db.entity;

import cn.oasissoft.core.db.utils.MapUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * map 实体
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 20:19
 */
public class MapEntity extends HashMap<String, Object> {

    public MapEntity() {
    }

    public MapEntity(Map<String, Object> map) {
        super(map);
    }

    public String getString(String key) {
        return MapUtils.getValue(this, String.class, key);
    }

    public String getString(String key, String defaultValue) {
        return MapUtils.getValue(this, String.class, key, defaultValue);
    }

    public String tryGetString(String key) {
        return MapUtils.tryGetValue(this, String.class, key, Object::toString);
    }

    public String tryGetString(String key, Function<Object, String> convertFunc) {
        return MapUtils.tryGetValue(this, String.class, key, convertFunc);
    }

    public Byte getByte(String key) {
        return MapUtils.getValue(this, Byte.class, key);
    }

    public Byte getByte(String key, Byte defaultValue) {
        return MapUtils.getValue(this, Byte.class, key, defaultValue);
    }

    public Byte tryGetByte(String key) {
        return MapUtils.tryGetValue(this, Byte.class, key, o -> Byte.parseByte(o.toString()));
    }

    public Byte tryGetByte(String key, Function<Object, Byte> convertFunc) {
        return MapUtils.tryGetValue(this, Byte.class, key, convertFunc);
    }

    public Short getShort(String key) {
        return MapUtils.getValue(this, Short.class, key);
    }

    public Short getShort(String key, Short defaultValue) {
        return MapUtils.getValue(this, Short.class, key, defaultValue);
    }

    public Short tryGetShort(String key) {
        return MapUtils.tryGetValue(this, Short.class, key, o -> Short.parseShort(o.toString()));
    }

    public Short tryGetShort(String key, Function<Object, Short> convertFunc) {
        return MapUtils.tryGetValue(this, Short.class, key, convertFunc);
    }

    public Integer getInteger(String key) {
        return MapUtils.getValue(this, Integer.class, key);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return MapUtils.getValue(this, Integer.class, key, defaultValue);
    }

    public Integer tryGetInteger(String key) {
        return MapUtils.tryGetValue(this, Integer.class, key, o -> Integer.parseInt(o.toString()));
    }

    public Integer tryGetInteger(String key, Function<Object, Integer> convertFunc) {
        return MapUtils.tryGetValue(this, Integer.class, key, convertFunc);
    }

    public Long getLong(String key) {
        return MapUtils.getValue(this, Long.class, key);
    }

    public Long getLong(String key, Long defaultValue) {
        return MapUtils.getValue(this, Long.class, key, defaultValue);
    }

    public Long tryGetLong(String key) {
        return MapUtils.tryGetValue(this, Long.class, key, o -> Long.parseLong(o.toString()));
    }

    public Long tryGetLong(String key, Function<Object, Long> convertFunc) {
        return MapUtils.tryGetValue(this, Long.class, key, convertFunc);
    }

    public Float getFloat(String key) {
        return MapUtils.getValue(this, Float.class, key);
    }

    public Float getFloat(String key, Float defaultValue) {
        return MapUtils.getValue(this, Float.class, key, defaultValue);
    }

    public Float tryGetFloat(String key) {
        return MapUtils.tryGetValue(this, Float.class, key, o -> Float.parseFloat(o.toString()));
    }

    public Float tryGetFloat(String key, Function<Object, Float> convertFunc) {
        return MapUtils.tryGetValue(this, Float.class, key, convertFunc);
    }

    public Double getDouble(String key) {
        return MapUtils.getValue(this, Double.class, key);
    }

    public Double getDouble(String key, Double defaultValue) {
        return MapUtils.getValue(this, Double.class, key, defaultValue);
    }

    public Double tryGetDouble(String key) {
        return MapUtils.tryGetValue(this, Double.class, key, o -> Double.parseDouble(o.toString()));
    }

    public Double tryGetDouble(String key, Function<Object, Double> convertFunc) {
        return MapUtils.tryGetValue(this, Double.class, key, convertFunc);
    }

    public Boolean getBoolean(String key) {
        return MapUtils.getValue(this, Boolean.class, key);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return MapUtils.getValue(this, Boolean.class, key, defaultValue);
    }

    public Boolean tryGetBoolean(String key) {
        return MapUtils.tryGetValue(this, Boolean.class, key, o -> Boolean.valueOf(o.toString()));
    }

    public Boolean tryGetBoolean(String key, Function<Object, Boolean> convertFunc) {
        return MapUtils.tryGetValue(this, Boolean.class, key, convertFunc);
    }

    public BigInteger getBigInteger(String key) {
        return MapUtils.getValue(this, BigInteger.class, key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return MapUtils.getValue(this, BigInteger.class, key, defaultValue);
    }

    public BigInteger tryGetBigInteger(String key) {
        return MapUtils.tryGetValue(this, BigInteger.class, key, o -> new BigInteger(o.toString()));
    }

    public BigInteger tryGetBigInteger(String key, Function<Object, BigInteger> convertFunc) {
        return MapUtils.tryGetValue(this, BigInteger.class, key, convertFunc);
    }

    public BigDecimal getBigDecimal(String key) {
        return MapUtils.getValue(this, BigDecimal.class, key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return MapUtils.getValue(this, BigDecimal.class, key, defaultValue);
    }

    public BigDecimal tryGetBigDecimal(String key) {
        return MapUtils.tryGetValue(this, BigDecimal.class, key, o -> new BigDecimal(o.toString()));
    }

    public BigDecimal tryGetBigDecimal(String key, Function<Object, BigDecimal> convertFunc) {
        return MapUtils.tryGetValue(this, BigDecimal.class, key, convertFunc);
    }

    public LocalDateTime getLocalDateTime(String key) {
        return MapUtils.getLocalDateTime(this, key);
    }

    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultValue) {
        return MapUtils.getLocalDateTime(this, key, defaultValue);
    }

    public LocalDateTime tryGetLocalDateTime(String key, DateTimeFormatter formatter) {
        return MapUtils.tryGetLocalDateTime(this, key, formatter);
    }

    public LocalDateTime tryGetLocalDateTime(String key) {
        return this.tryGetLocalDateTime(key, (DateTimeFormatter) null);
    }

    public LocalDateTime tryGetLocalDateTime(String key, Function<Object, LocalDateTime> convertFunc) {
        return MapUtils.tryGetValue(this, LocalDateTime.class, key, convertFunc);
    }

    public LocalDate getLocalDate(String key) {
        return MapUtils.getLocalDate(this, key);
    }

    public LocalDate getLocalDate(String key, LocalDate defaultValue) {
        return MapUtils.getLocalDate(this, key, defaultValue);
    }

    public LocalDate tryGetLocalDate(String key, DateTimeFormatter formatter) {
        return MapUtils.tryGetLocalDate(this, key, formatter);
    }

    public LocalDate tryGetLocalDate(String key) {
        return this.tryGetLocalDate(key, (DateTimeFormatter) null);
    }

    public LocalDate tryGetLocalDate(String key, Function<Object, LocalDate> convertFunc) {
        return MapUtils.tryGetValue(this, LocalDate.class, key, convertFunc);
    }

    public <T> List<T> getList(Class<T> tClass, String key) {
        return MapUtils.getArrayValue(this, tClass, key);
    }
}
