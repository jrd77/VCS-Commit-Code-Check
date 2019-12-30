package com.atzuche.order.cashieraccount.service.notservice;

import com.atzuche.order.accountrenterdeposit.exception.AccountRenterCostException;
import com.atzuche.order.accountrenterdeposit.exception.AccountRenterDepositDBException;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterCostReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import com.atzuche.order.accountrenterwzdepost.exception.AccountRenterWZDepositException;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.exception.OrderPayCallBackAsnyException;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayAsynResVO;
import com.atzuche.order.commons.IpUtil;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.account.PayStatusEnum;
import com.atzuche.order.commons.enums.cashier.PlatformEnum;
import com.atzuche.order.commons.enums.cashier.WalletFlagEnum;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.autopay.gateway.constant.DataAppIdConstant;
import com.autoyol.autopay.gateway.constant.DataPayEnvConstant;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.vo.req.WalletDeductionReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    AccountRenterDepositService accountRenterDepositService;
    @Autowired
    AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired
    AccountRenterCostSettleService accountRenterCostSettleService;


    /**
     * 收银台根据主单号 向订单模块查询子单号
     * @param orderNo
     * @return
     */
    public String getRenterOrderNoByOrderNo(String orderNo){
        RenterOrderEntity renterOrderEntity =  renterOrderService.getRenterOrderByOrderNoAndWaitPay(orderNo);
        if(Objects.isNull(renterOrderEntity) || StringUtil.isBlank(renterOrderEntity.getRenterOrderNo())){
            throw new AccountRenterWZDepositException();
        }
        return renterOrderEntity.getRenterOrderNo();
    }

    /**
     * 收银台记录应收金额
     */
    public int getPayDeposit(String orderNo,String memNo,String payKind){
        CashierEntity cashierEntity = cashierMapper.getPayDeposit(orderNo,memNo,payKind,DataPayTypeConstant.PAY_PUR);
        if(Objects.isNull(cashierEntity) || Objects.isNull(cashierEntity.getId())){
            return 0;
        }
        if(PayStatusEnum.PAYED.getCode().equals(cashierEntity.getTransStatus())){
            return 0;
        }
        return cashierEntity.getPayAmt() ;
    }

    /**
     * 收银台记录
     */
    public CashierEntity getCashierEntity(String orderNo,String memNo,String payKind){
        CashierEntity cashierEntity = cashierMapper.getPayDeposit(orderNo,memNo,payKind,DataPayTypeConstant.PAY_PUR);
        return cashierEntity;
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
             cashier.setPaySn(cashierEntity.getPaySn()+1);
             if("00".equals(orderPayAsynVO.getTransStatus()) && cashierEntity.getTransStatus().equals(orderPayAsynVO.getTransStatus())){
                 result = cashierMapper.updateByPrimaryKeySelective(cashier);
             }

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
     * 支付成功异步回调 补付租车费用回调
     * @param orderPayAsynVO
     * @return
     */
    public AccountRenterCostReqVO getAccountRenterCostReq(OrderPayAsynResVO orderPayAsynVO,RenterCashCodeEnum renterCashCodeEnum) {
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
        accountRenterCostDetail.setRenterCashCodeEnum(renterCashCodeEnum);
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

    /**
     * 构造远程扣减钱包参数
     */
    public WalletDeductionReqVO getWalletDeductionReqVO(OrderPaySignReqVO orderPaySign, OrderPayableAmountResVO payVO,int num) {
        WalletDeductionReqVO vo = new WalletDeductionReqVO();
        vo.setIp(IpUtil.getLocalIp());
        vo.setAmt(payVO.getAmtWallet());
        vo.setOrderNo(Long.parseLong(orderPaySign.getOrderNo()));
        vo.setMemNo(Integer.valueOf(orderPaySign.getMenNo()));
        vo.setFlag(WalletFlagEnum.RENTER_CONSUME.getCode());
        vo.setFlagTxt(WalletFlagEnum.RENTER_CONSUME.getText());
        vo.setServiceName("order-center");
        vo.setPlatform(PlatformEnum.OPERATION_TERMINAL.name());
        vo.setOperator(orderPaySign.getOperator());
        vo.setOperatorName(orderPaySign.getOperatorName());
        vo.setNum(num);
        return vo;
    }

    /**
     *  钱包支付落库 收银台 记录总金额/  租客 记录 流水（分为 租车消费 租车消费补付）
     * @param payBalance
     * @param orderPaySign
     * @return
     */
    public int payOrderByWallet(int payBalance, OrderPaySignReqVO orderPaySign, OrderPayableAmountResVO payVO) {
        //取出所有户头 钱包支付款项
        List<String> payKinds = orderPaySign.getPayKind();
        int num = 0;
        if (!CollectionUtils.isEmpty(payKinds)) {
            //1 钱包 优先抵扣  支付租车费用
            if (payKinds.contains(DataPayKindConstant.TK_FEE)) {
                CashierEntity cashierEntity = cashierMapper.getPayDeposit(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),DataPayKindConstant.TK_FEE,"00");
                if(Objects.nonNull(cashierEntity) && Objects.nonNull(cashierEntity.getPaySn())){
                    num = cashierEntity.getPaySn() + 1;
                    CashierEntity cashier = new CashierEntity ();
                    cashier.setId(cashierEntity.getId());
                    cashier.setPaySn(num);
                    int result = cashierMapper.updateByPrimaryKeySelective(cashier);
                    if(result ==0){
                        throw new AccountRenterDepositDBException();
                    }
                }
                //计算抵扣钱包金额 amt 负值
                int amt = payVO.getAmtRent() + payBalance < 0 ? -payBalance : payVO.getAmtRent();
                //计算剩余待支付租车费用 amtRent 负值
                int amtRent = payVO.getAmtRent() + payBalance < 0 ? payVO.getAmtRent() + payBalance : 0;
                payVO.setAmtRent(amtRent);
                payVO.setAmt(payVO.getAmt() + amt);
                payVO.setAmtWallet(amt);
            }
        }
        return num;
    }

    /**
     * 构造参数 PayVo (押金、违章押金)
     * @param cashierEntity
     * @param orderPaySign
     * @param payVO
     * @return
     */
    public PayVo getPayVO(CashierEntity cashierEntity,OrderPaySignReqVO orderPaySign,OrderPayableAmountResVO payVO,String payKind) {
        PayVo vo = new PayVo();
        vo.setInternalNo(String.valueOf(cashierEntity.getPaySn()));
        vo.setExtendParams(GsonUtils.toJson(cashierEntity));
        vo.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
        vo.setMemNo(orderPaySign.getMenNo());
        vo.setOrderNo(orderPaySign.getOrderNo());
        vo.setOpenId(orderPaySign.getOpenId());
        vo.setOS(orderPaySign.getOS());
        vo.setPayAmt(String.valueOf(Math.abs(payVO.getAmt())));
        vo.setPayEnv(StringUtil.isBlank(orderPaySign.getPayEnv())? DataPayEnvConstant.PRO:orderPaySign.getPayEnv());
        vo.setPayId(cashierEntity.getId().toString());
        vo.setPayKind(payKind);
        vo.setPaySn(cashierEntity.getPaySn().toString());
        vo.setPaySource(orderPaySign.getPaySource());
        vo.setPayTitle(payVO.getTitle());
        vo.setPayType(orderPaySign.getPayType());
        vo.setReqIp(IpUtil.getLocalIp());
        return vo;
    }

    /**
     *  租车费用
     * @param orderPaySign
     * @param payVO
     * @return
     */
    public PayVo getPayVOByRentCost(OrderPaySignReqVO orderPaySign, OrderPayableAmountResVO payVO) {
        PayVo vo = new PayVo();
        return vo;
    }

    /**
     * 租车押金 收银台回调
     * @param orderPayAsynVO
     * @param payedOrderRenterDeposit
     */
    public void updataCashierAndRenterDeposit(OrderPayAsynResVO orderPayAsynVO, PayedOrderRenterDepositReqVO payedOrderRenterDeposit) {
        //1更新收银台
        updataCashier(orderPayAsynVO);
        //2 租车押金 更新数据
        accountRenterDepositService.updateRenterDeposit(payedOrderRenterDeposit);

    }
    /**
     * 违章押金 收银台回调
     * @param orderPayAsynVO
     * @param payedOrderRenterWZDeposit
     */
    public void updataCashierAndRenterWzDeposit(OrderPayAsynResVO orderPayAsynVO, PayedOrderRenterWZDepositReqVO payedOrderRenterWZDeposit) {
        //1更新收银台
        updataCashier(orderPayAsynVO);
        //2 违章押金 更新数据
        accountRenterWzDepositService.updateRenterWZDeposit(payedOrderRenterWZDeposit);
    }

    /**
     * 租车费用/补付租车费用
     * @param orderPayAsynVO
     * @param accountRenterCostReq
     */
    public void updataCashierAndRenterCost(OrderPayAsynResVO orderPayAsynVO,AccountRenterCostReqVO accountRenterCostReq) {
        //1更新收银台
        updataCashier(orderPayAsynVO);
        //2  实收租车费用落库 更新数据
        accountRenterCostSettleService.insertRenterCostDetail(accountRenterCostReq);
    }
}
