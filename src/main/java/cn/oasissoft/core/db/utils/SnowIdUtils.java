package cn.oasissoft.core.db.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 雪花id生成器辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/20 23:16
 */
public class SnowIdUtils {
    // 全局唯一雪花算法id生成器
    private static final SnowId SNOW_ID = new SnowId();

    /**
     * 生成下一个id
     *
     * @return
     */
    public static long nextId() {
        return SNOW_ID.nextId();
    }

    /**
     * 反向推算出日期
     *
     * @param id
     * @return
     */
    public static Date getDateById(Long id, Long twepoch) {
        return new Date((id >> 22) + twepoch);
    }

    /**
     * 反向推算日期
     * @param id
     * @return
     */
    public static Date getDateById(Long id) {
        return getDateById(id, SnowId.TWEPOCH);
    }

    /**
     * 雪花id 反向推算出时间
     *
     * @param id
     * @return
     */
    public static LocalDateTime getDateTimeById(Long id, Long twepoch) {
        Date dt = getDateById(id, twepoch);
        Instant instant = dt.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static LocalDateTime getDateTimeById(Long id) {
        return getDateTimeById(id, SnowId.TWEPOCH);
    }
}
