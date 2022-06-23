package cn.oasissoft.core.db.ex;

/**
 * 数据库jdbc操作异常
 *
 * @author Quinn
 * @desc
 * @time 2022/06/17 17:43
 */
public class OasisJdbcException extends RuntimeException {

    public OasisJdbcException(String msg) {
        super(msg);
    }

    public OasisJdbcException(Exception e) {
        super(e.getMessage());
    }

}
