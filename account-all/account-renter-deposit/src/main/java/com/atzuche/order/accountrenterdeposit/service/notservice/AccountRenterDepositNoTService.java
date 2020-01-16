package com.atzuche.order.accountrenterdeposit.service.notservice;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.exception.PayOrderRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositMapper;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        accountRenterDepositEntity.setFreeDepositType(createOrderRenterDepositReqVO.getFreeDepositType().getCode());
        int result = accountRenterDepositMapper.insertSelective(accountRenterDepositEntity);
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
    public AccountRenterDepositEntity getAccountRenterDepositEntity(String orderNo, String memNo) {
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        return accountRenterDepositEntity;
    }

    /**
     * 更新押金 实付信息
     * @param payedOrderRenterDeposit
     */
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit) {
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(payedOrderRenterDeposit.getOrderNo(),payedOrderRenterDeposit.getMemNo());
        BeanUtils.copyProperties(payedOrderRenterDeposit,accountRenterDepositEntity);
        int result = accountRenterDepositMapper.updateByPrimaryKeySelective(accountRenterDepositEntity);
        if(result==0){
            throw new PayOrderRenterDepositDBException();
        }
    }

    /**
     * 更新车辆押金 剩余金额
     * @param detainRenterDepositReqVO
     */
    public void updateRenterDepositChange(DetainRenterDepositReqVO detainRenterDepositReqVO) {
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(detainRenterDepositReqVO.getOrderNo(),detainRenterDepositReqVO.getMemNo());
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterDepositDBException();
        }
        //计算剩余可扣金额押金总和
        int surplusAmt = accountRenterDepositEntity.getSurplusDepositAmt();
        if(-detainRenterDepositReqVO.getAmt() + surplusAmt<0){
            //可用 剩余押金 不足
            throw new PayOrderRenterDepositDBException();
        }
        AccountRenterDepositEntity accountRenterDeposit = new AccountRenterDepositEntity();
        accountRenterDeposit.setId(accountRenterDepositEntity.getId());
        accountRenterDeposit.setVersion(accountRenterDepositEntity.getVersion());
        //押金剩余金额
        accountRenterDeposit.setSurplusDepositAmt(accountRenterDepositEntity.getSurplusDepositAmt() - Math.abs(detainRenterDepositReqVO.getAmt()));

        int result =  accountRenterDepositMapper.updateByPrimaryKeySelective(accountRenterDeposit);
        if(result==0){
            throw new PayOrderRenterDepositDBException();
        }
    }

    /**
     * 更新押金结算状态
     * @param memNo
     * @param orderNo
     */
    public void updateOrderDepositSettle(String memNo, String orderNo) {
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterDepositDBException();
        }
        AccountRenterDepositEntity entity = new AccountRenterDepositEntity();
        entity.setId(accountRenterDepositEntity.getId());
        entity.setVersion(accountRenterDepositEntity.getVersion());
        entity.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        entity.setSettleTime(LocalDateTime.now());
        int result = accountRenterDepositMapper.updateByPrimaryKeySelective(entity);
        if(result == 0){
            throw new PayOrderRenterDepositDBException();
        }
    }

    public void updateRenterDepositEntity(AccountRenterDepositEntity entity) {
        accountRenterDepositMapper.updateByPrimaryKeySelective(entity);
    }
}
