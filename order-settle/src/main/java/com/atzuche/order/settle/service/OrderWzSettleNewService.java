package com.atzuche.order.settle.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformSubsidyDetailNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostSettleDetailNoTService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.service.notservice.OrderWzSettleNoTService;
import com.atzuche.order.settle.vo.req.RentCostsWz;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OrderWzSettleNewService {
    @Autowired CashierNoTService cashierNoTService;
    @Autowired AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;
    @Autowired AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired
    private AccountRenterWzDepositCostSettleDetailNoTService accountRenterWzDepositCostSettleDetailNoTService;
    @Autowired
    OrderWzSettleNoTService orderWzSettleNoTService;
    
    /**
     *  先查询  发现 有结算数据停止结算 手动处理
     * @param orderNo
     */
    public void checkIsSettle(String orderNo) {
    	//违章
        /*
         * 	违章费用结算明细表
			account_renter_wz_deposit_cost_settle_detail
         */
    	List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetailEntitys = accountRenterWzDepositCostSettleDetailNoTService.getAccountRenterWzDepositCostSettleDetail(orderNo);
    	
    	/**
    	 * todo 应该根据违章押金的部分的补贴和收益来处理，代收代付？？  先注释掉。
    	 */

        if(!CollectionUtils.isEmpty(accountRenterWzDepositCostSettleDetailEntitys)){
            throw new RuntimeException("有违章结算数据停止结算");
        }

    }
    
    
    /**
     * 车辆结算  校验费用落库等无实物操作
     */
    public void settleOrderFirst(SettleOrdersWz settleOrders){
        //1 查询所有租客费用明细
    	orderWzSettleNoTService.getRenterCostSettleDetail(settleOrders);
        log.info("wz OrderSettleService getRenterCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));
        Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
 
        RentCostsWz rentCosts = settleOrders.getRentCostsWz();
        if(Objects.nonNull(rentCosts)){
            //1.1 查询违章费用
      
            List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails = rentCosts.getRenterOrderWzCostDetails();
            if(!CollectionUtils.isEmpty(renterOrderWzCostDetails)){
            	
            	List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetails = new ArrayList<AccountRenterWzDepositCostSettleDetailEntity>();
            	
            	List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = new ArrayList<AccountRenterCostSettleDetailEntity>();
            	
                for(int i=0; i<renterOrderWzCostDetails.size();i++){
                	RenterOrderWzCostDetailEntity renterOrderWzCostDetail = renterOrderWzCostDetails.get(i);
                    AccountRenterWzDepositCostSettleDetailEntity accountRenterWzDepositCostSettleDetail = new AccountRenterWzDepositCostSettleDetailEntity();
                    //赋值
                    accountRenterWzDepositCostSettleDetail.setOrderNo(renterOrderWzCostDetail.getOrderNo());
                    accountRenterWzDepositCostSettleDetail.setMemNo(String.valueOf(renterOrderWzCostDetail.getMemNo()));
                    accountRenterWzDepositCostSettleDetail.setUniqueNo(String.valueOf(renterOrderWzCostDetail.getId()));
                    accountRenterWzDepositCostSettleDetail.setPrice(renterOrderWzCostDetail.getAmount());
                    accountRenterWzDepositCostSettleDetail.setWzAmt(renterOrderWzCostDetail.getAmount());
                    accountRenterWzDepositCostSettleDetail.setUnit(1); //默认1
                    
                    accountRenterWzDepositCostSettleDetails.add(accountRenterWzDepositCostSettleDetail);
                    
                    //封装费用
                  //wzTotalCost-todo
                    AccountRenterCostSettleDetailEntity  entity = new AccountRenterCostSettleDetailEntity();
                    BeanUtils.copyProperties(renterOrderWzCostDetail, entity);
                    entity.setType(10);  //代表违章
                    entity.setAmt(NumberUtils.convertNumberToFushu(renterOrderWzCostDetail.getAmount()));  //显示负数,代表的是支出
                    entity.setCostCode(renterOrderWzCostDetail.getCostCode());
                    entity.setCostDetail(renterOrderWzCostDetail.getCostDesc());
                    accountRenterCostSettleDetails.add(entity);
                    
                }
                
                //同时让租客费用总表里面记录。
                
                if(accountRenterWzDepositCostSettleDetails.size() > 0) {
                	//落库
                	cashierSettleService.insertAccountRenterWzDepoistCostSettleDetails(accountRenterWzDepositCostSettleDetails);
                	
                }
              //wzTotalCost-todo
                if(accountRenterCostSettleDetails.size() > 0) {
                	cashierSettleService.insertAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
                }
            }
        }
        
    }
    
    /**
     * 结算逻辑
     */
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(SettleOrdersWz settleOrders) {
        //7.1 违章费用  总费用 信息落库 并返回最新租车费用 实付
    	/**
    	 * 违章费用总表及其结算总表
		account_renter_wz_deposit_cost
    	 */
    	AccountRenterWzDepositCostEntity accountRenterCostSettle = cashierSettleService.updateWzRentSettleCost(settleOrders.getOrderNo(),settleOrders.getRenterMemNo(), settleOrders.getRenterOrderCostWz());
        log.info("OrderSettleService updateRentSettleCost [{}]",GsonUtils.toJson(accountRenterCostSettle));
        Cat.logEvent("updateRentSettleCost",GsonUtils.toJson(accountRenterCostSettle));
        
        //8 获取租客 实付 违章押金
        int wzDepositAmt = cashierSettleService.getSurplusWZDepositCostAmt(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
        
        SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
        BeanUtils.copyProperties(settleOrders,settleOrdersAccount);
        settleOrdersAccount.setRentCostAmtFinal(accountRenterCostSettle.getYingfuAmt());  //应付  违章费用
        settleOrdersAccount.setRentCostPayAmt(0);  //实付   0,没有违章押金的单独支付
        settleOrdersAccount.setDepositAmt(wzDepositAmt);
        settleOrdersAccount.setDepositSurplusAmt(wzDepositAmt);
        
        //按0处理，违章费用没有单独支付。
        int rentCostSurplusAmt = 0;//(accountRenterCostSettle.getYingfuAmt() + accountRenterCostSettle.getShifuAmt())<=0?0:(accountRenterCostSettle.getRentAmt() + accountRenterCostSettle.getShifuAmt());
        settleOrdersAccount.setRentCostSurplusAmt(rentCostSurplusAmt);
        
        log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount one [{}]", GsonUtils.toJson(settleOrdersAccount));
        Cat.logEvent("settleOrdersAccount",GsonUtils.toJson(settleOrdersAccount));

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(settleOrders.getOrderNo());
        ///add 
        orderStatusDTO.setWzSettleTime(LocalDateTime.now());
        orderStatusDTO.setStatus(OrderStatusEnum.TO_CLAIM_SETTLE.getStatus());
        orderStatusDTO.setWzSettleStatus(SettleStatusEnum.SETTLED.getCode());
        
        //9 租客费用 结余处理
        orderWzSettleNoTService.wzCostSettle(settleOrders,settleOrdersAccount);
        //10租客车辆押金/租客剩余租车费用 结余历史欠款
        orderWzSettleNoTService.repayWzHistoryDebtRent(settleOrdersAccount);
        //12 违章押金 退还
        orderWzSettleNoTService.refundWzDepositAmt(settleOrdersAccount,orderStatusDTO);
        //15 更新订单状态
        settleOrdersAccount.setOrderStatusDTO(orderStatusDTO);
        orderWzSettleNoTService.saveOrderStatusInfo(settleOrdersAccount);
        log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount two [{}]", GsonUtils.toJson(settleOrdersAccount));
    }
    
    

    
    // ----------------------------------------------------------------------------------------------------------------------------------
    

 
}
