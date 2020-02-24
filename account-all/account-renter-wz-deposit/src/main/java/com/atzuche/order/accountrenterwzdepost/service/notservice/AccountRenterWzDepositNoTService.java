package com.atzuche.order.accountrenterwzdepost.service.notservice;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.exception.AccountRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.exception.PayOrderRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositMapper;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.DetainRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.OrderRenterDepositWZDetainReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;


/**
 * 违章押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositNoTService {
    @Autowired
    private AccountRenterWzDepositMapper accountRenterWzDepositMapper;




    /**
     * 查询违章信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getAccountRenterWZDepositAmt(String orderNo, String memNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        if(Objects.isNull(accountRenterDepositEntity) || Objects.isNull(accountRenterDepositEntity.getShishouDeposit())){
            return 0;
        }
        return accountRenterDepositEntity.getShishouDeposit();
    }

    /**
     * 查询违章信息
     * @param orderNo
     * @param memNo
     * @return
     */
    public AccountRenterWzDepositEntity getAccountRenterWZDeposit(String orderNo, String memNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        return accountRenterDepositEntity;
    }
    /**
     * 查询违章信息
     * @param orderNo
     * @return
     */
    public AccountRenterWzDepositEntity getAccountRenterWZDepositByOrder(String orderNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrder(orderNo);
        return accountRenterDepositEntity;
    }

    /**
     * 订单下单成功 记录 应收违章押金
     * @param createOrderRenterWZDepositReq
     */
    public void insertRenterWZDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = new AccountRenterWzDepositEntity ();
        BeanUtils.copyProperties(createOrderRenterWZDepositReq,accountRenterDepositEntity);
        accountRenterDepositEntity.setFreeDepositType(createOrderRenterWZDepositReq.getFreeDepositType().getCode());
        accountRenterDepositEntity.setYingshouDeposit(createOrderRenterWZDepositReq.getYingfuDepositAmt());
        int result = accountRenterWzDepositMapper.insertSelective(accountRenterDepositEntity);
        if(result==0){
            throw new AccountRenterWZDepositException();
        }
    }
    /**
     * 更新违章押金 实付信息
     * @param payedOrderWZRenterDeposit
     */
    public void updateRenterDeposit(PayedOrderRenterWZDepositReqVO payedOrderWZRenterDeposit) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(payedOrderWZRenterDeposit.getOrderNo(),payedOrderWZRenterDeposit.getMemNo());
        BeanUtils.copyProperties(payedOrderWZRenterDeposit,accountRenterDepositEntity);
        int result = accountRenterWzDepositMapper.updateByPrimaryKeySelective(accountRenterDepositEntity);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }
    }
    /**
     * 更新违章押金 实退金额
     * @param payedOrderRenterWZDepositDetail
     */
    public void updateRenterWZDepositChange(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail) {
    	AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(payedOrderRenterWZDepositDetail.getOrderNo(),payedOrderRenterWZDepositDetail.getMemNo());
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterWZDepositException();
        }
        if(payedOrderRenterWZDepositDetail.getAmt() + accountRenterDepositEntity.getShishouDeposit()<0){
            //可用 剩余押金 不足
            throw new PayOrderRenterWZDepositException();
        }
        AccountRenterWzDepositEntity accountRenterDeposit = new AccountRenterWzDepositEntity();
        accountRenterDeposit.setId(accountRenterDepositEntity.getId());
        accountRenterDeposit.setVersion(accountRenterDepositEntity.getVersion());
        //抵扣1元欠款的时候，第一次执行为1999，第二次退款1999的时候，这个地方又被修改为1
//        accountRenterDeposit.setShouldReturnDeposit(accountRenterDepositEntity.getShishouDeposit() - Math.abs(payedOrderRenterWZDepositDetail.getAmt())); //实收，默认是全部要退的。     应退
        accountRenterDeposit.setRealReturnDeposit(accountRenterDepositEntity.getRealReturnDeposit());

        //实退，是在退款的环节来考虑的。
        if(Objects.nonNull(accountRenterDeposit.getRealReturnDeposit()) || accountRenterDeposit.getRealReturnDeposit()>Math.abs(payedOrderRenterWZDepositDetail.getAmt())){
            accountRenterDeposit.setShouldReturnDeposit(accountRenterDeposit.getRealReturnDeposit() + payedOrderRenterWZDepositDetail.getAmt());
        }
        int result =  accountRenterWzDepositMapper.updateByPrimaryKeySelective(accountRenterDeposit);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }
    }
    
    /**
     * 更新车辆押金 剩余金额
     * @param detainRenterDepositReqVO
     */
    public void updateRenterWzDepositChange(DetainRenterWZDepositReqVO detainRenterDepositReqVO) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(detainRenterDepositReqVO.getOrderNo(),detainRenterDepositReqVO.getMemNo());
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterWZDepositException();
        }
        //TODO
        //计算剩余可扣金额押金总和
        int surplusAmt = accountRenterDepositEntity.getShishouDeposit();
        if(detainRenterDepositReqVO.getAmt() + surplusAmt<0){
            //可用 剩余押金 不足
            throw new PayOrderRenterWZDepositException();
        }
        AccountRenterWzDepositEntity accountRenterDeposit = new AccountRenterWzDepositEntity();
        accountRenterDeposit.setId(accountRenterDepositEntity.getId());
        accountRenterDeposit.setVersion(accountRenterDepositEntity.getVersion());
        //押金剩余金额  应退
        //accountRenterDeposit.setShouldReturnDeposit(accountRenterDepositEntity.getShishouDeposit() - Math.abs(detainRenterDepositReqVO.getAmt()));
        accountRenterDeposit.setShouldReturnDeposit(detainRenterDepositReqVO.getAmt());
        
        int result =  accountRenterWzDepositMapper.updateByPrimaryKeySelective(accountRenterDeposit);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }
    }
    
    /**
     * 更新违章押金 应退退金额
     * @param vo
     */
    public void updateRenterWZDeposit(OrderRenterDepositWZDetainReqVO vo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(vo.getOrderNo(),vo.getMemNo());
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterWZDepositException();
        }
        vo.setAmt(accountRenterDepositEntity.getShouldReturnDeposit());
        AccountRenterWzDepositEntity accountRenterDeposit = new AccountRenterWzDepositEntity();
        accountRenterDeposit.setId(accountRenterDepositEntity.getId());
        accountRenterDeposit.setVersion(accountRenterDepositEntity.getVersion());
//        accountRenterDeposit.setShouldReturnDeposit(0);
      //押金剩余金额  应退
        //accountRenterDeposit.setShouldReturnDeposit(accountRenterDepositEntity.getShishouDeposit() - Math.abs(vo.getAmt()));
        accountRenterDeposit.setShouldReturnDeposit(vo.getAmt());
        
        int result = accountRenterWzDepositMapper.updateByPrimaryKeySelective(accountRenterDeposit);
        if(result==0){
            throw new PayOrderRenterWZDepositException();
        }
    }

	
	public void updateOrderDepositSettle(String memNo, String orderNo) {
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
        if(Objects.isNull(accountRenterDepositEntity)){
            throw new PayOrderRenterWZDepositException();
        }
        AccountRenterWzDepositEntity entity = new AccountRenterWzDepositEntity();
        entity.setId(accountRenterDepositEntity.getId());
        entity.setVersion(accountRenterDepositEntity.getVersion());
        entity.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        entity.setSettleTime(LocalDateTime.now());
        int result = accountRenterWzDepositMapper.updateByPrimaryKeySelective(entity);
        if(result == 0){
            throw new PayOrderRenterWZDepositException();
        }
    }
	
}
