package com.atzuche.order.cashieraccount.service.notservice;

import com.atzuche.order.accountrenterdeposit.exception.AccountRenterCostException;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterCostReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import com.atzuche.order.accountrenterwzdepost.exception.AccountRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.exception.OrderPayCallBackAsnyException;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayAsynResVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.account.PayStatusEnum;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;


/**
 * 收银表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierNoTService {
    @Autowired
    private CashierMapper cashierMapper;

    /**
     * 收银台记录应收金额
     */
    public int getPayDeposit(String orderNo,String memNo,String payKind){
        CashierEntity cashierEntity = cashierMapper.getPayDeposit(orderNo,memNo,payKind);
        if(Objects.isNull(cashierEntity) || Objects.isNull(cashierEntity.getId())){
            return 0;
        }
        if(PayStatusEnum.PAYED.getCode().equals(cashierEntity.getTransStatus())){
            return 0;
        }
        return cashierEntity.getPayAmt() ;
    }

    /**
     * 收银台记录应收违章押金
     * @param createOrderRenterWZDepositReq
     */
    public void insertRenterWZDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq) {
        //1 校验
        Assert.notNull(createOrderRenterWZDepositReq, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterWZDepositReq.check();

        CashierEntity cashierEntity = new CashierEntity();
        BeanUtils.copyProperties(createOrderRenterWZDepositReq,cashierEntity);
        cashierEntity.setPayAmt(createOrderRenterWZDepositReq.getYingfuDepositAmt());
        int result = cashierMapper.insert(cashierEntity);
        if(result==0){
            throw new AccountRenterWZDepositException();
        }
    }
    /**
     * 收银台记录应收车俩押金
     * @param createOrderRenterDeposit
     */
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDeposit) {
        //1 校验
        Assert.notNull(createOrderRenterDeposit, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterDeposit.check();
        CashierEntity cashierEntity = new CashierEntity();
        BeanUtils.copyProperties(createOrderRenterDeposit,cashierEntity);
        cashierEntity.setPayAmt(createOrderRenterDeposit.getYingfuDepositAmt());
        int result = cashierMapper.insert(cashierEntity);
        if(result==0){
            throw new AccountRenterDepositDBException();
        }
    }

    /**
     * 下单成功  收银台落 收银记录（此时没有应付 ，应付从租车费用中取）
     * @param createOrderRenterCost
     */
    public void insertRenterCost(CreateOrderRenterCostReqVO createOrderRenterCost) {
        //1 校验
        Assert.notNull(createOrderRenterCost, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterCost.check();
        CashierEntity cashierEntity = new CashierEntity();
        BeanUtils.copyProperties(createOrderRenterCost,cashierEntity);
        cashierEntity.setPayAmt(NumberUtils.INTEGER_ZERO);
        int result = cashierMapper.insert(cashierEntity);
        if(result==0){
            throw new AccountRenterCostException();
        }
    }

    /**
     * 支付成功异步回调 车俩押金参数初始化
     * @param orderPayAsynVO
     * @return
     */
    public PayedOrderRenterDepositReqVO getPayedOrderRenterDepositReq(OrderPayAsynResVO orderPayAsynVO) {
        PayedOrderRenterDepositReqVO vo = new PayedOrderRenterDepositReqVO();
        BeanUtils.copyProperties(orderPayAsynVO,vo);
        vo.setPayStatus(PayStatusEnum.PAYED.getCode());
        int transStatus = StringUtil.isBlank(orderPayAsynVO.getTransStatus())?0:Integer.parseInt(orderPayAsynVO.getTransStatus());
        vo.setPayStatus(transStatus);
        vo.setPayTime(LocalDateTimeUtils.parseStringToDateTime(orderPayAsynVO.getOrderTime(),LocalDateTimeUtils.DEFAULT_PATTERN));
        //"01"：消费
        if(DataPayTypeConstant.PAY_PUR.equals(orderPayAsynVO.getPayType())){
            vo.setShifuDepositAmt(orderPayAsynVO.getSettleAmount());
            vo.setSurplusDepositAmt(orderPayAsynVO.getSettleAmount());
        }
        //"02"：预授权
        if(DataPayTypeConstant.PAY_PRE.equals(orderPayAsynVO.getPayType())){
            vo.setAuthorizeDepositAmt(orderPayAsynVO.getSettleAmount());
            vo.setSurplusAuthorizeDepositAmt(orderPayAsynVO.getSettleAmount());
        }
        //TODO 预授权到期时间
        //车辆押金进出明细
        DetainRenterDepositReqVO detainRenterDeposit = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(orderPayAsynVO,detainRenterDeposit);
        detainRenterDeposit.setAmt(orderPayAsynVO.getSettleAmount());
        detainRenterDeposit.setUniqueNo(orderPayAsynVO.getQn());
        detainRenterDeposit.setRenterCashCodeEnum(RenterCashCodeEnum.CASHIER_RENTER_DEPOSIT);
        vo.setDetainRenterDepositReqVO(detainRenterDeposit);
        return vo;
    }

    /**
     * 更新收银台租车押金已支付
     * @param orderPayAsynVO
     */
    public void updataCashier(OrderPayAsynResVO orderPayAsynVO) {
        CashierEntity cashierEntity = cashierMapper.selectByPrimaryKey(orderPayAsynVO.getPayId());
        int result =0;
         if(Objects.nonNull(cashierEntity) && Objects.nonNull(cashierEntity.getId())){
             CashierEntity cashier = new CashierEntity();
             BeanUtils.copyProperties(orderPayAsynVO,cashier);
             cashier.setId(cashierEntity.getId());
             cashier.setVersion(cashierEntity.getVersion());
             result = cashierMapper.updateByPrimaryKeySelective(cashier);
         }else {
             CashierEntity cashier = new CashierEntity();
             BeanUtils.copyProperties(orderPayAsynVO,cashier);
             result = cashierMapper.insert(cashier);
         }
         if(result == 0){
            throw new OrderPayCallBackAsnyException();
         }
    }
    /**
     * 支付成功异步回调 违章押金参数初始化
     * @param orderPayAsynVO
     * @return
     */
    public PayedOrderRenterWZDepositReqVO getPayedOrderRenterWZDepositReq(OrderPayAsynResVO orderPayAsynVO) {
        PayedOrderRenterWZDepositReqVO vo = new PayedOrderRenterWZDepositReqVO();
        BeanUtils.copyProperties(orderPayAsynVO,vo);
        vo.setShishouDeposit(orderPayAsynVO.getSettleAmount());

        //违章押金进出明细
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterDepositDetail = new PayedOrderRenterDepositWZDetailReqVO();
        BeanUtils.copyProperties(orderPayAsynVO,payedOrderRenterDepositDetail);
        payedOrderRenterDepositDetail.setUniqueNo(orderPayAsynVO.getQn());
        payedOrderRenterDepositDetail.setRenterCashCodeEnum(RenterCashCodeEnum.CASHIER_RENTER_WZ_DEPOSIT);
        payedOrderRenterDepositDetail.setPayChannel(orderPayAsynVO.getPaySource());
        payedOrderRenterDepositDetail.setPayment(orderPayAsynVO.getPayType());
        payedOrderRenterDepositDetail.setAmt(orderPayAsynVO.getSettleAmount());
        vo.setPayedOrderRenterDepositDetailReqVO(payedOrderRenterDepositDetail);
        return vo;
    }

    /**
     * 支付成功异步回调 补补租车费用回调
     * @param orderPayAsynVO
     * @return
     */
    public AccountRenterCostReqVO getAccountRenterCostReq(OrderPayAsynResVO orderPayAsynVO) {
        AccountRenterCostReqVO vo = new AccountRenterCostReqVO();
        BeanUtils.copyProperties(orderPayAsynVO,vo);
        vo.setShifuAmt(orderPayAsynVO.getSettleAmount());
        //费用明细
        AccountRenterCostDetailReqVO accountRenterCostDetail = new AccountRenterCostDetailReqVO();
        BeanUtils.copyProperties(orderPayAsynVO,accountRenterCostDetail);
        accountRenterCostDetail.setPaySource(orderPayAsynVO.getPaySource());
        accountRenterCostDetail.setPayType(orderPayAsynVO.getPayType());
        accountRenterCostDetail.setTime(LocalDateTimeUtils.parseStringToDateTime(orderPayAsynVO.getOrderTime(),LocalDateTimeUtils.DEFAULT_PATTERN));
        accountRenterCostDetail.setAmt(orderPayAsynVO.getSettleAmount());
        accountRenterCostDetail.setRenterCashCodeEnum(RenterCashCodeEnum.CASHIER_RENTER_AGAIN_COST);
        return vo;
    }
    /**
     * 计算租车费用应付
     * @param payableVOs
     * @return
     */
    public int sumRentOrderCost(List<PayableVO> payableVOs) {
        int amt = NumberUtils.INTEGER_ZERO;
        if(!CollectionUtils.isEmpty(payableVOs)){
            return payableVOs.stream().mapToInt(PayableVO::getAmt).sum();
        }
        return amt;
    }


}
