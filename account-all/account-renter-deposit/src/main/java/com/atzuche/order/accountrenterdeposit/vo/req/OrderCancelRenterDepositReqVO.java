package com.atzuche.order.accountrenterdeposit.vo.req;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 暂扣/扣减 记录实付付车俩押金记录请求参数
 */
@Data
public class OrderCancelRenterDepositReqVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;

    /**
     * 转移金额
     */
    private Integer amt;
    /**
     * 车主子订单
     */
    private String renterOrderNo;

}
