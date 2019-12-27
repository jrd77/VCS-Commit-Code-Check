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

    public void setAccountPayAbleRes(List<PayableVO> payableVOs, int amtDeposit, int amtWZDeposit, int rentAmt) {

        List<AccountPayAbleResVO> accountPayAbles = ImmutableList.of(
                new AccountPayAbleResVO(orderNo,memNo,amtDeposit,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()),
                new AccountPayAbleResVO(orderNo,memNo,amtWZDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getTxt())
        );
        if(!CollectionUtils.isEmpty(payableVOs)){
            payableVOs.stream().forEach(obj ->{
                RenterCashCodeEnum type = getAmtPay()>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                accountPayAbles.add(new AccountPayAbleResVO(orderNo,memNo,obj.getAmt(),type,obj.getTitle()));
            });
        }
        setAccountPayAbles(accountPayAbles);
        setTitle("待支付金额：" +amt + "，订单号："  + orderNo);
    }


    /**
     * 初始化待支付费用明细
     * @param payableVOs
     */

}
