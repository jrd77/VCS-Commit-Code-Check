package com.atzuche.order.settle.vo.req;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import lombok.Data;

/**
 * 订单结算租客押金处理请求参数
 *
 * @author pengcheng.fu
 * @date 2020/4/21 11:49
 */

@Data
public class SettleOrderRenterDepositReqVO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 租客会员号
     */
    private String memNo;

    /**
     * 费用信息
     */
    private RenterCashCodeEnum costEnum;

    /**
     * 入账来源
     */
    private RenterCashCodeEnum sourceEnum;

    /**
     * 应该收取费用金额
     */
    private Integer shouldTakeAmt;

    /**
     * 实际抵扣金额
     */
    private Integer realDeductAmt;


}
