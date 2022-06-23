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

##### 表名机制
> + 正常表名 -> \`表名\`
> + 分表表名 -> \`表名_x`\
> + 联合表名 -> SELECT * FROM \`表_0\` UNION SELECT * FROM \`表_1\`
> + 多表联合 -> \`表A\` as A LEFT JOIN \`表B\` as B
### 待开发功能
