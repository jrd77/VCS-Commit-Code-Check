package com.atzuche.order.renterorder.vo;

import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import lombok.Data;

/**
 * 违章押金
 *
 * @author pengcheng.fu
 * @date 2019/12/31 11:03
 */
@Data
public class RenterOrderIllegalResVO {


    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 应付押金总额
     */
    private int yingfuDepositAmt;
    /**
     * 免押方式
     */
    private FreeDepositTypeEnum freeDepositType;

}
