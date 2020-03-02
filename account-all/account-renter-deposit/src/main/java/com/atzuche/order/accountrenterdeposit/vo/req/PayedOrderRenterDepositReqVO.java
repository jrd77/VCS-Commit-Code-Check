package com.atzuche.order.accountrenterdeposit.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 支付成功 回调 记录实付付车俩押金
 */
@Data
public class PayedOrderRenterDepositReqVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 支付状态
     */
    private String payStatus;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 实付押金总金额
     */
    private Integer shifuDepositAmt;
    /**
     * 是否预授权，0否1是
     */
    private Integer isAuthorize;
    /**
     * 预授权金额
     */
    private Integer authorizeDepositAmt;
    /**
     * 信用支付金额
     */
    private Integer creditPayAmt;
    /**
     * 剩余押金总额
     */
    private Integer surplusDepositAmt;
    /**
     * 剩余预授权金额
     */
    private Integer surplusAuthorizeDepositAmt;
    
    /**
     * 剩余信用支付金额
     */
    private Integer surplusCreditPayAmt;
    
    /**
     * 开启免押
     */
    private Integer isFreeDeposit;

    /**
     * 创建人
     */
    private String createOp;

    /**
     *修改人
     */
    private String updateOp;

    /**
     * 车辆押金进出明细
     */
    private DetainRenterDepositReqVO detainRenterDepositReqVO;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayStatus(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayTime(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getDetainRenterDepositReqVO(), ErrorCode.PARAMETER_ERROR.getText());
        getDetainRenterDepositReqVO().check();
    }
}
