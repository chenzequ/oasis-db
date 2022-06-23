package cn.oasissoft.core.db.query;

import cn.oasissoft.core.db.utils.LambdaUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库lambda查询对象构建器
 *
 * @author Quinn
 * @desc
 * @time 2022/06/19 09:37
 */
public class DbLambdaQueryBuilder {

    private DbLambdaQueryBuilder() {
    }

    public static <T> InnerBuilder to(Class<T> tClass) {
        return new InnerBuilder<T>(tClass);
    }

    // 内部构建器接口
    public interface InnerItemBuilderBase<T> {
    }

    // 内部项构建器
    public static class InnerItemBuilder<T> implements InnerItemBuilderBase<T> {
        private final String property;
        private final DbLoginOperator logicOP;
        private final InnerBuilder innerBuilder;

        private Object value;
        private DbCriterionOperator criterionOP;

        private InnerItemBuilder(String property, DbLoginOperator logicOP, InnerBuilder innerBuilder) {
            this.property = property;
            this.logicOP = logicOP;
            this.innerBuilder = innerBuilder;
        }

        public InnerBuilder eq(Object value) {
            this.criterionOP = DbCriterionOperator.Equal;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder notEq(Object value) {
            this.criterionOP = DbCriterionOperator.NotEqual;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder greatThan(Object value) {
            this.criterionOP = DbCriterionOperator.GreatThan;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder greatThanEq(Object value) {
            this.criterionOP = DbCriterionOperator.GreatThanEqual;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder lessThan(Object value) {
            this.criterionOP = DbCriterionOperator.LessThan;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder lessThanEq(Object value) {
            this.criterionOP = DbCriterionOperator.LessThanEqual;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder like(String value) {
            this.criterionOP = DbCriterionOperator.Like;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder leftLike(String value) {
            this.criterionOP = DbCriterionOperator.LeftLike;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder rightLike(Object value) {
            this.criterionOP = DbCriterionOperator.RightLike;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder notLike(Object value) {
            this.criterionOP = DbCriterionOperator.NotLike;
            this.value = value;
            return this.innerBuilder;
        }

        public InnerBuilder in(Object[] values) {
            this.criterionOP = DbCriterionOperator.In;
            this.value = values;
            return this.innerBuilder;
        }

        public InnerBuilder in(Iterable values) {
            this.criterionOP = DbCriterionOperator.In;
            this.value = values;
            return this.innerBuilder;
        }

        public InnerBuilder notIn(Object[] values) {
            this.criterionOP = DbCriterionOperator.NotIn;
            this.value = values;
            return this.innerBuilder;
        }

        public InnerBuilder notIn(Iterable values) {
            this.criterionOP = DbCriterionOperator.NotIn;
            this.value = values;
            return this.innerBuilder;
        }

        public InnerBuilder between(Object begin, Object end) {
            this.criterionOP = DbCriterionOperator.Between;
            this.value = new Object[]{begin, end};
            return this.innerBuilder;
        }

        public InnerBuilder notBetween(Object begin, Object end) {
            this.criterionOP = DbCriterionOperator.NotBetween;
            this.value = new Object[]{begin, end};
            return this.innerBuilder;
        }

        public InnerBuilder isNull() {
            this.criterionOP = DbCriterionOperator.Null;
            this.value = null;
            return this.innerBuilder;
        }

        public InnerBuilder isNotNull() {
            this.criterionOP = DbCriterionOperator.NotNull;
            this.value = null;
            return this.innerBuilder;
        }

    }

    // 内部Sql条件构建器
    public static class InnerSqlItemBuilder<T> implements InnerItemBuilderBase<T> {
        private final String property;
        private final DbLoginOperator logicOP;
        private final InnerBuilder innerBuilder;
        private DbCriterionOperator criterionOP;
        private String sqlSection;

        private InnerSqlItemBuilder(String property, DbLoginOperator logicOP, InnerBuilder innerBuilder) {
            this.property = property;
            this.logicOP = logicOP;
            this.innerBuilder = innerBuilder;
        }

        private InnerBuilder baseSql(DbCriterionOperator criterionOP, String sqlSection) {
            this.criterionOP = criterionOP;
            this.sqlSection = sqlSection;
            return this.innerBuilder;
        }

        public InnerBuilder eq(String sqlSection) {
            return baseSql(DbCriterionOperator.Equal, sqlSection);
        }

        public InnerBuilder notEq(String sqlSection) {
            return baseSql(DbCriterionOperator.NotEqual, sqlSection);
        }

        public InnerBuilder greatThan(String sqlSection) {
            return baseSql(DbCriterionOperator.GreatThan, sqlSection);
        }

        public InnerBuilder greatThanEq(String sqlSection) {
            return baseSql(DbCriterionOperator.GreatThanEqual, sqlSection);
        }

        public InnerBuilder lessThan(String sqlSection) {
            return baseSql(DbCriterionOperator.LessThan, sqlSection);
        }

        public InnerBuilder lessThanEq(String sqlSection) {
            return baseSql(DbCriterionOperator.LessThanEqual, sqlSection);
        }

        public InnerBuilder like(String sqlSection) {
            return baseSql(DbCriterionOperator.Like, sqlSection);
        }

        public InnerBuilder notLike(String sqlSection) {
            return baseSql(DbCriterionOperator.NotLike, sqlSection);
        }
    }

    // 内部Sql条件构建器
    public static class InnerSqlItem2Builder implements InnerItemBuilderBase {
        private final DbLoginOperator logicOP;
        private final InnerBuilder innerBuilder;
        private String sqlSection;

        private InnerSqlItem2Builder(DbLoginOperator logicOP, InnerBuilder innerBuilder) {
            this.logicOP = logicOP;
            this.innerBuilder = innerBuilder;
        }

        public InnerBuilder sql(String sqlSection) {
            this.sqlSection = sqlSection;
            return this.innerBuilder;
        }
    }

    // 内部构建器
    public static class InnerBuilder<T> {
        final List<InnerItemBuilderBase> items;
        final List<DbOrderClause> orderItems;

        final Class<T> tClass;

        private InnerBuilder(Class<T> tClass) {
            this.tClass = tClass;
            items = new ArrayList<>();
            orderItems = new ArrayList<>();
        }

        public InnerItemBuilder and(LambdaFunction<T> func) {
            InnerItemBuilder builder = new InnerItemBuilder(LambdaUtils.getPropertyName(func), DbLoginOperator.And, this);
            items.add(builder);
            return builder;
        }

        public InnerItemBuilder or(LambdaFunction<T> func) {
            InnerItemBuilder builder = new InnerItemBuilder(LambdaUtils.getPropertyName(func), DbLoginOperator.Or, this);
            items.add(builder);
            return builder;
        }

        public InnerSqlItemBuilder andSql(LambdaFunction<T> func) {
            InnerSqlItemBuilder builder = new InnerSqlItemBuilder(LambdaUtils.getPropertyName(func), DbLoginOperator.And, this);
            items.add(builder);
            return builder;
        }

        public InnerSqlItemBuilder orSql(LambdaFunction<T> func) {
            InnerSqlItemBuilder builder = new InnerSqlItemBuilder(LambdaUtils.getPropertyName(func), DbLoginOperator.Or, this);
            items.add(builder);
            return builder;
        }

        public InnerSqlItem2Builder andSql() {
            InnerSqlItem2Builder builder = new InnerSqlItem2Builder(DbLoginOperator.And, this);
            items.add(builder);
            return builder;
        }

        public InnerSqlItem2Builder orSql() {
            InnerSqlItem2Builder builder = new InnerSqlItem2Builder(DbLoginOperator.Or, this);
            items.add(builder);
            return builder;
        }

        public InnerBuilder orderAsc(LambdaFunction<T> func) {
            DbOrderClause orderClause = new DbOrderClause(LambdaUtils.getPropertyName(func), DbOrderOperator.Asc);
            orderItems.add(orderClause);
            return this;
        }

        public InnerBuilder orderDesc(LambdaFunction<T> func) {
            DbOrderClause orderClause = new DbOrderClause(LambdaUtils.getPropertyName(func), DbOrderOperator.Desc);
            orderItems.add(orderClause);
            return this;
        }

        /**
         * 构建Query对象
         *
         * @return
         */
        public DbQuery build() {
            DbQuery query = new DbQuery();
            if (orderItems.size() > 0) {
                query.getOrderClauseList().addAll(orderItems);
            }
            if (items.size() > 0) {
                DbSubQuery subQuery = new DbSubQuery();
                for (InnerItemBuilderBase item : items) {
                    if (item instanceof InnerItemBuilder) {
                        InnerItemBuilder innerItem = (InnerItemBuilder) item;
                        DbCriterion c = new DbCriterion(innerItem.property, innerItem.value, innerItem.criterionOP, innerItem.logicOP);
                        subQuery.add(c);
                    } else if (item instanceof InnerSqlItemBuilder) {
                        InnerSqlItemBuilder innerSqlItem = (InnerSqlItemBuilder) item;
                        DbCriterionSql c = new DbCriterionSql(innerSqlItem.property, innerSqlItem.sqlSection, innerSqlItem.criterionOP, innerSqlItem.logicOP);
                        subQuery.add(c);
                    } else if (item instanceof InnerSqlItem2Builder) {
                        InnerSqlItem2Builder innerSqlItem = (InnerSqlItem2Builder) item;
                        DbCriterionSql c = new DbCriterionSql(innerSqlItem.sqlSection, innerSqlItem.logicOP);
                        subQuery.add(c);
                    }
                }
                query.addSubQuery(subQuery);
            }
            return query;
        }
    }

}
