package cn.oasissoft.core.db.ex;

/**
 * 数据库访问器异常
 *
 * @author Quinn
 * @desc
 * @time 2022/06/18 12:03
 */
public class OasisDbException extends RuntimeException {

    public OasisDbException(String msg) {
        super(msg);
    }

    public OasisDbException(Exception e) {
        super(e.getMessage());
    }

}