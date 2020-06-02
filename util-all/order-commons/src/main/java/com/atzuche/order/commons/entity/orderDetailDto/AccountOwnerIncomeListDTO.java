package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class AccountOwnerIncomeListDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @AutoDocProperty(value="会员号")
    private String memNo;
    @AutoDocProperty(value="收益总金额")
    private Integer incomeAmt;
}
