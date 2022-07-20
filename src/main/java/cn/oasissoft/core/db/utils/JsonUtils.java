package cn.oasissoft.core.db.utils;

import cn.oasissoft.core.db.ex.OasisJsonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Json序列化辅助
 *
 * @author Quinn
 * @desc
 * @time 2022/7/20
 */
public class JsonUtils {
    private static ObjectMapper object_mapper = null;

    private static ObjectMapper getObjectMapper() {
        if (object_mapper == null) {
            object_mapper = new ObjectMapper();
        }
        return object_mapper;
    }

    public static void initObjectMapper(ObjectMapper objectMapper) {
        object_mapper = objectMapper;
    }

    /**
     * 转换转成Json字符串
     *
     * @param value
     * @return
     */
    public static String toJson(Object value) {
        return toJson(getObjectMapper(), value);
    }

    /**
     * 转换转成Json字符串
     *
     * @param value
     * @return
     */
    public static String toJson(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new OasisJsonException("json processing error:" + e.getMessage());
        }
    }

    /**
     * json字符串转成指定类型对象
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return fromJson(getObjectMapper(), json, tClass);
    }

    public static <T> T fromJson(ObjectMapper objectMapper, String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (IOException e) {
            throw new OasisJsonException("read json to target error :" + e.getMessage());
        }
    }

    /**
     * json字符串转成指定类型的集合
     *
     * @param json
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> List<T> listFromJson(String json, Class<T> beanClass) {
        return listFromJson(getObjectMapper(), json, beanClass);
    }

    /**
     * json字符串转成指定类型的集合
     *
     * @param json
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> List<T> listFromJson(ObjectMapper objectMapper, String json, Class<T> beanClass) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, beanClass);
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new OasisJsonException("read json to target list error:" + e.getMessage());
        }
    }


}
