package cn.oasissoft.core.db.entity;

import cn.oasissoft.core.db.query.LambdaFunction;
import cn.oasissoft.core.db.utils.LambdaUtils;
import org.springframework.util.Assert;

/**
 * 更新对象
 *
 * @author Quinn
 * @desc
 * @time 2022/06/21 10:15
 */
public class UpdateObject<T> {

    private final String prop;
    private Object value;


    public UpdateObject(String prop, Object value) {
        Assert.hasText(prop, "prop is blank.");
        this.prop = prop;
        this.value = value;
    }

    public UpdateObject(String prop) {
        this(prop, null);
    }

    public UpdateObject(LambdaFunction<T> lambdaFunction, Object value) {
        this(LambdaUtils.getPropertyName(lambdaFunction), value);
    }

    public UpdateObject(LambdaFunction<T> lambdaFunction) {
        this(lambdaFunction, null);
    }

    public String getProp() {
        return prop;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
