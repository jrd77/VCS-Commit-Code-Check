package com.atzuche.order.accountrenterwzdepost.service;

import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.accountrenterwzdepost.vo.req.CreateOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


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
        return accountRenterWzDepositNoTService.getAccountRenterWZDeposit(orderNo,memNo);
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
        payedOrderWZRenterDeposit.check();
        //2更新押金 实付信息
        accountRenterWzDepositNoTService.updateRenterDeposit(payedOrderWZRenterDeposit);
        //添加押金资金进出明细
        accountRenterWzDepositDetailNoTService.insertRenterWZDepositDetail(payedOrderWZRenterDeposit.getPayedOrderRenterDepositDetailReqVO());
    }
    /**
     * 支户头违章押金资金进出 操作
     */
    public void updateRenterWZDepositChange(PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail){
        //1 参数校验
        Assert.notNull(payedOrderRenterWZDepositDetail, ErrorCode.PARAMETER_ERROR.getText());
        payedOrderRenterWZDepositDetail.check();
        //2更新车辆押金  剩余押金 金额
        accountRenterWzDepositNoTService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
        //添加押金资金进出明细
        accountRenterWzDepositDetailNoTService.insertRenterWZDepositDetail(payedOrderRenterWZDepositDetail);
    }

}
