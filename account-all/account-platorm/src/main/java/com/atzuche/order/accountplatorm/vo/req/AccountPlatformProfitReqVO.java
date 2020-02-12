package com.atzuche.order.accountplatorm.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;

/**
 *  平台订单收益结算信息
 * @author haibao.yan
 */
@Data
public class AccountPlatformProfitReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 车主补贴金额
     */
    private int ownerSubsidyAmt;
    /**
     * 补贴车主金额
     */
    private int subsidyOwnerAmt;
    /**
     * 车主收益金额
     */
    private int ownerIncomeAmt;
    /**
     * 租客费用
     */
    private int renterCost;
    /**
     * 租客补贴金额
     */
    private int renterSubsidyAmt;
    /**
     * 补贴租客金额
     */
    private int subsidyRenterAmt;
    /**
     * 平台补贴
     */
    private int platformSubsidyAmt;
    /**
     * 平台应收费用金额
     */
    private int platformReceivableAmt;
    /**
     * 平台实收费用金额
     */
    private int platformReceivedAmt;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改人
     */
    private String updateOp;

    /**
     * 订单补贴信息
     */
    private List<AccountPlatformSubsidyDetailReqVO> accountPlatformSubsidyDetails;
    /**
     * 订单收益信息
     */
    private List<AccountPlatformProfitDetailReqVO> accountPlatformProfitDetail;


    public void check() {
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
