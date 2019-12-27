package com.atzuche.order.accountrenterdeposit.service;

import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositDetailNoTService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositRatioNotService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
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
    private AccountRenterDepositRatioNotService accountRenterDepositRatioNotService;
    /**
     * 查询押金状态信息
     */
    public AccountRenterDepositResVO getAccountRenterDepositEntity(String orderNo, String memNo){
        Assert.notNull(orderNo, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        return accountRenterDepositNoTService.getAccountRenterDepositEntity(orderNo,memNo);
    }
    /**
     * 查询车俩押金是否付清
     * @param orderNo
     * @param memNo
     * @return
     */
    public boolean isPayOffForRenterDeposit(String orderNo, String memNo) {
        AccountRenterDepositResVO accountRenterDepositRes = getAccountRenterDepositEntity(orderNo,memNo);
        // 1 记录不存在
        if(Objects.isNull(accountRenterDepositRes) || Objects.isNull(accountRenterDepositRes.getOrderNo())){
            return Boolean.FALSE;
        }
        //2开启免押
        if(YesNoEnum.YES.getCode()==accountRenterDepositRes.getIsFreeDeposit()){
            return Boolean.TRUE;
        }
        //3 实付 可能是getShifuDepositAmt ，预授权getAuthorizeDepositAmt 或者 信用支付getCreditPayAmt（） 均不为负数
        //应付 负数    相加大于等于0 表示 已经付过押金
        int yingfuAmt = accountRenterDepositRes.getYingfuDepositAmt();
        int shifuAmt = accountRenterDepositRes.getShifuDepositAmt() + accountRenterDepositRes.getAuthorizeDepositAmt()+accountRenterDepositRes.getCreditPayAmt();
        return yingfuAmt + shifuAmt>=0;
    }
    /**
     * 查询车辆押金余额
     */
    public int getSurplusRenterDeposit(String orderNo, String memNo) {
        //查询车辆押金信息
        AccountRenterDepositResVO accountRenterDepositRes = getAccountRenterDepositEntity(orderNo,memNo);
        //1 校验 是否存在车辆押金记录
        Assert.notNull(accountRenterDepositRes, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(accountRenterDepositRes.getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        //2 返回计算剩余押金余额
        int surplusRenterDeposit = accountRenterDepositRes.getSurplusDepositAmt() + accountRenterDepositRes.getSurplusAuthorizeDepositAmt() + accountRenterDepositRes.getSurplusCreditPayAmt();
        return surplusRenterDeposit;
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
        //3 记录减免押金详情
        if(createOrderRenterDepositReqVO.getReductionAmt()>0){
            accountRenterDepositRatioNotService.insertReductDeposit(createOrderRenterDepositReqVO.getCreateOrderRenterReductionDeposits());
        }

    }
    /**
     * 支付成功后记录实付押金信息 和押金资金进出信息
     */
    @CatAnnotation
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit){
        //1 参数校验
        Assert.notNull(payedOrderRenterDeposit, ErrorCode.PARAMETER_ERROR.getText());
        payedOrderRenterDeposit.check();
        //2更新押金 实付信息
        accountRenterDepositNoTService.updateRenterDeposit(payedOrderRenterDeposit);
        //添加押金资金进出明细
        accountRenterDepositDetailNoTService.insertRenterDepositDetail(payedOrderRenterDeposit.getDetainRenterDepositReqVO());
    }

    /**
     * 账户押金转出
     * @param detainRenterDepositReqVO
     */
    public void detainRenterDeposit(DetainRenterDepositReqVO detainRenterDepositReqVO) {
        //1 参数校验
        Assert.notNull(detainRenterDepositReqVO, ErrorCode.PARAMETER_ERROR.getText());
        detainRenterDepositReqVO.check();
        //2更新车辆押金  剩余押金 金额
        accountRenterDepositNoTService.updateRenterDepositChange(detainRenterDepositReqVO);
        //添加押金资金进出明细
        accountRenterDepositDetailNoTService.insertRenterDepositDetail(detainRenterDepositReqVO);
    }
}
