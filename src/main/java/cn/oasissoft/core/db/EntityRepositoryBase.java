package cn.oasissoft.core.db;

import cn.oasissoft.core.db.entity.schema.DBTable;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import cn.oasissoft.core.db.executor.SqlExecutorBase;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 实体仓储基类
 *
 * @author Quinn
 * @desc
 * @time 2022/06/22 00:55
 */
public abstract class EntityRepositoryBase<T, K> extends AbstractRepositoryBase {

    private final Class<T> entityClass; // 操作实体的类型
    private final TableSchema<T> tableSchema; // 表结构

    protected EntityRepositoryBase(NamedParameterJdbcTemplate readJdbc, NamedParameterJdbcTemplate writeJdbc) {
        super(readJdbc, writeJdbc);
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        // 当前的仓储对象必须有两个泛型签名
        if (parameterizedType.getActualTypeArguments().length != 2) {
            throw new OasisDbDefineException(String.format("仓储 [%s] 必须有两个泛型参数.", this.getClass()));
        }
        // 实体类型
        this.entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
        // 主键类型
        Type pkType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Assert.notNull(entityClass, String.format("[%s]  entity class is null", this.getClass()));
        Assert.notNull(pkType, String.format("[%s]  primary key class is null", this.getClass()));
        this.tableSchema = getTableSchema();
        Assert.notNull(this.tableSchema, String.format("[%s]  table schema is null", this.getClass()));
        // 验证主键类型是否正确
        this.tableSchema.checkPrimaryKeyType(pkType);
        logger.info("初始化[" + this.getClass() + "]对应的仓储对象 -> 完成");
    }

    protected EntityRepositoryBase(NamedParameterJdbcTemplate readJdbc) {
        this(readJdbc, null);
    }

    protected Map<Class<? extends SqlExecutorBase>, SqlExecutorBase> getSqlExecutorMap() {
        return null;
    }

    /**
     * 获取当前表结构生成器（可改写）
     */
    protected TableSchema<T> getTableSchema() {
        DBTable dbTable = this.entityClass.getAnnotation(DBTable.class);
        if (dbTable != null) {
            return TableSchema.renderTableSchema(entityClass);
        }
        return null;
    }

}
