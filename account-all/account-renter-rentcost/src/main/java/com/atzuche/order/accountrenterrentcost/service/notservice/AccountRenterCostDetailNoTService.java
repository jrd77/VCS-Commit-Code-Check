package com.atzuche.order.accountrenterrentcost.service.notservice;

import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostDetailException;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostDetailMapper;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 租车费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostDetailNoTService {
    @Autowired
    private AccountRenterCostDetailMapper accountRenterCostDetailMapper;

    /**
     * 租车费用资金明细落库
     * @param accountRenterCostDetailReqVO
     */
    public int insertAccountRenterCostDetail(AccountRenterCostDetailReqVO accountRenterCostDetailReqVO) {
        AccountRenterCostDetailEntity accountRenterCostDetail = new AccountRenterCostDetailEntity();
        BeanUtils.copyProperties(accountRenterCostDetailReqVO,accountRenterCostDetail);
        accountRenterCostDetail.setSourceCode(accountRenterCostDetailReqVO.getRenterCashCodeEnum().getCashNo());
        accountRenterCostDetail.setSourceDetail(accountRenterCostDetailReqVO.getRenterCashCodeEnum().getTxt());
        int result = accountRenterCostDetailMapper.insertSelective(accountRenterCostDetail);
        if(result==0){
            throw new AccountRenterRentCostDetailException();
        }
        return accountRenterCostDetail.getId();
    }

    /**
     * 租车费用资金明细落库
     * @param accountRenterCostDetail
     */
    public int insertAccountRenterCostDetailEntity(AccountRenterCostDetailEntity accountRenterCostDetail) {
        int result = accountRenterCostDetailMapper.insertSelective(accountRenterCostDetail);
        if(result==0){
            throw new AccountRenterRentCostDetailException();
        }
        return accountRenterCostDetail.getId();
    }

    /**
     * 根据订单号查询租客的租金支付明细
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo) {
        return accountRenterCostDetailMapper.getAccountRenterCostDetailsByOrderNo(orderNo);
    }

    /**
     * 根据订单号 和会员号 查询 订单 钱包支付金额
     * @param orderNo
     * @param renterMemNo
     */
    public int getRentCostPayByWallet(String orderNo, String renterMemNo) {
        List<AccountRenterCostDetailEntity> result = accountRenterCostDetailMapper.selectRenterCostSettleDetail(orderNo,renterMemNo, PaySourceEnum.WALLET_PAY.getCode());
        if(CollectionUtils.isEmpty(result)){
            return 0;
        }
        int amt = result.stream().mapToInt(AccountRenterCostDetailEntity::getAmt).sum();
        return amt;
    }
}
