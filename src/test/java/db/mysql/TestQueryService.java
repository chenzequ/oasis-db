package db.mysql;

import cn.oasissoft.core.db.query.DbQuery;
import cn.oasissoft.core.db.query.DbQueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 查询条件
 *
 * @author Quinn
 * @desc
 * @time 2022/06/23 15:18
 */
@SpringBootTest
public class TestQueryService {

    @Autowired
    private TestRepository testRepository;

    @Test
    public void test_query(){
//        DbQuery query = DbQueryBuilder.and(TestInfo.Fields.intValue).greatThan(200005)
//                .build();

        DbQuery query = DbQueryBuilder.and(TestInfo::getIntValue).greatThan(200005).build();

        testRepository.queryList(query);
    }

    @Test
    public void test_query2(){
//        DbQuery query = DbQueryBuilder
//                .orderDesc(TestInfo.Fields.id).build();
        DbQuery query = DbQueryBuilder.orderDesc(TestInfo::getId).build();
//        query.addCriterion(TestInfo.Fields.boolValue, true);
//        query.addCriterion(TestInfo.Fields.intValue, 200005, DbCriterionOperator.GreatThan);
        testRepository.queryList(query);
    }

    @Test
    public void test_query3(){
        DbQuery query = new DbQuery();
        query.addCriterion(TestInfo::getBoolValue, true);
        testRepository.queryList(query);
    }

    @Test
    public void test_query4(){
        DbQuery query = DbQuery.builderAnd(TestInfo::getIntValue).greatThanEq(200004).build();
        testRepository.queryList(query);
    }

    @Test
    public void test_query5(){
        DbQuery query = DbQuery.builderAnd(TestInfo::getIntValue).greatThanEq(20008).build();
        Boolean exists = testRepository.exists(query);
        System.out.println(exists);
    }
}
