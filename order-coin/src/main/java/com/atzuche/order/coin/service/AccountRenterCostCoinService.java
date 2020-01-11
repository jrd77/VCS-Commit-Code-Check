package com.atzuche.order.coin.service;


import com.atzuche.order.coin.entity.AccountRenterCostCoinEntity;
import com.atzuche.order.coin.mapper.AccountRenterCostCoinMapper;
import com.autoyol.auto.coin.service.vo.req.AutoCoiChargeRequestVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 租客订单使凹凸币流水
 *
 * @author ZhangBin
 * @date 2020-01-07 20:23:33
 */
@Service
public class AccountRenterCostCoinService {
    private final static Logger logger = LoggerFactory.getLogger(AccountRenterCostCoinService.class);

    public static final int AUTO_COIN_RATIO=100;
    
    @Autowired
    private AccountRenterCostCoinMapper accountRenterCostCoinMapper;

    @Autowired
    private AutoCoinProxyService autoCoinProxyService;

    /**
     * 返回用户能使用凹凸币抵扣的总金额（例如用户有1000个凹凸币，返回10即用户能使用凹凸币抵扣10元）
     * @param memNo
     * @return
     */
    public int getUserCoinTotalAmt(String memNo){
        return autoCoinProxyService.getCrmCustPoint(memNo)/AUTO_COIN_RATIO;
    }

    /**
     * 返回指定订单和用户能够使用的凹凸币总金额（例如用户有1000个凹凸币，返回10即用户能使用凹凸币抵扣10元）
     * @param memNo
     * @param orderNo
     * @return
     */
    public int getUserCoinTotalAmt(String memNo,String orderNo){
        return autoCoinProxyService.getCrmCustPoint(memNo)/AUTO_COIN_RATIO+countAutoCoinByOrderNo(orderNo)/AUTO_COIN_RATIO;
    }

    /**
     * 扣减用户的凹凸币金额抵扣租金,该接口订单扣款使用
     * @param memNo
     * @param orderNo
     * @param amt 凹凸币代表的钱的金额，例如5代表凹凸币抵扣5元
     * @return
     */
    public void deductAutoCoin(String memNo,String orderNo,String renterOrderNo,int amt){
        int totalExpense = countAutoCoinByOrderNo(orderNo);
        if(amt*AUTO_COIN_RATIO>totalExpense){
            int diff = amt-totalExpense/AUTO_COIN_RATIO;
            doDeductAutoCoin(memNo,orderNo,renterOrderNo,diff);
        }else{
            logger.warn("扣减的凹凸币数值:{} 小于已扣金额:{}，不再进行扣减",amt*AUTO_COIN_RATIO,totalExpense);
        }
    }

    /**
     * 凹凸币进行结算，多扣要退还
     * @param memNo
     * @param orderNo
     * @param deductAmt 抵扣的金额
     */
    public void settleAutoCoin(String memNo,String orderNo,int deductAmt){
        int totalExpense = countAutoCoinByOrderNo(orderNo);
        if(totalExpense>deductAmt){
            //需要退换
            int diff = (totalExpense-deductAmt)*AUTO_COIN_RATIO;
            boolean returnFlag = doReturnAutoCoin(memNo, orderNo, diff);
            if(returnFlag){
                insertReturnLog(memNo, diff);
            }
            logger.info("success settle autoCoin:{}");
        }else if(totalExpense<deductAmt){
            throw new RuntimeException("the expense autoCoin:"+totalExpense+" is less than deductAmt:"+deductAmt);
        }
    }

    /**
     * 退还凹凸币
     * @param memNo
     * @param orderNo
     * @param diff
     * @return
     */
    private boolean doReturnAutoCoin(String memNo, String orderNo, int diff) {
        AutoCoiChargeRequestVO requestVO = new AutoCoiChargeRequestVO();
        requestVO.setChargeAutoCoin(diff);
        requestVO.setOrderNo(orderNo);
        requestVO.setMemNo(Integer.parseInt(memNo));
        requestVO.setOrderType("3");
        requestVO.setRemark("订单结算退还");
        return autoCoinProxyService.returnCoin(requestVO);
    }

    /**
     * 插入订单结算退还记录
     * @param memNo
     * @param diff
     */
    private void insertReturnLog(String memNo, int diff) {
        AccountRenterCostCoinEntity entity = new AccountRenterCostCoinEntity();
        entity.setAmt(-diff);
        entity.setMemNo(memNo);
        entity.setRemark("订单结算退还");
        entity.setOrderType(3);
        accountRenterCostCoinMapper.insert(entity);
    }


    /**
     * 远程扣减凹凸币金额
     * @param memNo
     * @param orderNo
     * @param renterOrderNo
     * @param amt
     */
    private void doDeductAutoCoin(String memNo,String orderNo,String renterOrderNo,int amt){
        AutoCoiChargeRequestVO requestVO = new AutoCoiChargeRequestVO();
        requestVO.setMemNo(Integer.parseInt(memNo));
        requestVO.setOrderNo(orderNo);
        requestVO.setRemark("订单消费扣减");
        requestVO.setOrderType("1");
        requestVO.setChargeAutoCoin(amt);
        boolean deductFlag = autoCoinProxyService.deduct(requestVO);
        if(deductFlag){
            insertDeductLog(memNo, renterOrderNo, amt);
            logger.info("success deduct auto coin and insert db:memNo={},orderNo={},renterOderNo={},amt={}",memNo,orderNo,renterOrderNo,amt);
        }else{
            logger.warn("cannot deduct auto coin:memNo={},orderNo={},renterOderNo={},amt={}",memNo,orderNo,renterOrderNo,amt);
        }
    }

    /**
     * 插入订单扣减记录
     * @param memNo
     * @param renterOrderNo
     * @param amt
     */
    private void insertDeductLog(String memNo, String renterOrderNo, int amt) {
        AccountRenterCostCoinEntity entity = new AccountRenterCostCoinEntity();
        entity.setAmt(amt*AUTO_COIN_RATIO);
        entity.setMemNo(memNo);
        entity.setRemark("订单抵扣");
        entity.setRenterOrderNo(renterOrderNo);
        entity.setOrderType(1);
        accountRenterCostCoinMapper.insert(entity);
    }

    /**
     * 返回订单消费的凹凸币总额
     * @param orderNo
     * @return
     */
    private int countAutoCoinByOrderNo(String orderNo){
        return accountRenterCostCoinMapper.getAccountRenterCostCoinTotal(orderNo)/AUTO_COIN_RATIO;
    }
}
