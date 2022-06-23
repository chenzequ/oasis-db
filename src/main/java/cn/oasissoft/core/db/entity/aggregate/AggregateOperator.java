package cn.oasissoft.core.db.entity.aggregate;

/**
 * 集合函数枚举
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 14:10
 */
public enum AggregateOperator {

    /**
     * 求总和(返回值BigDecimal)
     */
    Sum,
    /**
     * 求平均值(返回值BigDecimal)
     */
    Avg,
    /**
     * 求最大值(默认值为null)
     */
    Max,
    /**
     * 求最小值(默认值为null)
     */
    Min,
    /**
     * 求数量(Long)(默认值为0L)
     */
    Count,
    /**
     * 获取单个对象
     */
    Single;

    public static String getOpSql(AggregateOperator op) {
        switch (op) {
            case Count:
                return "COUNT";
            case Sum:
                return "SUM";
            case Avg:
                return "AVG";
            case Max:
                return "MAX";
            case Min:
                return "MIN";
            default:
                return "";
        }
    }

}
