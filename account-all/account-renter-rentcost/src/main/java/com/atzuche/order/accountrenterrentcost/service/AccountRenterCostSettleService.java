package com.atzuche.order.accountrenterrentcost.service;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostRefundException;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostSettleMapper;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleNoTService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostChangeReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostToFineReqVO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * 租客费用及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleService{
    @Autowired private AccountRenterCostSettleNoTService accountRenterCostSettleNoTService;
    @Autowired private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired private AccountRenterCostSettleMapper accountRenterCostSettleMapper;
    
    /**
     * 查询订单 已付租车费用
     */
    public int getCostPaidRent(String orderNo,String memNo) {
        Assert.notNull(orderNo, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        return accountRenterCostSettleNoTService.getCostPaidRent(orderNo,memNo);
    }

    /**
     * 收银台支付成功  实收租车费用落库
     */
    public void insertRenterCostDetail(AccountRenterCostReqVO accountRenterCostReqVO){
        //1 参数校验
        Assert.notNull(accountRenterCostReqVO, ErrorCode.PARAMETER_ERROR.getText());
        accountRenterCostReqVO.check();
        //2 实付租车费用落库
        accountRenterCostSettleNoTService.insertOrUpdateRenterCostSettle(accountRenterCostReqVO);
        //3租车费用明细落库
        accountRenterCostDetailNoTService.insertAccountRenterCostDetail(accountRenterCostReqVO.getAccountRenterCostDetailReqVO());
    }

    /**
     * 退还多付的费用
     * @param accountRenterCostDetail
     */
    public int refundRenterCostDetail(AccountRenterCostDetailReqVO accountRenterCostDetail) {
        //1 校验
        //1 参数校验
        Assert.notNull(accountRenterCostDetail, ErrorCode.PARAMETER_ERROR.getText());
        accountRenterCostDetail.check();
        AccountRenterCostSettleEntity accountRenterCostSettle = accountRenterCostSettleNoTService.getCostPaidRentSettle(accountRenterCostDetail.getOrderNo(),accountRenterCostDetail.getMemNo());
       if(Objects.isNull(accountRenterCostSettle)){
            throw new AccountRenterRentCostRefundException();
       }
       //2 更新已退还金额
        accountRenterCostSettleNoTService.refundRenterCostSettle(accountRenterCostSettle,accountRenterCostDetail.getAmt());
       //3 记录退款费用记录
       return accountRenterCostDetailNoTService.insertAccountRenterCostDetail(accountRenterCostDetail);
    }

    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo){
        return accountRenterCostDetailNoTService.getAccountRenterCostDetailsByOrderNo(orderNo);
    }

    /**
     * 插入租客资金进出明细
     * @param accountRenterCostChangeReqVO
     */
    public int deductDepositToRentCost(AccountRenterCostDetailReqVO accountRenterCostChangeReqVO) {
        return accountRenterCostDetailNoTService.insertAccountRenterCostDetail(accountRenterCostChangeReqVO);
    }


    /**
     * 租车费用转 罚金 记录 租车费用资金流水
     * @param vo
     */
    public void deductRentCostToRentFine(AccountRenterCostToFineReqVO vo) {
        AccountRenterCostDetailEntity accountRenterCostDetail = new AccountRenterCostDetailEntity();
        accountRenterCostDetail.setOrderNo(vo.getOrderNo());
        accountRenterCostDetail.setMemNo(vo.getMemNo());
        accountRenterCostDetail.setAmt(vo.getAmt());
        accountRenterCostDetail.setSourceCode(RenterCashCodeEnum.SETTLE_RENT_COST_TO_FINE.getCashNo());
        accountRenterCostDetail.setSourceDetail(RenterCashCodeEnum.SETTLE_RENT_COST_TO_FINE.getTxt());
        accountRenterCostDetail.setPaySource(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getTxt());
        accountRenterCostDetail.setPaySourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo());
        accountRenterCostDetailNoTService.insertAccountRenterCostDetailEntity(accountRenterCostDetail);

    }
    /**
     * 钱包支付金额 抵扣 罚金 记录 租车费用资金流水
     * @param vo
     */
    public void deductWalletCostToRentFine(AccountRenterCostToFineReqVO vo) {
        AccountRenterCostDetailEntity accountRenterCostDetail = new AccountRenterCostDetailEntity();
        accountRenterCostDetail.setOrderNo(vo.getOrderNo());
        accountRenterCostDetail.setMemNo(vo.getMemNo());
        accountRenterCostDetail.setAmt(vo.getAmt());
        accountRenterCostDetail.setSourceCode(RenterCashCodeEnum.SETTLE_RENT_WALLET_COST_TO_FINE.getCashNo());
        accountRenterCostDetail.setSourceDetail(RenterCashCodeEnum.SETTLE_RENT_WALLET_COST_TO_FINE.getTxt());
        accountRenterCostDetail.setPaySource(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getTxt());
        accountRenterCostDetail.setPaySourceCode(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo());
        accountRenterCostDetailNoTService.insertAccountRenterCostDetailEntity(accountRenterCostDetail);
    }
    
    /**
     * 查询结算对象
     * @param orderNo
     * @param memNo
     * @return
     */
    public AccountRenterCostSettleEntity selectByOrderNo(String orderNo,String memNo) {
    	return accountRenterCostSettleMapper.selectByOrderNo(orderNo, memNo);
    }
    
}
