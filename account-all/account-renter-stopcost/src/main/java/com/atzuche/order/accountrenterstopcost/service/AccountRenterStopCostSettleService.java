package com.atzuche.order.accountrenterstopcost.service;

import com.atzuche.order.accountrenterstopcost.service.notservice.AccountRenterStopCostDetailNoTService;
import com.atzuche.order.accountrenterstopcost.service.notservice.AccountRenterStopCostSettleNoTService;
import com.atzuche.order.accountrenterstopcost.vo.req.AccountRenterStopCostDetailReqVO;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterstopcost.mapper.AccountRenterStopCostSettleMapper;
import org.springframework.util.Assert;


/**
 * 停运费状态及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Service
public class AccountRenterStopCostSettleService{
    @Autowired
    private AccountRenterStopCostDetailNoTService accountRenterStopCostDetailNoTService;
    @Autowired
    private AccountRenterStopCostSettleNoTService accountRenterStopCostSettleNoTService;

    /**
     * 查询 停运费总和
     */
    @CatAnnotation
    public int getRenterStopCostAmt(String memNo){
        return accountRenterStopCostSettleNoTService.getRenterStopCostAmt(memNo);
    }

    /**
     *  停运费资金进出
     */
    @CatAnnotation
    public void changeRenteStopCostCost(AccountRenterStopCostDetailReqVO accountRenterStopCostDetail){
        //1校验
        Assert.notNull(accountRenterStopCostDetail, ErrorCode.PARAMETER_ERROR.getText());
        accountRenterStopCostDetail.check();
        //2 更新停运费用总额
        accountRenterStopCostSettleNoTService.changeRenterClaimCost(accountRenterStopCostDetail);
        //3 记录停运费账户费用流水
        accountRenterStopCostDetailNoTService.insertCostDetail(accountRenterStopCostDetail);
    }

}
