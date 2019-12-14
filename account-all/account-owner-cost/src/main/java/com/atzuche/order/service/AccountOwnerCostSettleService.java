package com.atzuche.order.service;

import com.atzuche.order.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.service.notservice.AccountOwnerCostSettleNoTService;
import com.atzuche.order.vo.req.AccountOwnerCostSettleReqVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 *   车主结算费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
 */
@Service
@Slf4j
public class AccountOwnerCostSettleService{
    @Autowired
    private AccountOwnerCostSettleNoTService accountOwnerCostSettleNoTService;
    @Autowired
    private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;


    /**
     * 车主结算信息插入
     * @param accountOwnerCostSettleReqVO
     */
    public void insertAccountOwnerCostSettle(AccountOwnerCostSettleReqVO accountOwnerCostSettleReqVO){
        log.info("AccountOwnerCostSettleService insertAccountOwnerCostSettle param", GsonUtils.toJson(accountOwnerCostSettleReqVO));
        //1 校验
        Assert.notNull(accountOwnerCostSettleReqVO, ErrorCode.PARAMETER_ERROR.getText());
        accountOwnerCostSettleReqVO.check();
        //2 费用结算插入 车主结算总表
        accountOwnerCostSettleNoTService.insertAccountOwnerCostSettle(accountOwnerCostSettleReqVO);
        //3 费用结算插入 车主结算明细表
        accountOwnerCostSettleDetailNoTService.insertAccountOwnerCostSettleDetail(accountOwnerCostSettleReqVO.getAccountOwnerCostSettleDetailReqVO());

    }
}
