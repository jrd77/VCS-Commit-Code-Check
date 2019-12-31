package com.atzuche.order.renterorder.vo;

import com.atzuche.order.commons.enums.account.FreeDepositTypeEnum;
import lombok.Data;

/**
 * 车辆押金
 *
 * @author pengcheng.fu
 * @date 2019/12/31 11:02
 */

@Data
public class RenterOrderCarDepositResVO {

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
    private Integer yingfuDepositAmt;
    /**
     * 免押金额
     */
    private Integer reductionAmt;

    /**
     * 减免金额比例
     */
    private String amt;

    /**
     * 免押方式
     */
    private FreeDepositTypeEnum freeDepositType;
}
