# Oasis DB 访问器

## 功能

### 已支持的功能

#### repository - 单表仓储(核心)

##### RepositoryBase
> 所有仓储的基类,提供与jdbc操作的核心方法
> 
> + 每个sql jdbc 操作的钩子(执行前，执行后，执行异常)
> + 支持 Read,Write 分离。并且提供每个sql执行方法独立的 jdbc template
> + 提供统一修改执行sql传入参数的方法,已内置了 LocalDateTime去除纳秒时间的处理

##### 基础Repository功能

> + 查询单个数据，传入id|query，返回 Model,MapEntity,View 三种 , 可for update 锁定数据
> + 查询多行数据 传入 query , 返回指定数量|全部 的 Model|MapEntity|View
> + 分页查询多行数据 传入 page参数 query , 返回 Model|MapEntity|View
> 
> + 查询单个结果
>   + Single 单个单元格
>   + Count 数量
>   + Sum 合计
>   + Avg 平均值
>   + Max 最大值
>   + Min 最小值

##### RepositoryConfigParam 仓储配置参数

> + 在 `AbstractRepositoryBase`中，可以使用构造函数注入，或者属性注入，注入参数

##### 表名机制
> + 正常表名 -> \`表名\`
> + 分表表名 -> \`表名_x`\
> + 联合表名 -> SELECT * FROM \`表_0\` UNION SELECT * FROM \`表_1\`
> + 多表联合 -> \`表A\` as A LEFT JOIN \`表B\` as B
### 待开发功能


## 组件代码演示
```java
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
```