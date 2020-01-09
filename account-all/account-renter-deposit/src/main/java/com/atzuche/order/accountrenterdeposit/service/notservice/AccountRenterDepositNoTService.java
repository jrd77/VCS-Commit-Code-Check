package com.atzuche.order.accountrenterdeposit.service.notservice;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.exception.PayOrderRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositMapper;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
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
        if("00".equals(accountRenterDepositEntity.getPayStatus())){
            return;
        }
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
        int surplusAmt = accountRenterDepositEntity.getSurplusDepositAmt() + accountRenterDepositEntity.getSurplusAuthorizeDepositAmt() + accountRenterDepositEntity.getSurplusCreditPayAmt();
        if(-detainRenterDepositReqVO.getAmt() + surplusAmt<0){
            //可用 剩余押金 不足
            throw new PayOrderRenterDepositDBException();
        }
        AccountRenterDepositEntity accountRenterDeposit = new AccountRenterDepositEntity();
        accountRenterDeposit.setId(accountRenterDepositEntity.getId());
        accountRenterDeposit.setVersion(accountRenterDepositEntity.getVersion());
        //预授权押金剩余金额
        if(Objects.nonNull(accountRenterDeposit.getSurplusAuthorizeDepositAmt()) || accountRenterDeposit.getSurplusAuthorizeDepositAmt()>0){
            accountRenterDeposit.setSurplusAuthorizeDepositAmt(accountRenterDeposit.getSurplusAuthorizeDepositAmt() - detainRenterDepositReqVO.getAmt());
        }
        //押金剩余金额
        if(Objects.nonNull(accountRenterDeposit.getSurplusDepositAmt()) || accountRenterDeposit.getSurplusDepositAmt()>0){
            accountRenterDeposit.setSurplusDepositAmt(accountRenterDeposit.getSurplusDepositAmt() - detainRenterDepositReqVO.getAmt());
        }
        //信用支付押金剩余金额
        if(Objects.nonNull(accountRenterDeposit.getSurplusCreditPayAmt()) || accountRenterDeposit.getSurplusCreditPayAmt()>0){
            accountRenterDeposit.setSurplusCreditPayAmt(accountRenterDeposit.getSurplusCreditPayAmt() - detainRenterDepositReqVO.getAmt());
        }
        int result =  accountRenterDepositMapper.updateByPrimaryKeySelective(accountRenterDeposit);
        if(result==0){
            throw new PayOrderRenterDepositDBException();
        }
    }
}
