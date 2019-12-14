package com.atzuche.order.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 租客费用总表落库 请求参数
 * @author haibao.yan
 */
@Data
public class AccountRenterCostReqVO {

    //租客费用及其结算总表
    /**
     * 主订单号
     */
    private Long orderNo;
    /**
     * 会员号
     */
    private Integer memNo;
    /**
     * 租车费用
     */
    private Integer rentAmt;
    /**
     * 手续费
     */
    private Integer yongjinAmt;
    /**
     * 基础保障费用
     */
    private Integer basicEnsureAmount;
    /**
     * 全面保障费用
     */
    private Integer comprehensiveEnsureAmount;
    /**
     * 附加驾驶人保证费用
     */
    private Integer additionalDrivingEnsureAmount;
    /**
     * 其他费用
     */
    private Integer otherAmt;
    /**
     * 平台补贴费用
     */
    private Integer platformSubsidyAmount;
    /**
     * 车主补贴费用
     */
    private Integer carOwnerSubsidyAmount;
    /**
     * 实付费用
     */
    private Integer shifuAmt;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改人
     */
    private String updateOp;


    /**
     * 费用明细
     */
    private AccountRenterCostDetailReqVO accountRenterCostDetailReqVO;
    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAdditionalDrivingEnsureAmount(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getBasicEnsureAmount(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getCarOwnerSubsidyAmount(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getComprehensiveEnsureAmount(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOtherAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPlatformSubsidyAmount(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRentAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getShifuAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getYongjinAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAccountRenterCostDetailReqVO(), ErrorCode.PARAMETER_ERROR.getText());
        getAccountRenterCostDetailReqVO().check();
    }
}
