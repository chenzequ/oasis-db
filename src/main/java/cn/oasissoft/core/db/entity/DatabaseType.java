package cn.oasissoft.core.db.entity;

import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.ex.OasisDbException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;

/**
 * @author: Quinn
 * @title: 支撑的数据库类型 枚举
 * @description: TODO: 目前主要支持MySql，其他数据库没有测试过
 * @date: 2021-03-18 11:13 上午
 */
public enum DatabaseType {
    MySql, SqlServer, SQLite, H2, PostgreSQL, Oracle;

    /**
     * 获取数据库类型对应的命令参数标志符
     *
     * @param databaseType
     * @return
     */
    public static String getSymbol(DatabaseType databaseType) {
        // TODO: 暂时不区分
        if (SqlServer == databaseType) {
            return "@";
        } else {
            return ":";
        }
    }

    public static boolean supportTop(DatabaseType dbType) {
        return SqlServer.equals(dbType);
    }

    /**
     * 通过数据库名称获取数据库类型
     *
     * @param dbName
     * @return
     */
    public static DatabaseType getDatabase(String dbName) {
        switch (dbName.toLowerCase()) {
            case "mysql":
                return MySql;
            case "h2":
                return H2;
            case "sqlite":
                return SQLite;
            case "oracle":
                return Oracle;
            case "postgresql":
                return PostgreSQL;
            case "sqlserver":
                return SqlServer;
            default:
                throw new OasisDbDefineException("不支持的数据库名称:" + dbName);
        }
    }

    public static DatabaseType getDataBaseBy(NamedParameterJdbcTemplate jdbcTemplate) {
        try {
            return DatabaseType.getDatabase(jdbcTemplate.getJdbcTemplate().getDataSource().getConnection().getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            throw new OasisDbException("无法从jdbc连接中获取数据库类型.");
        }
    }

}
