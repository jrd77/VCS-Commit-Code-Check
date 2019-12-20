package com.atzuche.order.accountrenterdeposit.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 下单成功 记录应付车俩押金
 */
@Data
public class CreateOrderRenterReductionDepositReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 减免金额
     */
    private Integer amt;
    /**
     * 减免类型
     */
    private Integer type;
    /**
     * 减免类型名称
     */
    private String typeName;
    /**
     * 减免比例
     */
    private Integer ratio;
    /**
     * 减免项名称
     */
    private String name;

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
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getType(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRatio(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getName(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
