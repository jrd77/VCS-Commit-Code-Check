package com.atzuche.order.accountrenterwzdepost.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 支付成功 回调 记录实付付车俩押金
 */
@Data
public class PayedOrderRenterWZDepositReqVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;

    /**
     * 实收违章押金
     */
    private int shishouDeposit;
    /**
     * 是否预授权
     */
    private Integer isAuthorize;
    /**
     * 是否免押
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
     * 违章押金进出明细
     */
    private PayedOrderRenterDepositWZDetailReqVO payedOrderRenterDepositDetailReqVO;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getIsFreeDeposit(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPayedOrderRenterDepositDetailReqVO(), ErrorCode.PARAMETER_ERROR.getText());
        getPayedOrderRenterDepositDetailReqVO().check();
    }
}
