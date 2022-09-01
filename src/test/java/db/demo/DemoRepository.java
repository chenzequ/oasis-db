package db.demo;

import cn.oasissoft.core.db.TableRepositoryBase;
import cn.oasissoft.core.db.entity.UpdateObject;
import cn.oasissoft.core.db.entity.UpdateSqlObject;
import cn.oasissoft.core.db.entity.schema.DBTable;
import cn.oasissoft.core.db.entity.schema.DBTableId;
import cn.oasissoft.core.db.entity.schema.PrimaryKeyStrategy;
import cn.oasissoft.core.db.query.DbOperandOperator;
import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.query.DbQueryBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 演示仓储(单表)
 *
 * @author Quinn
 * @desc
 * @time 2022/8/11
 */
public class DemoRepository extends TableRepositoryBase<Demo, Long> {

    /*** 操作 ****/

    public void doUpdate() {
        Demo demo = new Demo(0L, "name", true, "memo", LocalDateTime.now(), 1);
        int updates = 0;
        List<Demo> list = new ArrayList<>();
        list.add(demo);
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        DbQuery updateQuery = DbQueryBuilder.and(Demo::getEnabled).eq(true).build();
        // 添加
        // PS1: SnowId 与 AutoIncrement 会自动生成，并填充回 model里面
        // PS2: SnowId 可以通过SnowIdUtils 里面的方法反向获取到 Date,分表时可能会有关
        this.insertSE.by(demo);
        // 添加但不添加memo
        this.insertSE.by(demo, Demo::getMemo);
        // 批量添加
        this.insertSE.batchSave(list);
        // 批量添加，除了memo
        this.insertSE.batchSave(list, Demo::getMemo);

        // 删除
        // 根据id删除
        this.deleteSE.byId(1L);
        // 根据条件批量删除(删除enabled==false的记录)
        this.deleteSE.byQuery(DbQueryBuilder.and(Demo::getEnabled).eq(false).build());

        // 更新
        this.updateSE.by(demo);
        // 除 enabled,memo 外全部更新(主键不会更新)
        this.updateSE.by(demo, Demo::getEnabled);
        // 批量更新
        this.updateSE.updates(ids, new UpdateObject<>(Demo::getEnabled, true), new UpdateObject<>(Demo::getUpdateTime, LocalDateTime.now()));
        this.updateSE.updates(updateQuery, new UpdateObject<>(Demo::getEnabled, true), new UpdateObject<>(Demo::getUpdateTime, LocalDateTime.now()));

        // 批量运算sql
        this.updateSE.updates(ids, new UpdateSqlObject<Demo>(Demo::getMemo, "demo=demo+'abc'"));
        this.updateSE.updates(updateQuery, new UpdateSqlObject<Demo>(Demo::getMemo, "demo=demo+'abc'"));

        // 批量运算(更新指定条件的版本号+1 并且 updateTime=now())
        this.updateSE.updatesNumber(ids, Demo::getVersion, DbOperandOperator.Add, 1, new UpdateObject<>(Demo::getUpdateTime, LocalDateTime.now()));
        this.updateSE.updatesNumber(updateQuery, Demo::getVersion, DbOperandOperator.Add, 1, new UpdateObject<>(Demo::getUpdateTime, LocalDateTime.now()));

        // 批量添加一天
        this.updateSE.updatesNumber(ids, Demo::getUpdateTime, DbOperandOperator.Add, 24 * 60 * 60);
        this.updateSE.updatesNumber(updateQuery, Demo::getUpdateTime, DbOperandOperator.Add, 24 * 60 * 60);

        // 批量打拆扣...

        // 批量反转(true=>false;false=>true)
        this.updateSE.updatesFlip(ids, Demo::getEnabled);
        this.updateSE.updatesFlip(updateQuery, Demo::getEnabled);

        // 批量查找替换(将memo字段里面的old改为new)
        this.updateSE.updatesReplace(ids, Demo::getMemo, "old", "new");
        this.updateSE.updatesReplace(updateQuery, Demo::getMemo, "old", "new");
    }

    public void doQuery() {

        DbQueryBuilder
                // => version=1
                .and(Demo::getVersion).eq(1)
                // => version<>1
                .and(Demo::getVersion).notEq(1)
                // => version>1
                .and(Demo::getVersion).greatThan(1)
                // => version>=1
                .and(Demo::getVersion).greatThanEq(1)
                // => version<1
                .and(Demo::getVersion).lessThan(1)
                // => version<=1
                .and(Demo::getVersion).lessThanEq(1)
                // => name LIKE '%hello%'
                .and(Demo::getName).like("hello")
                // => name NOT LIKE '%hello%'
                .and(Demo::getName).notLike("hello")
                // => name LIKE 'hello%'
                .and(Demo::getName).leftLike("hello")
                // => name LIKE '%hello'
                .and(Demo::getName).rightLike("hello")
                // => version BETWEEN (1,10)
                .and(Demo::getVersion).between(1, 10)
                // => version NOT BETWEEN (1,10)
                .and(Demo::getVersion).notBetween(1, 10)
                // => version IN (1,2,3,4)
                .and(Demo::getVersion).in(new Object[]{1, 2, 3, 4})
                // => version NOT IN (1,2,3,4)
                .and(Demo::getVersion).notIn(new Object[]{1, 2, 3, 4})
                // => version IS NULL
                .and(Demo::getVersion).isNull()
                // => version IS NOT NULL
                .and(Demo::getVersion).isNotNull()
                // OR
                .or(Demo::getName).eq("or")
                // order by version asc
                .orderAsc(Demo::getVersion)
                // order by version desc
                .orderDesc(Demo::getVersion).build()

        ;

        // 单记录查询
        Demo model = this.itemSE.toModel(0L);
        model = this.itemSE.toModel(DbQuery.builderAnd(Demo::getId).eq(0).build());
        // 锁行查询
        model = this.itemSE.toModel(0L, true);
        model = this.itemSE.toModel(DbQuery.builderAnd(Demo::getId).eq(0).build(), true);
        // 返回部分结果
//        this.itemSE.toMap()
    }
}

@Data
@AllArgsConstructor
@DBTable("demo")
class Demo {
    /**
     * id
     */
    @DBTableId(strategy = PrimaryKeyStrategy.SnowId)
    private Long id;
    /**
     * name
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 描述
     */
    private String memo;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 版本号
     */
    private Integer version;
}

@Data
class DemoVO {
    private Long id;
    private String name;
}