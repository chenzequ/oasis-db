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