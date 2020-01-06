package com.atzuche.order.cashieraccount.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 抵扣欠款传参
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
     * 待抵扣金额
     */
    private Integer amt;

    /**
     * 租客实付 车辆押金
     */
    private int depositAmt;



    /**
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()==0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getDepositAmt(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
