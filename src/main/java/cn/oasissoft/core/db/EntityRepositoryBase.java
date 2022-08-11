package cn.oasissoft.core.db;

import cn.oasissoft.core.db.config.RepositoryConfigParams;
import cn.oasissoft.core.db.entity.schema.DBTable;
import cn.oasissoft.core.db.entity.schema.TableSchema;
import cn.oasissoft.core.db.ex.OasisDbDefineException;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    protected EntityRepositoryBase(RepositoryConfigParams configParams) {
        super(configParams);
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        // 当前的仓储对象必须有两个泛型签名
        if (parameterizedType.getActualTypeArguments().length != 2) {
            throw new OasisDbDefineException(String.format("仓储 [%s] 必须有两个泛型参数.", this.getClass()));
        }
        // 实体类型
        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(actualTypeArgument instanceof Class)) {
            throw new OasisDbDefineException(String.format("泛型的第一个参数类型[%s]不是实体类型!", actualTypeArgument.getTypeName()));
        }
        this.entityClass = (Class<T>) actualTypeArgument;
        Type pkType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Assert.notNull(entityClass, String.format("[%s]  entity class is null", this.getClass()));
        Assert.notNull(pkType, String.format("[%s]  primary key class is null", this.getClass()));
        this.tableSchema = getTableSchema();
        Assert.notNull(this.tableSchema, String.format("[%s]  table schema is null", this.getClass()));
        // 验证主键类型是否正确
        this.tableSchema.checkPrimaryKeyType(pkType);
        logger.info("初始化[" + this.getClass() + "]对应的仓储对象 -> 完成");
    }

    protected EntityRepositoryBase() {
        this(null);
    }

    // 不使用依赖注入
    protected EntityRepositoryBase(RepositoryConfigParams repositoryConfigParams, Class<T> entityClass) {
        // 主键类型
        super(repositoryConfigParams);
        Assert.notNull(entityClass, "entityClass is null.");
//        Assert.notNull(idClass, "idClass is null.");
        this.entityClass = entityClass;
        this.tableSchema = getTableSchema();
//        this.tableSchema.checkPrimaryKeyType(idClass);
//        logger.info("初始化[" + this.getClass() + "]对应的仓储对象 -> 完成\n");
        //
        init();
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
