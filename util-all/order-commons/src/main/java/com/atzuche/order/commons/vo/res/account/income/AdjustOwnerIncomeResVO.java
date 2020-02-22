package com.atzuche.order.commons.vo.res.account.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * 调账成功车主收益接口
 * @author haibao.yan
 */
@Data
public class AdjustOwnerIncomeResVO {
    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 订单号
     */
    @AutoDocProperty("订单号")
    private String orderNo;
    /**
     * 订单号
     */
    @AutoDocProperty("收益明细id")
    private int accountOwnerIncomeDetailId;


}
