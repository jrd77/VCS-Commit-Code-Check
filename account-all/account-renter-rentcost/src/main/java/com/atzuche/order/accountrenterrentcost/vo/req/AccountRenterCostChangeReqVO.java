package com.atzuche.order.accountrenterrentcost.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 租客费用总表落库 请求参数
 * @author haibao.yan
 */
@Data
public class AccountRenterCostChangeReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 租车费用
     */
    private int amt;
    /**
     * 子订单号
     */
    private String renterOrderNo;

    /**
     * 费用凭证
     */
    private String uniqueNo;
    /**
     * 费用类型编码
     */
    private RenterCashCodeEnum renterCashCodeEnum;


    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
