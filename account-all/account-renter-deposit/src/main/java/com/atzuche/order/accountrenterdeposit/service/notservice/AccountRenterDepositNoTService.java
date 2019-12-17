package com.atzuche.order.accountrenterdeposit.service.notservice;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.exception.ChangeRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.exception.PayOrderRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositMapper;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositDetailReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 租车押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Service
public class AccountRenterDepositNoTService {
    @Autowired
    private AccountRenterDepositMapper accountRenterDepositMapper;


    /**
     * 保存应付押金信息
     * @param createOrderRenterDepositReqVO
     */
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO) {
        AccountRenterDepositEntity accountRenterDepositEntity = new AccountRenterDepositEntity ();
        BeanUtils.copyProperties(createOrderRenterDepositReqVO,accountRenterDepositEntity);
        int result = accountRenterDepositMapper.insert(accountRenterDepositEntity);
        if(result==0){
            throw new AccountRenterDepositDBException();
        }
    }

    /**
     * 返回车辆押金信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public AccountRenterDepositResVO getAccountRenterDepositEntity(String orderNo, String memNo) {
        AccountRenterDepositResVO  result = new AccountRenterDepositResVO();
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        if(Objects.nonNull(accountRenterDepositEntity)){
            BeanUtils.copyProperties(accountRenterDepositEntity,result);
        }
        return result;
    }

    /**
     * 更新押金 实付信息
     * @param payedOrderRenterDeposit
     */
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit) {
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(payedOrderRenterDeposit.getOrderNo(),payedOrderRenterDeposit.getMemNo());
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterDepositDBException();
        }
        BeanUtils.copyProperties(payedOrderRenterDeposit,accountRenterDepositEntity);
        int result = accountRenterDepositMapper.updateByPrimaryKeySelective(accountRenterDepositEntity);
        if(result==0){
            throw new PayOrderRenterDepositDBException();
        }
    }

    /**
     * 更新车辆押金 剩余金额
     * @param payedOrderRenterDepositDetail
     */
    public void updateRenterDepositChange(PayedOrderRenterDepositDetailReqVO payedOrderRenterDepositDetail) {
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(payedOrderRenterDepositDetail.getOrderNo(),payedOrderRenterDepositDetail.getMemNo());
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new ChangeRenterDepositDBException();
        }
        if(payedOrderRenterDepositDetail.getAmt() + accountRenterDepositEntity.getSurplusAuthorizeDepositAmt()<0
           && payedOrderRenterDepositDetail.getAmt()+ accountRenterDepositEntity.getSurplusDepositAmt()<0
        ){
            //可用 剩余押金 不足
            throw new ChangeRenterDepositDBException();
        }
        AccountRenterDepositEntity accountRenterDeposit = new AccountRenterDepositEntity();
        accountRenterDeposit.setId(accountRenterDepositEntity.getId());
        accountRenterDeposit.setVersion(accountRenterDepositEntity.getVersion());
        if(Objects.nonNull(accountRenterDeposit.getSurplusAuthorizeDepositAmt()) || accountRenterDeposit.getSurplusAuthorizeDepositAmt()>Math.abs(payedOrderRenterDepositDetail.getAmt())){
            accountRenterDeposit.setSurplusAuthorizeDepositAmt(accountRenterDeposit.getSurplusAuthorizeDepositAmt() + payedOrderRenterDepositDetail.getAmt());
        }
        if(Objects.nonNull(accountRenterDeposit.getSurplusDepositAmt()) || accountRenterDeposit.getSurplusDepositAmt()>Math.abs(payedOrderRenterDepositDetail.getAmt())){
            accountRenterDeposit.setSurplusDepositAmt(accountRenterDeposit.getSurplusDepositAmt() + payedOrderRenterDepositDetail.getAmt());
        }
        int result =  accountRenterDepositMapper.updateByPrimaryKeySelective(accountRenterDeposit);
        if(result==0){
            throw new ChangeRenterDepositDBException();
        }
    }
}
