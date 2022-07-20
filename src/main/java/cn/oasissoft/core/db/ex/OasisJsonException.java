package cn.oasissoft.core.db.ex;

/**
 * json 序列化相关异常
 *
 * @author Quinn
 * @desc
 * @time 2022/7/20
 */
public class OasisJsonException extends RuntimeException {

    public OasisJsonException(String error) {
        super(error);
    }
}
