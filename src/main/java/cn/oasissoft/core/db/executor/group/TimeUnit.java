package cn.oasissoft.core.db.executor.group;

import cn.oasissoft.core.db.ex.OasisDbDefineException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: Quinn
 * @title: 时间单位类
 * @description:
 * @date: 2021-03-21 8:47 下午
 */
public enum TimeUnit {
    Year,
    Month,
    Day,
    Hour,
    Minute,
    Second;

    public static String getDateFormatStringBy(String timeField, TimeUnit timeUnit) {
        switch (timeUnit) {
            case Year:
                return "DATE_FORMAT(" + timeField + ",'%Y')";
            case Month:
                return "DATE_FORMAT(" + timeField + ",'%Y-%m')";
            case Day:
                return "DATE_FORMAT(" + timeField + ",'%Y-%m-%d')";
            case Hour:
                return "DATE_FORMAT(" + timeField + ",'%Y-%m-%d %H')";
            case Minute:
                return "DATE_FORMAT(" + timeField + ",'%Y-%m-%d %H:%I')";
            case Second:
                return "DATE_FORMAT(" + timeField + ",'%Y-%m-%d %H:%I:%S')";
            default:
                throw new RuntimeException("不支持的运算符");
        }
    }

    /**
     * 获取指定条件的时间单位名称数组
     *
     * @param beginTime 开始时间(为空时，代表从当前时间之前取num数量的timeUnit)
     * @param timeUnit  时间单位
     * @param num       时间单位间隔数量
     * @return
     */
    public static TimeUnitName[] getTimeNames(LocalDateTime beginTime, TimeUnit timeUnit, Integer num) {
        boolean isNow = beginTime == null;
        // 开始计数的时间
        LocalDateTime time = isNow ? LocalDateTime.now() : beginTime;

        TimeUnitName[] timeUnitNames = new TimeUnitName[num];
        for (int i = 0; i < num; i++) {
            TimeUnitName timeUnitName = new TimeUnitName();
            timeUnitNames[i] = timeUnitName;
            switch (timeUnit) {
                case Year:
                    int year = isNow ? time.minusYears(i).getYear() : time.plusYears(i).getYear();
                    timeUnitName.setName(year + "");
                    timeUnitName.setDateTime(LocalDateTime.of(year, 1, 1, 0, 0));
                    break;
                case Month:
                    LocalDateTime dtMonths = isNow ? time.minusMonths(i) : time.plusMonths(i);
                    String monthStr = getNumStr(dtMonths.getMonthValue());
                    timeUnitName.setName(dtMonths.getYear() + "-" + monthStr);
                    timeUnitName.setDateTime(LocalDateTime.of(dtMonths.getYear(), dtMonths.getMonthValue(), 1, 0, 0));
                    break;
                case Day:
                    LocalDateTime dtDays = isNow ? time.minusDays(i) : time.plusDays(i);
                    String dayMonthStr = getNumStr(dtDays.getMonthValue());
                    String dayStr = getNumStr(dtDays.getDayOfMonth());
                    timeUnitName.setName(dtDays.getYear() + "-" + dayMonthStr + "-" + dayStr);
                    timeUnitName.setDateTime(LocalDateTime.of(dtDays.getYear(), dtDays.getMonthValue(), dtDays.getDayOfMonth(), 0, 0));
                    break;
                case Hour:
                    LocalDateTime dtHours = isNow ? time.minusHours(i) : time.plusHours(i);
                    String hourMonthStr = getNumStr(dtHours.getMonthValue());
                    String hourDayStr = getNumStr(dtHours.getDayOfMonth());
                    String hourStr = getNumStr(dtHours.getHour());
                    timeUnitName.setName(dtHours.getYear() + "-" + hourMonthStr + "-" + hourDayStr + " " + hourStr);
                    timeUnitName.setDateTime(LocalDateTime.of(dtHours.getYear(), dtHours.getMonthValue(), dtHours.getDayOfMonth(), dtHours.getHour(), 0));
                    break;
                case Minute:
                    LocalDateTime dtMinute = isNow ? time.minusHours(i) : time.plusMinutes(i);
                    String minuteMonthStr = getNumStr(dtMinute.getMonthValue());
                    String minuteDayStr = getNumStr(dtMinute.getDayOfMonth());
                    String minuteHourStr = getNumStr(dtMinute.getHour());
                    String minuteStr = getNumStr(dtMinute.getMinute());
                    timeUnitName.setName(dtMinute.getYear() + "-" + minuteMonthStr + "-" + minuteDayStr + " " + minuteHourStr + ":" + minuteStr);
                    timeUnitName.setDateTime(LocalDateTime.of(dtMinute.getYear(), dtMinute.getMonthValue(), dtMinute.getDayOfMonth(), dtMinute.getHour(), dtMinute.getMinute()));
                    break;
                case Second:
                    LocalDateTime dtSecond = isNow ? time.minusSeconds(i) : time.plusSeconds(i);
                    String secondMonthStr = getNumStr(dtSecond.getMonthValue());
                    String secondDayStr = getNumStr(dtSecond.getDayOfMonth());
                    String secondHourStr = getNumStr(dtSecond.getHour());
                    String secondMinuteStr = getNumStr(dtSecond.getMinute());
                    String secondStr = getNumStr(dtSecond.getSecond());
                    timeUnitName.setName(dtSecond.getYear() + "-" + secondMonthStr + "-" + secondDayStr + " " + secondHourStr + ":" + secondMinuteStr + ":" + secondStr);
                    timeUnitName.setDateTime(LocalDateTime.of(dtSecond.getYear(), dtSecond.getMonthValue(), dtSecond.getDayOfMonth(), dtSecond.getHour(), dtSecond.getMinute(), dtSecond.getSecond()));
                    break;
                default:
                    throw new OasisDbDefineException("未实现在的时间单位");
            }
        }

        if (isNow) {
            // 数组反转
            List<TimeUnitName> list = Arrays.asList(timeUnitNames);
            Collections.reverse(list);
            return list.toArray(new TimeUnitName[0]);
        } else {
            return timeUnitNames;
        }
    }

    // 返回时间数字字符串,不足2位前面+0
    private static String getNumStr(int num) {
        String numStr = num + "";
        if (numStr.length() < 2) {
            return "0" + numStr;
        } else {
            return numStr;
        }
    }
}
