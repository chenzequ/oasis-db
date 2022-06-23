package db.mysql;

import cn.oasissoft.core.db.entity.PageList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:43
 */
@SpringBootTest
public class TestService {

    @Autowired
    private TestDDLRepository testDDL;

    @Autowired
    private TestRepository testRepo;

    @Test
    public void test_create_table() {
        String sql = testDDL.table_MySql_UTF8_MB4();
        System.out.println(sql);
    }

    @Test
    public void test_insert() {
        for (int i = 0; i < 10; i++) {
            TestInfo info = TestInfo.newInstance(i);
            int result = testRepo.save(info);
            System.out.println(result);
        }
    }

    @Test
    public void test_update() {
        TestInfo info = TestInfo.newInstance(88);
        info.setId(1767479329735884800L);
        int result = testRepo.update(info);
        System.out.println(result);
    }

    @Test
    public void test_remove() {
        int result = testRepo.remove(1767476050943655936L);
        System.out.println(result);
    }

    @Test
    public void test_get() {
        TestInfo info = testRepo.queryModel(1767479329735884800L);
        System.out.println(info);
    }

    @Test
    public void test_list(){
        List<TestInfo> models = testRepo.queryList(null);
        models.forEach(System.out::println);
    }

    @Test
    public void test_list_query(){
        List<TestInfo> list = testRepo.queryList(null, 4, 3);
        list.forEach(System.out::println);
    }

    @Test
    public void test_page(){
        PageList<TestInfo> page = testRepo.queryPage(null, 4, 3);
        System.out.println(page.getTotalPagesBy(4));
        System.out.println(page.getTotal());
        System.out.println("------");
        page.getModels().forEach(System.out::println);
    }
}
