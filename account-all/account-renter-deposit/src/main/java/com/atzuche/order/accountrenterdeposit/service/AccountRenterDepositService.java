package com.atzuche.order.accountrenterdeposit.service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositMapper;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositDetailNoTService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.OrderCancelRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 租车押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Service
public class AccountRenterDepositService{
    @Autowired
    private AccountRenterDepositDetailNoTService accountRenterDepositDetailNoTService;
    @Autowired
    private AccountRenterDepositNoTService accountRenterDepositNoTService;
    @Autowired
    public AccountRenterDepositMapper accountRenterDepositMapper;



    /*
     * @Author ZhangBin
     * @Date 2020/1/11 16:25
     * @Description: 根据订单号查询押金信息
     *
     **/
    public AccountRenterDepositEntity selectByOrderNo(String orderNo){
        return accountRenterDepositMapper.selectByOrderNo(orderNo);
    }

    /**
     * 查询押金状态信息
     */
    public AccountRenterDepositResVO getAccountRenterDepositEntity(String orderNo, String memNo){
        Assert.notNull(orderNo, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());

        AccountRenterDepositResVO  result = new AccountRenterDepositResVO();
        AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositNoTService.getAccountRenterDepositEntity(orderNo,memNo);
        if(Objects.nonNull(accountRenterDepositEntity)){
            BeanUtils.copyProperties(accountRenterDepositEntity,result);
        }
        return result;
    }

    /**
     * 查询车辆押金余额
     */
    public int getRenterDeposit(String orderNo, String memNo) {
        //查询车辆押金信息
        AccountRenterDepositResVO accountRenterDepositRes = getAccountRenterDepositEntity(orderNo,memNo);
        //1 校验 是否存在车辆押金记录
        Assert.notNull(accountRenterDepositRes, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(accountRenterDepositRes.getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        //2 返回计算剩余押金余额
        int amt = accountRenterDepositRes.getYingfuDepositAmt();
        return amt;
    }
    /**
     * 下单成功记录应付押金
     */
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO){
        //1 校验
        Assert.notNull(createOrderRenterDepositReqVO, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterDepositReqVO.check();
        //2 记录应付 减免押金
        accountRenterDepositNoTService.insertRenterDeposit(createOrderRenterDepositReqVO);


    }
    /**
     * 支付成功后记录实付押金信息 和押金资金进出信息
     */
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit){
        //1 参数校验
        Assert.notNull(payedOrderRenterDeposit, ErrorCode.PARAMETER_ERROR.getText());
        //payedOrderRenterDeposit.check();
        //2更新押金 实付信息
        accountRenterDepositNoTService.updateRenterDeposit(payedOrderRenterDeposit);
        //添加押金资金进出明细
        accountRenterDepositDetailNoTService.insertRenterDepositDetail(payedOrderRenterDeposit.getDetainRenterDepositReqVO());
    }

    /**
     * 账户押金转出
     * @param detainRenterDepositReqVO
     */
    public int detainRenterDeposit(DetainRenterDepositReqVO detainRenterDepositReqVO) {
        //1 参数校验
        Assert.notNull(detainRenterDepositReqVO, ErrorCode.PARAMETER_ERROR.getText());
        detainRenterDepositReqVO.check();
        //2更新车辆押金  剩余押金 金额
        accountRenterDepositNoTService.updateRenterDepositChange(detainRenterDepositReqVO);
        //添加押金资金进出明细
        return accountRenterDepositDetailNoTService.insertRenterDepositDetail(detainRenterDepositReqVO);
    }
    
    public int detainRenterWzDeposit(DetainRenterDepositReqVO detainRenterDepositReqVO) {
        //1 参数校验
        Assert.notNull(detainRenterDepositReqVO, ErrorCode.PARAMETER_ERROR.getText());
        detainRenterDepositReqVO.check();
        //2更新车辆押金  剩余押金 金额
        accountRenterDepositNoTService.updateRenterDepositChange(detainRenterDepositReqVO);
        //添加押金资金进出明细
        int id = accountRenterDepositDetailNoTService.insertRenterDepositDetail(detainRenterDepositReqVO);
        return id;
    }
    

    public void updateRenterDepositUniqueNo(String uniqueNo, int renterDepositDetailId) {
        accountRenterDepositDetailNoTService.updateRenterDepositUniqueNo(uniqueNo,renterDepositDetailId);
    }

    /**
     * 车俩押金结算 状态更新
     * @param detainRenterDepositReqVO
     */
    public void updateOrderDepositSettle(DetainRenterDepositReqVO detainRenterDepositReqVO) {
        accountRenterDepositNoTService.updateOrderDepositSettle(detainRenterDepositReqVO.getMemNo(),detainRenterDepositReqVO.getOrderNo());
    }


    /**
     * 车俩押金抵扣 罚金
     * @param vo
     */
    public void deductRentDepositToRentFine(OrderCancelRenterDepositReqVO vo) {
        AccountRenterDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterDepositDetailEntity();
        accountRenterDepositDetailEntity.setOrderNo(vo.getOrderNo());
        accountRenterDepositDetailEntity.setMemNo(vo.getMemNo());
        accountRenterDepositDetailEntity.setAmt(vo.getAmt());
        accountRenterDepositDetailEntity.setSourceCode(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_COST_TO_FINE.getCashNo());
        accountRenterDepositDetailEntity.setSourceDetail(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_COST_TO_FINE.getTxt());
        accountRenterDepositDetailNoTService.insertRenterDepositDetailEntity(accountRenterDepositDetailEntity);

        AccountRenterDepositEntity entity = accountRenterDepositNoTService.getAccountRenterDepositEntity(vo.getOrderNo(),vo.getMemNo());
        if(Objects.nonNull(entity) && Objects.nonNull(entity.getMemNo())){
            entity.setSurplusDepositAmt(entity.getSurplusDepositAmt() + vo.getAmt());
            accountRenterDepositNoTService.updateRenterDepositEntity(entity);
        }
    }
}
