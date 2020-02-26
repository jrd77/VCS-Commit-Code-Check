package com.atzuche.order.cashieraccount.vo.req;

import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 车辆押金/违章押金退款请求传参
 * @author haibao.yan
 */
@Data
public class CashierRefundApplyReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 退款类型来源
     */
    private RenterCashCodeEnum renterCashCodeEnum;
    /**
     * 退款金额
     */
    private Integer amt;

    /**
     * 备注
     */
    private String remake;
    /**
     * 操作部门
     */
    private String deptName;
    /**
     *
     */
    private String createOp;
    /**
     *
     */
    private String updateOp;

    /**
     * 凹凸appID
     */
    private String atappId;
    /**
     * 支付项目
     */
    private String payKind;
    /**
     * 支付类型
     */
    private String payType;
    /**
     * 支付来源
     */
    private String paySource;

    /**
     * 退款状态
     */
    private Integer status;
    /**
     * 支付流水号
     */
    private String qn;

    /**
     * 退款项目：押金，违章押金，费用
     */
    private String flag;
    /**
     * 类型（系统退款/手工退款）
     */
    private Integer type;


    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterCashCodeEnum(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getFlag(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
