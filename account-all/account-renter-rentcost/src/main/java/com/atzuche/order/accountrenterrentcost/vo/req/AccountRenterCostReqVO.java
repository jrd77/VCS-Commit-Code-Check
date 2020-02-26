package com.atzuche.order.accountrenterrentcost.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 租客费用总表落库 请求参数
 * @author haibao.yan
 */
@Data
public class AccountRenterCostReqVO {

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
    private Integer rentAmt;
    /**
     * 手续费
     */
    private int yongjinAmt;
    /**
     * 基础保障费用
     */
    private int basicEnsureAmount;
    /**
     * 全面保障费用
     */
    private int comprehensiveEnsureAmount;
    /**
     * 附加驾驶人保证费用
     */
    private int additionalDrivingEnsureAmount;
    /**
     * 其他费用
     */
    private int otherAmt;
    /**
     * 平台补贴费用
     */
    private int platformSubsidyAmount;
    /**
     * 车主补贴费用
     */
    private int carOwnerSubsidyAmount;
    /**
     * 实付费用
     */
    private int shifuAmt;


    /**
     * 费用明细
     */
    private AccountRenterCostDetailReqVO accountRenterCostDetailReqVO;
    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getAdditionalDrivingEnsureAmount(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getBasicEnsureAmount(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getCarOwnerSubsidyAmount(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getComprehensiveEnsureAmount(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getOtherAmt(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getPlatformSubsidyAmount(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getRentAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getShifuAmt(), ErrorCode.PARAMETER_ERROR.getText());
//        Assert.notNull(getYongjinAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAccountRenterCostDetailReqVO(), ErrorCode.PARAMETER_ERROR.getText());
        getAccountRenterCostDetailReqVO().check();
    }
}
