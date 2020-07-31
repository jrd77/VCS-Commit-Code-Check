package com.atzuche.order.settle.dto.mq;

import lombok.Data;

/**
 * @author pengcheng.fu
 * @date 2020/7/22 15:56
 */
@Data
public class SendWzSettleSuccessMqToRenyunDTO {

    /**
     * 违章编码
     */
    private String wzcode;
    /**
     * 订单号
     */
    private String orderno;
    /**
     * 违章押金状态
     */
    private String wzdstate;
    /**
     * 支付违章押金
     */
    private String wzdeposit;
    /**
     * 剩余违章押金
     */
    private String sywzdeposit;
    /**
     * 保险理赔
     */
    private String claim;
    /**
     * 其他扣款
     */
    private String other;


}
