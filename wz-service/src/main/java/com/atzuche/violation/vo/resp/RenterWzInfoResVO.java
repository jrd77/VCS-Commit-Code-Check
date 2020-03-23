package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocIgnoreProperty;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * RenterWzInfo
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class RenterWzInfoResVO {

    @AutoDocProperty("违章押金")
    private String wzDeposit;

    @AutoDocProperty("应收")
    private String yingshouDeposit;

    @AutoDocIgnoreProperty()
    private Integer payment;

    @AutoDocProperty("支付方式")
    private String paymentStr;

    @AutoDocProperty("支付类型")
    private String freeDepositTypeStr;

    @AutoDocIgnoreProperty()
    private Integer freeDepositType;

    @AutoDocProperty("减免金额")
    private String waiverAmount;

    @AutoDocProperty("支付时间")
    private String payTimeStr;

    @AutoDocIgnoreProperty()
    private Date payTime;

    @AutoDocProperty("支付状态")
    private String transStatusStr;

    @AutoDocIgnoreProperty()
    private String transStatus;

}
