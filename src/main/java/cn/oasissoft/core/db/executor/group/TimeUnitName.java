package cn.oasissoft.core.db.executor.group;

import java.time.LocalDateTime;

/**
 * @author: Quinn
 * @title: 时间单位名称类
 * @description:
 * @date: 2021-03-22 8:55 上午
 */
public class TimeUnitName {

    private String name;
    private LocalDateTime dateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
