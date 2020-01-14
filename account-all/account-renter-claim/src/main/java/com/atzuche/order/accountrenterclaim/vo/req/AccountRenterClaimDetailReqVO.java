package com.atzuche.order.accountrenterclaim.vo.req;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 下单成功 记录应付车俩押金
 */
@Data
public class AccountRenterClaimDetailReqVO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     *f 理赔费用来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 入账金额
     */
    private Integer amt;
    /**
     * 入账时间
     */
    private LocalDateTime time;
    /**
     * 收银凭证
     */
    private String uniqueNo;
    /**
     * 操作部门名称
     */
    private String deptName;
    /**
     * 操作人
     */
    private String deptOp;
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
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()!=0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getUniqueNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
    }
}
