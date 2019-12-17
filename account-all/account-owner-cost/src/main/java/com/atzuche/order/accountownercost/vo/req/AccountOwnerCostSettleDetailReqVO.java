package com.atzuche.order.accountownercost.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 车主结算费用明细信息
 * @author haibao.yan
 */
@Data
public class AccountOwnerCostSettleDetailReqVO {


    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String ownerOrderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 金额
     */
    private Integer amt;
    /**
     * 费用编码
     */
    private Integer sourceCode;
    /**
     * 费用来源描述
     */
    private String sourceDetail;
    /**
     * 费用唯一凭证
     */
    private String uniqueNo;
    /**
     * 费用类型
     */
    private Integer costType;

    /**
     * 创建人
     */
    private String createOp;

    /**
     *更新人
     */
    private String updateOp;

    /**
     * 参数详情
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOwnerOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getCostType(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
