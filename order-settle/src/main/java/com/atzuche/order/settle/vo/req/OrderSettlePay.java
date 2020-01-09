package com.atzuche.order.settle.vo.req;

import lombok.Data;

@Data
public class OrderSettlePay {
    /**平台订单号*/
    private String orderNo;
    /**车主会员号*/
    private String ownerNo;
    /**租客产生欠款*/
    private Double renterCreateDebt;
    /**抵扣租客欠款*/
    private Double renterDeduct;
    /**车主收益*/
    private Double ownerIncome;
    /**平台收益 */
    private Double plateformIncome;
    /**平台补贴*/
    private Double plateformSubsidy;
}
