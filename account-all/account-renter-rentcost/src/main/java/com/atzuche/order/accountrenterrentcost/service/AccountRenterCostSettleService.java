package com.atzuche.order.accountrenterrentcost.service;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostRefundException;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleNoTService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostChangeReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
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
    @Autowired private AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;

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
    public void refundRenterCostDetail(AccountRenterCostDetailReqVO accountRenterCostDetail) {
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
        accountRenterCostDetailNoTService.insertAccountRenterCostDetail(accountRenterCostDetail);
    }

    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo){
        return accountRenterCostDetailNoTService.getAccountRenterCostDetailsByOrderNo(orderNo);
    }

    /**
     * 结算时候，
     * 1应付金额大于实付金额，订单存在租车费用欠款，车辆押金抵扣
     * 2实付大于应付 且存在历史欠款 费用还历史欠款，租客费用记录
     * @param accountRenterCostChangeReqVO
     */
    public int deductDepositToRentCost(AccountRenterCostChangeReqVO accountRenterCostChangeReqVO) {
        // 1 校验
        AccountRenterCostSettleEntity entity =  accountRenterCostSettleNoTService.getCostPaidRentSettle(accountRenterCostChangeReqVO.getOrderNo(),accountRenterCostChangeReqVO.getMemNo());
        Assert.notNull(entity, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(entity.getId(), ErrorCode.PARAMETER_ERROR.getText());
        //2 插入租客费用流水
        AccountRenterCostSettleDetailEntity accountRenterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(accountRenterCostChangeReqVO,accountRenterCostSettleDetail);
        int id = accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(accountRenterCostSettleDetail);
        //3 更新租客总实付
        AccountRenterCostSettleEntity accountRenterCostSettle = new AccountRenterCostSettleEntity();
        accountRenterCostSettle.setId(entity.getId());
        accountRenterCostSettle.setVersion(entity.getVersion());
        accountRenterCostSettle.setShifuAmt(accountRenterCostSettle.getShifuAmt() + accountRenterCostSettleDetail.getAmt());
        accountRenterCostSettleNoTService.updateAccountRenterCostSettle(accountRenterCostSettle);
        return id;

    }


}
