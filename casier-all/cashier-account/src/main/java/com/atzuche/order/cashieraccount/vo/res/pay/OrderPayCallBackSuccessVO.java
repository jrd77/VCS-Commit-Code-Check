package com.atzuche.order.cashieraccount.vo.res.pay;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * 支付系统回调
 */
@Data
public class OrderPayCallBackSuccessVO {

    /**
     * memNo
     */
    private String memNo;
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 租车费用支付状态（待支付、已支付）
     */
    private Integer rentCarPayStatus;

    /**
     * 车辆押金支付状态
     */
    private Integer depositPayStatus;
    /**
     * 违章押金支付状态
     */
    private Integer wzPayStatus;

    /**
     * 租车费用是否 补付 1 是 0 否
     */
    private Integer isPayAgain;


}
