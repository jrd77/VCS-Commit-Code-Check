package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberDebtListResDTO implements Serializable {
    @AutoDocProperty("会员号")
    private String memNo;
    @AutoDocProperty("手机号")
    private String mobile;
    @AutoDocProperty("姓名")
    private String realName;
    @AutoDocProperty("会员历史欠款")
    private Integer historyDebtAmt = 0;
    @AutoDocProperty("会员订单欠款")
    private Integer orderDebtAmt = 0;
    @AutoDocProperty("未支付的补付")
    private Integer noPaySupplementAmt = 0;
}
