package com.atzuche.order.cashieraccount.vo.res;

import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 个人总欠款信息
 * @author haibao.yan
 */
@Data
public class CashierDeductDebtResVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 结算剩余可抵扣金额
     */
    private Integer amt;

    /**
     * 已抵扣抵扣金额
     */
    private Integer deductAmt;

    /**
     * 剩余未抵扣金额
     */
    private Integer surplusAmt;

    /**
     * 来源编码描述
     */
    private Integer sourceCode;
    /**
     * 来源编码（收银台/非收银台）
     */
    private String sourceDetail;

    public CashierDeductDebtResVO(CashierDeductDebtReqVO cashierDeductDebtReqVO,int deductAmt) {
        BeanUtils.copyProperties(cashierDeductDebtReqVO,this);
        this.deductAmt = deductAmt;
        this.surplusAmt = amt - deductAmt;
    }
}
