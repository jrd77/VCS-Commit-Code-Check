package com.atzuche.order.accountrenterdeposit.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
     * 免押金额
     */
    private Integer reductionAmt;

    /**
     * 减免详情
     */
    List<CreateOrderRenterReductionDepositReqVO> createOrderRenterReductionDeposits;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getYingfuDepositAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getYingfuDepositAmt()==0, ErrorCode.PARAMETER_ERROR.getText());
        //是否存在减免
        Assert.notNull(getReductionAmt(), ErrorCode.PARAMETER_ERROR.getText());
        boolean isReduction = getReductionAmt()>0 && !CollectionUtils.isEmpty(createOrderRenterReductionDeposits);
        Assert.isTrue(isReduction, ErrorCode.PARAMETER_ERROR.getText());

    }
}
