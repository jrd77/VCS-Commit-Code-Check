package com.atzuche.order.cashieraccount.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 押金抵扣 租车费用欠款
 * @author haibao.yan
 */
@Data
public class DeductDepositToRentCostReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客订单号
     */
    private String renterOrderNo;
    /**
     * 待抵扣金额
     */
    private Integer amt;

    /**
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
    }
}
