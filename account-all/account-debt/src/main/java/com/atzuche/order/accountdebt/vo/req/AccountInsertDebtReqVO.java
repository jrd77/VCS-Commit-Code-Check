package com.atzuche.order.accountdebt.vo.req;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 产生欠款传参
 * @author haibao.yan
 */
@Data
public class AccountInsertDebtReqVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客子订单号
     */
    private String renterOrderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 类型（订单取消罚金/订单结算欠款）
     */
    private Integer type;
    /**
     * 订单欠款
     */
    private Integer amt;

    /**
     * 历史欠款来源编码
     */
    private Integer sourceCode;
    /**
     * 欠款历史来源描述
     */
    private String sourceDetail;

    /**
     * 操作人部门名称
     */
    private String deptName;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 更新人
     */
    private String updateOp;
    /**
     * 0-正常，1-已逻辑删除
     */
    private Integer isDelete;

    /**
     * 参数校验
     */
    public void check() {
        Assert.notNull(getMemNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getOwnerOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getRenterOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getAmt(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.isTrue(getAmt()==0, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getSourceCode(), ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(getType(), ErrorCode.PARAMETER_ERROR.getText());


    }
}
