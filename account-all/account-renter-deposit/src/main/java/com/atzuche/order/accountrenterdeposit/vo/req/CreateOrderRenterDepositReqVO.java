package com.atzuche.order.accountrenterdeposit.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 下单成功 记录应付车俩押金
 */
@Data
public class CreateOrderRenterDepositReqVO {

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
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getYingfuDepositAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getYingfuDepositAmt()==0, ErrorCode.PARAMETER_ERROR.getText());
    }
}
