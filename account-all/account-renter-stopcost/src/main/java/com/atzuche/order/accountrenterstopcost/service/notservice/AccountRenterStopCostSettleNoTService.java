package com.atzuche.order.accountrenterstopcost.service.notservice;

import com.atzuche.order.accountrenterstopcost.entity.AccountRenterStopCostSettleEntity;
import com.atzuche.order.accountrenterstopcost.exception.AccountRenterStopDetailException;
import com.atzuche.order.accountrenterstopcost.mapper.AccountRenterStopCostSettleMapper;
import com.atzuche.order.accountrenterstopcost.vo.req.AccountRenterStopCostDetailReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 停运费状态及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Service
public class AccountRenterStopCostSettleNoTService {
    @Autowired
    private AccountRenterStopCostSettleMapper accountRenterStopCostSettleMapper;

    /**
     * 查询 停运费总和
     */
    public int getRenterStopCostAmt(String memNo) {
        AccountRenterStopCostSettleEntity accountRenterStopCost= accountRenterStopCostSettleMapper.getRenterStopCostAmt(memNo);
        if(Objects.isNull(accountRenterStopCost) || Objects.isNull(accountRenterStopCost.getShifuAmt())){
            return NumberUtils.INTEGER_ZERO;
        }
        return  accountRenterStopCost.getShifuAmt();
    }

    /**
     *  更新停运费用总额
     * @param accountRenterStopCostDetail
     */
    public void changeRenterClaimCost(AccountRenterStopCostDetailReqVO accountRenterStopCostDetail) {
        //1 校验
        AccountRenterStopCostSettleEntity accountRenterStopCostSettle = accountRenterStopCostSettleMapper.getRenterStopCostAmt(accountRenterStopCostDetail.getMemNo());
        if(Objects.isNull(accountRenterStopCostSettle) || Objects.isNull(accountRenterStopCostSettle.getShifuAmt())){
            throw new AccountRenterStopDetailException();
        }
        int amt = accountRenterStopCostSettle.getShifuAmt() + accountRenterStopCostDetail.getAmt();
        if(amt<0){
            throw new AccountRenterStopDetailException();
        }
        // 2 更新违章费用账户余额
        AccountRenterStopCostSettleEntity accountRenterStopCostSettleEntity = new AccountRenterStopCostSettleEntity();
        accountRenterStopCostSettleEntity.setVersion(accountRenterStopCostSettle.getVersion());
        accountRenterStopCostSettleEntity.setShifuAmt(amt);
        accountRenterStopCostSettleEntity.setId(accountRenterStopCostSettle.getId());
        int result = accountRenterStopCostSettleMapper.updateByPrimaryKeySelective(accountRenterStopCostSettleEntity);
        if(result==0){
            throw new AccountRenterStopDetailException();
        }
    }
}
