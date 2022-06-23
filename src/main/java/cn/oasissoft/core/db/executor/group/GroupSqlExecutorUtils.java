package cn.oasissoft.core.db.executor.group;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.entity.MapEntity;
import cn.oasissoft.core.db.entity.aggregate.AggregatePropertyBase;
import cn.oasissoft.core.db.entity.schema.ColumnSchema;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbException;
import cn.oasissoft.core.db.executor.function.QueryForListFunction;
import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.utils.DbQueryUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GroupBy sql执行器辅助类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/22 10:19
 */
final class GroupSqlExecutorUtils {

    // 分组查询

    /**
     * 分组查询
     * SELECT [groupProps],SUM([column]) AS SUM_[column] FROM [tableName] WHERE [whereSql] GROUP BY [groupProps] ORDER BY [orderSql] LIMIT [LimitCount]
     *
     * @param tableNameSql
     * @param schema
     * @param dbType
     * @param queryForList
     * @param groupProps
     * @param aggregateProperties
     * @param query
     * @param limitCount
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> List<MapEntity> groupBy(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, Set<String> groupProps, AggregatePropertyBase[] aggregateProperties, DbQuery query, int limitCount) {
        Assert.notEmpty(aggregateProperties, "aggregateProperties is empty");

        StringBuilder sql = new StringBuilder(64);
        sql.append("SELECT ");

        boolean supportTop = DatabaseType.supportTop(dbType);
        if (limitCount > 0 && supportTop) {
            sql.append(" TOP ").append(limitCount).append(" ");
        }

        StringBuilder groupPropsSql = new StringBuilder();
        if (groupProps != null) {
            for (String groupProp : groupProps) {
                ColumnSchema column = schema.getColumnByProperty(groupProp);
                if (null != column) {
                    groupPropsSql.append(column.getColumnNameSql()).append(",");
                } else {
                    if (groupProp.indexOf('.') > 0) {
                        groupPropsSql.append(groupProp).append(",");
                    } else {
                        throw new OasisDbException("未知的分组属性[" + groupProp + "]");
                    }
                }
            }
        }
        if (groupPropsSql.length() > 0) {
            sql.append(groupPropsSql);
        }

        for (AggregatePropertyBase prop : aggregateProperties) {
            String opStr;
            switch (prop.getOp()) {
                case Sum:
                    opStr = "SUM";
                    break;
                case Count:
                    opStr = "COUNT";
                    break;
                case Avg:
                    opStr = "AVG";
                    break;
                case Max:
                    opStr = "MAX";
                    break;
                case Min:
                    opStr = "MIN";
                    break;
                default:
                    throw new OasisDbException("未实现的聚合运算符");
            }
            sql.append(opStr).append("(").append(prop.getColumnSqlBy(schema)).append(") AS ").append(prop.getAsName()).append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" FROM ").append(tableNameSql);

        Map<String, Object> params = new HashMap<>();
        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, params);
        String orderSql = DbQueryUtils.getOrderSql(schema, query);

        if (whereSql.length() > 0) {
            sql.append(" WHERE ").append(whereSql);
        }

        if (groupPropsSql.length() > 0) {
            sql.append(" GROUP  BY ").append(groupPropsSql);
        }

        if (orderSql.length() > 0) {
            sql.append(" ORDER BY ").append(orderSql);
        }

        if (limitCount > 0 && !supportTop) {
            sql.append(" LIMIT ").append(limitCount);
        }

        return queryForList.apply(sql.toString(), params).stream().map(MapEntity::new).collect(Collectors.toList());
    }

    // 按时间分组查询
    public static final String GROUP_TIME_AS_NAME = "DT";
    public static final String GROUP_TIME_ID_NAME = "_timeId";
    public static final String GROUP_TIME_NAME = "_time";

    /**
     * 按时间分组查询
     * SELECT [TimeUnit([TimeProperty])] AS DT,[groupProps] ,SUM([Column]) AS SUM_[Column] FROM [TableName] WHERE [whereSql] GROUP BY DT,[groupProps] ORDER BY [orderSql]
     *
     * @param tableNameSql
     * @param schema
     * @param dbType
     * @param queryForList
     * @param groupProps          分组属性
     * @param aggregateProperties 聚合属性
     * @param timeProperty        时间属性
     * @param timeUnit            时间单位
     * @param num                 时间间隔数量
     * @param beginTime           开始时间(为空时,选择从当前时间向前取指定数量时间)
     * @param query
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> List<MapEntity> groupTimeBy(String tableNameSql, TableSchema<T> schema, DatabaseType dbType, QueryForListFunction queryForList, Set<String> groupProps, AggregatePropertyBase[] aggregateProperties, String timeProperty, TimeUnit timeUnit, int num, LocalDateTime beginTime, DbQuery query) {
        Assert.hasText(timeProperty, "timeProperty is blank.");
        Assert.notEmpty(aggregateProperties, "aggregateProperties is empty.");
        if (num < 1) {
            throw new OasisDbException("num的值必须大于1");
        }

        LocalDateTime begin = null; // 开始时间
        if (null != beginTime) {
            // 将时间转化为 UTF+8的时间
            begin = beginTime;
        }

        // 获取时间列的sql表达方式
        String timeColumnSql;
        ColumnSchema timeColumn = schema.getColumnByProperty(timeProperty);
        if (null == timeColumn) {
            if (timeProperty.indexOf('.') > 0) {
                timeColumnSql = timeProperty;
            } else {
                throw new OasisDbException("无效的时间属性[" + timeProperty + "]");
            }
        } else {
            timeColumnSql = timeColumn.getColumnNameSql();
        }

        // 开始拼接
        StringBuilder sql = new StringBuilder(128);
        // 拼接时间列
        sql.append("SELECT ").append(TimeUnit.getDateFormatStringBy(timeColumnSql, timeUnit)).append(" AS ").append(GROUP_TIME_AS_NAME).append(",");
        // 拼接属性列
        StringBuilder groupPropsSql = new StringBuilder();
        if (groupProps != null) {
            for (String groupProp : groupProps) {
                ColumnSchema column = schema.getColumnByProperty(groupProp);
                if (null != column) {
                    groupPropsSql.append(column.getColumnNameSql()).append(",");
                } else {
                    if (groupProp.indexOf('.') > 0) {
                        groupPropsSql.append(groupProp).append(",");
                    } else {
                        throw new OasisDbException("未知的分组属性[" + groupProp + "]");
                    }
                }
            }
        }
        if (groupPropsSql.length() > 0) {
            sql.append(groupPropsSql);
        }

        // 拼接聚合属性
        for (AggregatePropertyBase prop : aggregateProperties) {
            String opStr;
            switch (prop.getOp()) {
                case Sum:
                    opStr = "SUM";
                    break;
                case Count:
                    opStr = "COUNT";
                    break;
                case Avg:
                    opStr = "AVG";
                    break;
                case Max:
                    opStr = "MAX";
                    break;
                case Min:
                    opStr = "MIN";
                    break;
                default:
                    throw new OasisDbException("未实现的聚合运算符");
            }
            sql.append(opStr).append("(").append(prop.getColumnSqlBy(schema)).append(") AS ").append(prop.getAsName()).append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        // 拼接表名
        sql.append(" FROM ").append(tableNameSql);

        Map<String, Object> params = new HashMap<>();
        String whereSql = DbQueryUtils.getWhereSql(dbType, schema, query, params);
        String orderSql = DbQueryUtils.getOrderSql(schema, query);
        // 拼接 Where
        if (whereSql.length() > 0) {
            sql.append(" WHERE ").append(whereSql);
        }

        // 拼接 group by
        sql.append(" GROUP BY ").append(GROUP_TIME_AS_NAME);
        if (groupPropsSql.length() > 0) {
            sql.append(",").append(groupPropsSql);
        }
        // 拼接 order by
        sql.append(" ORDER BY ").append(GROUP_TIME_AS_NAME);
        if (orderSql.length() > 0) {
            sql.append(",").append(orderSql);
        }

        // 查询结果
        List<Map<String, Object>> maps = queryForList.apply(sql.toString(), params);
        // 由于查询出来的时间可能有断层，所以需要补偿时间断层
        // 例如 3-4 100 , 3-7 200 => 需要补充 3-5 0,3-6 0 等数据
        List<MapEntity> results = new ArrayList<>(maps.size());
        TimeUnitName[] timeNames = TimeUnit.getTimeNames(begin, timeUnit, num);
        // 临时表
        Map<String, MapEntity> tmpMap = new HashMap<>();
        // 按时间顺序填充默认值
        for (int i = 0; i < num; i++) {
            MapEntity mapEntity = new MapEntity();
            mapEntity.put(GROUP_TIME_ID_NAME, timeNames[i].getName());
            mapEntity.put(GROUP_TIME_NAME, timeNames[i].getDateTime());
            for (int j = 0; j < aggregateProperties.length; j++) {
                mapEntity.put(aggregateProperties[j].getAsName(), aggregateProperties[j].getDefaultValue());
            }
            results.add(mapEntity);
            tmpMap.put(timeNames[i].getName(), mapEntity);
        }

        // 将数据库查询结果填充到返回结果中
        for (Map<String, Object> map : maps) {
            String dt = map.get(GROUP_TIME_AS_NAME).toString();
            MapEntity vo = tmpMap.get(dt);
            for (int i = 0; i < aggregateProperties.length; i++) {
                String asName = aggregateProperties[i].getAsName();
                vo.put(asName, map.get(asName));
            }
        }

        return results;
    }
}
