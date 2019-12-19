package com.atzuche.order.cashieraccount.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

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
     * 退款类型来源code
     */
    private Integer sourceCode;
    /**
     * 退款类型来源描述
     */
    private String sourceDetail;
    /**
     * 退款金额
     */
    private Integer amt;
    /**
     * 退款外部凭证
     */
    private String uniqueNo;
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
     * 参数校验
     */
    public void check() {
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());

    }
}
