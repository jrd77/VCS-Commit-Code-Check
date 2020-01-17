package com.atzuche.order.accountownercost.service;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import com.atzuche.order.accountownercost.mapper.AccountOwnerCostSettleMapper;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleNoTService;
import com.atzuche.order.accountownercost.vo.req.AccountOwnerCostSettleReqVO;
import com.autoyol.commons.web.ErrorCode;
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
public class AccountOwnerCostSettleService{
    @Autowired
    private AccountOwnerCostSettleNoTService accountOwnerCostSettleNoTService;
    @Autowired
    private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired
    private AccountOwnerCostSettleMapper accountOwnerCostSettleMapper;

    /**
     * 车主结算信息插入
     * @param accountOwnerCostSettleReqVO
     */
    public void insertAccountOwnerCostSettle(AccountOwnerCostSettleReqVO accountOwnerCostSettleReqVO){
        //1 校验
        Assert.notNull(accountOwnerCostSettleReqVO, ErrorCode.PARAMETER_ERROR.getText());
        accountOwnerCostSettleReqVO.check();
        //2 费用结算插入 车主结算总表
        accountOwnerCostSettleNoTService.insertAccountOwnerCostSettle(accountOwnerCostSettleReqVO);
        //3 费用结算插入 车主结算明细表
        accountOwnerCostSettleDetailNoTService.insertAccountOwnerCostSettleDetail(accountOwnerCostSettleReqVO.getAccountOwnerCostSettleDetailReqVO());
    }
    /**
     * 车主结算信息插入
     * @param  accountOwnerCostSettle
     */
    public void insertAccountOwnerCostSettle(AccountOwnerCostSettleEntity accountOwnerCostSettle){
        accountOwnerCostSettleNoTService.insertAccountOwnerCostSettle(accountOwnerCostSettle);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/1/15 20:38
     * @Description: 通过订单号查询收益
     *
     **/
    public AccountOwnerCostSettleEntity getsettleAmtByOrderNo(String orderNo,String ownerOrderNo){
        return accountOwnerCostSettleMapper.getsettleAmtByOrderNo(orderNo,ownerOrderNo);
    }

}
