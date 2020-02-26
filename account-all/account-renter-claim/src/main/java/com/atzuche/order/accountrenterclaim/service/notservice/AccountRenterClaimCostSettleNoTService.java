package com.atzuche.order.accountrenterclaim.service.notservice;

import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostSettleEntity;
import com.atzuche.order.accountrenterclaim.exception.AccountRenterClaimException;
import com.atzuche.order.accountrenterclaim.mapper.AccountRenterClaimCostSettleMapper;
import com.atzuche.order.accountrenterclaim.vo.req.AccountRenterClaimDetailReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 理赔费用/及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Service
public class AccountRenterClaimCostSettleNoTService {
    @Autowired
    private AccountRenterClaimCostSettleMapper accountRenterClaimCostSettleMapper;


    /**
     * 查询租客订单理赔费用总额
     * @param orderNo
     * @return
     */
    public AccountRenterClaimCostSettleEntity getRenterClaimCost(String orderNo) {
        AccountRenterClaimCostSettleEntity accountRenterClaimCostSettle = accountRenterClaimCostSettleMapper.getRenterClaimCost(orderNo);
        return  accountRenterClaimCostSettle;
    }

    /**
     * 查询租客订单理赔费用总额
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getRenterClaimCostAmt(String orderNo, String memNo) {
        AccountRenterClaimCostSettleEntity accountRenterClaimCostSettle = accountRenterClaimCostSettleMapper.getRenterClaimCostAmt(orderNo, memNo);
        if(Objects.isNull(accountRenterClaimCostSettle) || Objects.isNull(accountRenterClaimCostSettle.getShifuAmt())){
            return NumberUtils.INTEGER_ZERO;
        }
        return  accountRenterClaimCostSettle.getShifuAmt();
    }

    /**
     * 更新理赔费用总额
     * @param accountRenterClaimDetail
     */
    public void changeRenterClaimCost(AccountRenterClaimDetailReqVO accountRenterClaimDetail) {
        //1 校验
        AccountRenterClaimCostSettleEntity accountRenterClaimCostSettle = accountRenterClaimCostSettleMapper.getRenterClaimCostAmt(accountRenterClaimDetail.getOrderNo(),accountRenterClaimDetail.getMemNo());
        if(Objects.isNull(accountRenterClaimCostSettle) || Objects.isNull(accountRenterClaimCostSettle.getShifuAmt())){
            throw new AccountRenterClaimException();
        }
        int amt = accountRenterClaimCostSettle.getShifuAmt() + accountRenterClaimDetail.getAmt();
        if(amt<0){
            throw new AccountRenterClaimException();
        }
        // 2 更新违章费用账户余额
        AccountRenterClaimCostSettleEntity accountRenterClaimCostSettleEntity = new AccountRenterClaimCostSettleEntity();
        accountRenterClaimCostSettleEntity.setVersion(accountRenterClaimCostSettle.getVersion());
        accountRenterClaimCostSettleEntity.setShifuAmt(amt);
        accountRenterClaimCostSettleEntity.setId(accountRenterClaimCostSettle.getId());
        int result = accountRenterClaimCostSettleMapper.updateByPrimaryKeySelective(accountRenterClaimCostSettleEntity);
        if(result==0){
            throw new AccountRenterClaimException();
        }
    }
}
