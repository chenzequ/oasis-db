package cn.oasissoft.core.db.executor.ddl;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.ex.OasisDbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Quinn
 * @title: 列定义映射类
 * @description:
 * @date: 2021-03-22 10:59 上午
 */
public class ColumnDefinitionMap {

    private final Map<String, ColumnDefinition> inner = new HashMap<>();
    // 组合索引
    private final List<DBCombineIndex[]> combineIndexes = new ArrayList<>();

    public ColumnDefinition get(String property) {
        return inner.get(property);
    }

    public void put(String property, ColumnDefinition columnDefinition) {
        inner.put(property, columnDefinition);
    }

    public ColumnDefinitionMap update(String property, int length) {
        if (inner.containsKey(property)) {
            inner.get(property).setLength(length);
        } else {
            throw new OasisDbDefineException("属性[" + property + "]没有提前定义");
        }
        return this;
    }

    public ColumnDefinitionMap update(String property, DatabaseType dbType, String typeStr) {
        if (inner.containsKey(property)) {
            inner.get(property).addCustomType(dbType, typeStr);
        } else {
            throw new OasisDbException("属性[" + property + "]没有提前定义");
        }
        return this;
    }

    public ColumnDefinitionMap updateByMySql(String property, String typeStr) {
        return this.update(property, DatabaseType.MySql, typeStr);
    }

    public ColumnDefinitionMap updateDecimalByMySql(String property) {
        return this.updateDecimalByMySql(property, 18, 6);
    }

    public ColumnDefinitionMap updateDecimalByMySql(String property, Integer length, Integer decimal) {
        return this.updateByMySql(property, "decimal(" + length + "," + decimal + ")");
    }

    public ColumnDefinitionMap updateTinyIntByMySql(String property) {
        return this.updateByMySql(property, "tinyint(1)");
    }

    /**
     * 添加组合索引
     *
     * @param properties
     * @return
     */
    public ColumnDefinitionMap addCombineIndex(DBCombineIndex... properties) {
        if (properties == null || properties.length < 2) {
            throw new OasisDbException("组合索引必须有两个以上的属性");
        }

        for (DBCombineIndex combineIndex : properties) {
            if (!inner.containsKey(combineIndex.getProperty())) {
                throw new OasisDbException("必须先配置字段属性,才能设置组合索引属性. - [" + combineIndex.getProperty() + "]未找到");
            }
        }

        this.combineIndexes.add(properties);

        return this;
    }

    /**
     * 获取当前全部的组合索引属性
     *
     * @return
     */
    public List<DBCombineIndex[]> getAllCombineIndexes() {
        return this.combineIndexes;
    }

    /**
     * 获取全部索引
     *
     * @return
     */
    public Map<String, DBIndex> getAllIndexes() {
        Map<String, DBIndex> indexMap = new HashMap<>();
        for (Map.Entry<String, ColumnDefinition> entry : inner.entrySet()) {
            if (entry.getValue().getIndex() != null) {
                indexMap.put(entry.getKey(), entry.getValue().getIndex());
            }
        }
        return indexMap;
    }
}
