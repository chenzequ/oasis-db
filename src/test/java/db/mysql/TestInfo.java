package db.mysql;

import cn.oasissoft.core.db.entity.schema.DBTable;
import cn.oasissoft.core.db.entity.schema.DBTableId;
import cn.oasissoft.core.db.entity.schema.PrimaryKeyStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 测试实体
 *
 * @author Quinn
 * @desc
 * @time 2022/06/23 11:20
 */
@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@DBTable("test_info")
public class TestInfo {

    /**
     * id
     */
    @DBTableId(strategy = PrimaryKeyStrategy.SnowId)
    private Long id;

    /**
     * 字符串
     */
    private String stringValue;
    /**
     * byte value
     */
    private Byte byteValue;
    /**
     * short value
     */
    private Short shortValue;
    /**
     * int value
     */
    private Integer intValue;
    /**
     * long value
     */
    private Long longValue;
    /**
     * double value
     */
    private Double doubleValue;
    /**
     * bool value
     */
    private Boolean boolValue;

    /**
     * BigDecimal value
     */
    private BigDecimal bigDecimalValue;
    /**
     * BigInteger value
     */
    private BigInteger bigIntegerValue;
    /**
     * date value
     */
    private Date dateValue;
    /**
     * LocalDateTime value
     */
    private LocalDateTime localDateTimeValue;
    /**
     * LocalDate value
     */
    private LocalDate localDateValue;
    /**
     * LocalTime value
     */
    private LocalTime localTimeValue;
    /**
     * enum value
     */
    private TestEnum enumValue;

    /**
     * 创建新实例
     *
     * @param i
     * @return
     */
    public static TestInfo newInstance(int i) {
        TestInfo info = new TestInfo(i + 10000L, "Str_" + i, (byte) i, (short) (1000 + i), 200000 + i, 80000000L + i, i + 0.003, i % 2 == 0, BigDecimal.valueOf(i + 67800000L), BigInteger.valueOf(i + 3450000L), new Date(), LocalDateTime.now().plusDays(i), LocalDate.now().plusMonths(i), LocalTime.now().plusHours(i), TestEnum.of(i));
        return info;
    }


}
