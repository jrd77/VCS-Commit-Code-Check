package com.atzuche.order.accountownercost.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 车主结算费用信息
 * @author haibao.yan
 */
@Data
public class AccountOwnerCostSettleReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 车主订单号
     */
    private String orderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 车主补贴费用
     */
    private int subsidyAmt;
    /**
     * 采购费用
     */
    private int purchaseAmt;
    /**
     * 增值订单费用
     */
    private int incrementAmt;
    /**
     * 应收费用
     */
    private int yingshouAmt;
    /**
     * amt
     */
    private int shishouAmt;
    /**
     * 创建人
     */
    private String createOp;
    /**
     * update_op
     */
    private String updateOp;


    /**
     * 车主结算费用所以明细信息
     */
    private List<AccountOwnerCostSettleDetailReqVO> accountOwnerCostSettleDetailReqVO;

    /**
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOwnerOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSubsidyAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getPurchaseAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getIncrementAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getYingshouAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getShishouAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(!CollectionUtils.isEmpty(getAccountOwnerCostSettleDetailReqVO()), ErrorCode.PARAMETER_ERROR.getText());
        getAccountOwnerCostSettleDetailReqVO().stream().forEach(obj ->{
            obj.check();
        });
    }

}
