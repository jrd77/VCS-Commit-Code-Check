package com.atzuche.order.accountrenterdetain.vo.req;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 暂扣请求参数
 */
@Data
public class DetainRenterDepositReqVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 暂扣金额
     */
    private Integer amt;
    /**
     * 暂扣时间
     */
    private LocalDateTime time;
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
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());


    }
}
