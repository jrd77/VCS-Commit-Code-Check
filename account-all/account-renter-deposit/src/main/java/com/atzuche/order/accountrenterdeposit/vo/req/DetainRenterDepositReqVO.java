package com.atzuche.order.accountrenterdeposit.vo.req;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;

import lombok.Data;

/**
 * 暂扣/扣减 记录实付付车俩押金记录请求参数
 */
@Data
public class DetainRenterDepositReqVO {
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
    private int amt;
    /**
     * 押金凭证
     */
    private String uniqueNo;

    /**
     * 创建人
     */
    private String createOp;

    /**
     *修改人
     */
    private String updateOp;

    /**
     * 押金扣除来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;

    /**
     * 备注
     */
    private String remake;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
