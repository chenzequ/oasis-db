package cn.oasissoft.core.db.utils;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: Quinn
 * @title: 表名辅助方法类
 * @description:
 * @date: 2021-03-21 4:46 下午
 */
public class TableNameUtils {

    // 以数字结尾分表
    // 以年月分表

    // 辅助方法

    /**
     * 获取id分表的表名(取id的最后一位数字)
     * 例: "user_",1008 => "user_8"
     *
     * @param baseTableName 基础表名
     * @param id            id(数字)
     * @return
     */
    public static String getUidTableName(String baseTableName, Integer id) {
        String strUid = id.toString();
        return baseTableName + strUid.charAt(strUid.length() - 1);
    }

    /**
     * 获取根据id(0-9)分表的全部表Union的Sql
     * 例: "(SELECT * FROM user_0 UNION ..... SELECT * FROM user_9) AS T"
     *
     * @param baseViewName
     * @param whereSql
     * @return
     */
    public static String getIdTableName(String baseViewName, String whereSql) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("(");
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                sb.append("SELECT * FROM ").append(baseViewName).append(i);
            } else {
                sb.append(" UNION SELECT * FROM ").append(baseViewName).append(i);
            }
            // 添加where条件
            if (StringUtils.hasText(whereSql)) {
                sb.append(" WHERE ").append(whereSql);
            }
        }
        sb.append(") AS T");
        return sb.toString();

    }

    /**
     * 获取当前年月的分表表名
     * 例: 当前日期是:2021-01-01 => "record_202101"
     *
     * @param baseTableName
     * @return
     */
    public static String getYearMonthTableNameBy(String baseTableName) {
        LocalDateTime now = LocalDateTime.now();
        Integer year = now.getYear() * 100;
        Integer month = now.getMonthValue();
        return getYearMonthTableNameBy(baseTableName, year + month);
    }

    public static String getYearMonthTableNameBy(String baseTableName, Integer yearMonth) {
        return baseTableName + yearMonth;
    }

    public static String getYearMonthTableNameBy(String baseTableName, LocalDateTime dateTime) {
        Integer yearMonth = getYearMonth(dateTime);
        return getYearMonthTableNameBy(baseTableName, yearMonth);
    }

    /**
     * 根据时间戳(毫秒)获取分表表名
     *
     * @param baseTableName
     * @param timestamp
     * @return
     */
    public static String getYearMonthTableNameBy(String baseTableName, Long timestamp) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
        String yearMonth = monthFormat.format(timestamp);
        return baseTableName + yearMonth;
    }

    /**
     * 获取当前月份的年月值
     *
     * @return
     */
    private static Integer getCurrentYearMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() * 100 + now.getMonthValue();
    }

    private static Integer getYearMonth(LocalDate date) {
        return date.getYear() * 100 + date.getMonthValue();
    }

    private static Integer getYearMonth(LocalDateTime date) {
        return date.getYear() * 100 + date.getMonthValue();
    }

}
