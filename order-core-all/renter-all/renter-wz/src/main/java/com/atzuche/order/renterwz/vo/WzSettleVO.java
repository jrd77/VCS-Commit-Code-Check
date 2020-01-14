package com.atzuche.order.renterwz.vo;

import lombok.Data;

/**
 * WzSettleVO
 *
 * @author shisong
 * @date 2020/1/10
 */
@Data
public class WzSettleVO {

    /**
     * 违章金额
     */
    private String amount;

    /**
     * 是否能结算
     */
    private Boolean canSettle;

}
