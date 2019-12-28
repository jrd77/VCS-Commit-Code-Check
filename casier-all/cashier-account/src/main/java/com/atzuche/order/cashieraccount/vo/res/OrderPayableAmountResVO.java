package com.atzuche.order.cashieraccount.vo.res;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 个人代付信息
 * @author haibao.yan
 */
@Data
public class OrderPayableAmountResVO {

    /**
     * 会员号
     */
    private String memNo;
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 总待付款项
     */
    private Integer amtTotal;
    /**
     * 已付款项
     */
    private Integer amtPay;
    /**
     * 实际待款项
     */
    private Integer amt;
    /**
     * 名称
     */
    private String title;
    /**
     * 待付费用明细
     */
    private List<AccountPayAbleResVO> accountPayAbles;


    /**
     * 钱包抵扣计算 最终支付金额及款项
     */
    public void useWalletPay(int payBalance){
        setAmt(getAmt()>=payBalance?getAmt()-payBalance:0);

    }

}
