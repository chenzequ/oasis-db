package cn.oasissoft.core.db.executor.ddl;

import cn.oasissoft.core.db.entity.DatabaseType;
import cn.oasissoft.core.db.ex.OasisDbDefineException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Quinn
 * @title: 列定义类
 * @description:
 * @date: 2021-03-22 10:34 上午
 */
public class ColumnDefinition {

    private boolean notNull; // 列是否允许为null
    private String comment; // 列注释
    private int length; // 长度
    private DBIndex index; // 列索引
    private final Map<DatabaseType, String> customTypes; // 自定义列描述

    // 检查索引长度是否有效
    private void checkIndexLength() {
        if (index != null && index.getLength() != -1 && length != -1 && index.getLength() > length) {
            throw new OasisDbDefineException("索引长度不允许大于字段长度");
        }
    }

    public void checkIndexLength(int indexLength) {
        if (indexLength != -1 && length != -1 && indexLength > length) {
            throw new OasisDbDefineException("索引长度不允许大于字段长度");
        }
    }

    public ColumnDefinition() {
        this(false, "", -1, null);
    }

    public ColumnDefinition(boolean required, String comment) {
        this(required, comment, -1, null);
    }

    public ColumnDefinition(boolean required, String comment, int length) {
        this(required, comment, length, null);
    }

    public ColumnDefinition(boolean required, String comment, DBIndex columnIndex) {
        this(required, comment, -1, columnIndex);
    }

    public ColumnDefinition(boolean required, String comment, int length, DBIndex columnIndex) {
        this.notNull = required;
        this.comment = comment;
        this.length = length;
        this.index = columnIndex;
        checkIndexLength();
        this.customTypes = new HashMap<>();
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
        checkIndexLength();
    }

    public DBIndex getIndex() {
        return index;
    }

    public void setIndex(DBIndex index) {
        this.index = index;
        checkIndexLength();
    }

    public Map<DatabaseType, String> getCustomTypes() {
        return customTypes;
    }

    public void addCustomType(DatabaseType dbType, String typeStr) {
        if (this.customTypes.containsKey(dbType)) {
            this.customTypes.remove(dbType);
        }
        this.customTypes.put(dbType, typeStr);
    }

    public String getTypeString(DatabaseType dbType) {
        if (customTypes.containsKey(dbType)) {
            return this.customTypes.get(dbType);
        } else {
            return "";
        }
    }
}
