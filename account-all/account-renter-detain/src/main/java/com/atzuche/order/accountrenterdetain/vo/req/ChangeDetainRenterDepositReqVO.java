package com.atzuche.order.accountrenterdetain.vo.req;

import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 暂扣请求参数
 */
@Data
public class ChangeDetainRenterDepositReqVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 暂扣/解冻 金额
     * 暂扣为正 解冻为负
     */
    private int amt;
    /**
     * 是否暂扣  yes=暂扣    no = 解冻
     * 暂扣/解冻
     */
    private YesNoEnum isDetain;


    /**
     *暂扣来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 暂扣凭证
     */
    private String uniqueNo;
    /**
     * 备注
     */
    private String remake;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 更新人
     */
    private String updateOp;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getIsDetain(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
    }
}
