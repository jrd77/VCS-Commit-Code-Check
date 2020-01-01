package com.atzuche.order.cashieraccount.vo.res;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import lombok.Data;

/**
 * 个人代付信息
 * @author haibao.yan
 */
@Data
public class AccountPayAbleResVO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 会员号
     */
    private String memNo;
    /**
     * 名称
     */
    private String title;

    /**
     * 代付款项
     */
    private Integer amt;
    /**
     * 费用类型code
     */
    private RenterCashCodeEnum renterCashCode;

    private AccountPayAbleResVO(){}

    public AccountPayAbleResVO(String orderNo, String memNo, Integer amt, RenterCashCodeEnum renterCashCode,String title) {
        this.orderNo = orderNo;
        this.memNo = memNo;
        this.amt = amt;
        this.renterCashCode = renterCashCode;
        this.title = title;
    }

}
