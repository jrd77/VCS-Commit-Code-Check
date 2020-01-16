package com.atzuche.order.accountrenterwzdepost.service;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterCancelWZDepositCostReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;


/**
 * 违章押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositService{
    @Autowired
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;

    /**
     * 查询违章押金信息
     */
    public AccountRenterWZDepositResVO getAccountRenterWZDeposit(String orderNo, String memNo){
        Assert.notNull(orderNo, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        AccountRenterWZDepositResVO  result = new AccountRenterWZDepositResVO();
        AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositNoTService.getAccountRenterWZDeposit(orderNo,memNo);
        if(Objects.nonNull(accountRenterDepositEntity)){
            BeanUtils.copyProperties(accountRenterDepositEntity,result);
        }
        return result;
    }
    /**
     * 查询违章押金是否付清
     */
    public boolean isPayOffForRenterWZDeposit(String orderNo, String memNo) {
        AccountRenterWZDepositResVO accountRenterWZDepositRes = getAccountRenterWZDeposit(orderNo,memNo);
        // 1 记录不存在
        if(Objects.isNull(accountRenterWZDepositRes) || Objects.isNull(accountRenterWZDepositRes.getOrderNo())){
            return Boolean.FALSE;
        }
        //2开启免疫
        if(YesNoEnum.YES.getCode()==accountRenterWZDepositRes.getIsFreeDeposit()){
            return Boolean.TRUE;
        }
        //3 应付 和实付
        int yingfuAmt = accountRenterWZDepositRes.getYingshouDeposit();
        int shifuAmt = accountRenterWZDepositRes.getShishouDeposit();
        return shifuAmt+yingfuAmt>=0;
    }
    /**
     * 查询违章押金余额
     */
    public int getRenterWZDeposit(String orderNo, String memNo) {
        //查询车辆押金信息
        AccountRenterWZDepositResVO accountRenterDepositRes = getAccountRenterWZDeposit(orderNo,memNo);
        //1 校验 是否存在车辆押金记录
        Assert.notNull(accountRenterDepositRes, ErrorCode.PARAMETER_ERROR.getText());
        Assert.notNull(accountRenterDepositRes.getOrderNo(), ErrorCode.PARAMETER_ERROR.getText());
        //2 返回计算剩余押金余额  即应退余额
        Integer amt = accountRenterDepositRes.getYingshouDeposit();
        amt = Objects.isNull(amt)?0:amt;
        return amt;
    }

    /**
     * 下单成功记录应付违章押金
     */
    public void insertRenterWZDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq){
        //1 校验
        Assert.notNull(createOrderRenterWZDepositReq, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterWZDepositReq.check();
        accountRenterWzDepositNoTService.insertRenterWZDeposit(createOrderRenterWZDepositReq);
    }

    /**
     * 支付成功后记录实付违章押金信息 和违章押金资金进出信息
     */
    public void updateRenterWZDeposit(PayedOrderRenterWZDepositReqVO payedOrderWZRenterDeposit){
        //1 参数校验
        Assert.notNull(payedOrderWZRenterDeposit, ErrorCode.PARAMETER_ERROR.getText());
//        payedOrderWZRenterDeposit.check();
        //2更新押金 实付信息
        accountRenterWzDepositNoTService.updateRenterDeposit(payedOrderWZRenterDeposit);
        //添加押金资金进出明细
        accountRenterWzDepositDetailNoTService.insertRenterWZDepositDetail(payedOrderWZRenterDeposit.getPayedOrderRenterDepositDetailReqVO());
    }
    /**
     * 支户头违章押金资金进出 操作
     */
    public int  updateRenterWZDepositChange(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail){
        //1 参数校验
        Assert.notNull(payedOrderRenterWZDepositDetail, ErrorCode.PARAMETER_ERROR.getText());
        payedOrderRenterWZDepositDetail.check();
        //2更新车辆押金  剩余押金 金额
        accountRenterWzDepositNoTService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
        //添加押金资金进出明细
        return accountRenterWzDepositDetailNoTService.insertRenterWZDepositDetail(payedOrderRenterWZDepositDetail);
    }

    /**
     * 违章押金 抵扣罚金
     * @param vo
     */
    public void deductRentWzDepositToRentFine(RenterCancelWZDepositCostReqVO vo) {
        AccountRenterWzDepositDetailEntity accountRenterDepositDetailEntity = new AccountRenterWzDepositDetailEntity();
        accountRenterDepositDetailEntity.setOrderNo(vo.getOrderNo());
        accountRenterDepositDetailEntity.setMemNo(vo.getMemNo());
        accountRenterDepositDetailEntity.setAmt(vo.getAmt());
        accountRenterDepositDetailEntity.setSourceCode(RenterCashCodeEnum.SETTLE_RENT_WZ_DEPOSIT_COST_TO_FINE.getCashNo());
        accountRenterDepositDetailEntity.setSourceDetail(RenterCashCodeEnum.SETTLE_RENT_WZ_DEPOSIT_COST_TO_FINE.getTxt());
        accountRenterWzDepositDetailNoTService.insertRenterDepositDetailEntity(accountRenterDepositDetailEntity);
    }
}
