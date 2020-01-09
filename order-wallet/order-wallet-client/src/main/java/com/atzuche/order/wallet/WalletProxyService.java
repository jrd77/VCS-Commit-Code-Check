package com.atzuche.order.wallet;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.wallet.api.DeductWalletVO;
import com.atzuche.order.wallet.api.OrderWalletFeignService;
import com.atzuche.order.wallet.api.TotalWalletVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 7:14 下午
 **/
@Service
public class WalletProxyService {
    @Autowired
    private OrderWalletFeignService walletFeignService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(WalletProxyService.class);


    /**
     * 订单扣减钱包
     * @param memNo
     * @param orderNo
     * @param amt 正值
     * @return 真实的抵扣金额
     */
    public int orderDeduct(String memNo,String orderNo,int amt){
        LOGGER.info("订单钱包服务 remote call start param  [{},{},{}]", memNo,orderNo,amt);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单钱包服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "OrderWalletService.deductWallet");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "memNo=" + memNo+",orderNo="+orderNo+",amt="+amt);
            DeductWalletVO vo = buildRequestVO(memNo, orderNo, amt);
            ResponseData<DeductWalletVO> result = walletFeignService.deductWallet(vo);
            LOGGER.info("OrderWalletService.deductWallet remote call end result result: [{}]", JSON.toJSONString(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            t.setStatus(Transaction.SUCCESS);
            if(result!=null&& ErrorCode.SUCCESS.getCode().equals(result.getResCode())){
                t.setStatus(Transaction.SUCCESS);
                return result.getData().getAmt();
            }else{
                LOGGER.error("远程钱包服务订单扣减出现异常,errorCode!=000000,context[memNo={},orderNo={},amt={},result is {}]",memNo,orderNo,amt,result);
                RuntimeException e = new RuntimeException("remote order wallet exception:mem_no="+memNo+",orderNo="+orderNo+",amt="+amt);
                t.setStatus(e);
                Cat.logError("远程钱包服务订单扣减出现异常",e);
            }
        } catch (Exception e) {
            LOGGER.error("扣减会员订单钱包异常.memNo:[{}]", memNo, e);
            t.setStatus(e);
            Cat.logError("扣减会员订单钱包异常.", e);
        } finally {
            t.complete();
        }
        return 0;
    }

    /**
     * 退还用户的钱包金额
     * @param memNo
     * @param orderNo
     * @param amt 正值
     */
    public void returnOrChargeWallet(String memNo,String orderNo,int amt){
        LOGGER.info("订单钱包服务 remote call start param  [{},{},{}]", memNo,orderNo,amt);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单钱包服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "OrderWalletService.returnOrChangeWallet");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "memNo=" + memNo+",orderNo="+orderNo+",amt="+amt);
            DeductWalletVO vo = buildRequestVO(memNo, orderNo, amt);
            ResponseData<DeductWalletVO> result = walletFeignService.returnOrChangeWallet(vo);
            LOGGER.info("OrderWalletService.returnOrChangeWallet remote call end result result: [{}]", JSON.toJSONString(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            t.setStatus(Transaction.SUCCESS);
            if(result!=null&& ErrorCode.SUCCESS.getCode().equals(result.getResCode())){
                t.setStatus(Transaction.SUCCESS);
                return;
            }else{
                LOGGER.error("远程钱包服务订单退还出现异常,errorCode!=000000,context[memNo={},orderNo={},amt={},result is {}]",memNo,orderNo,amt,result);
                RuntimeException e = new RuntimeException("remote order wallet exception:mem_no="+memNo+",orderNo="+orderNo+",amt="+amt);
                t.setStatus(e);
                Cat.logError("远程钱包服务订单退还出现异常",e);
            }
        } catch (Exception e) {
            LOGGER.error("远程钱包服务订单退还出现异常.memNo:[{}]", memNo, e);
            t.setStatus(e);
            Cat.logError("远程钱包服务订单退还出现异常.", e);
        } finally {
            t.complete();
        }
    }

    private DeductWalletVO buildRequestVO(String memNo, String orderNo, int amt) {
        DeductWalletVO vo = new DeductWalletVO();
        vo.setAmt(amt);
        vo.setMemNo(memNo);
        vo.setOrderNo(orderNo);
        return vo;
    }

    /**
     * 查询用户的钱包余额
     * @param memNo
     * @return
     */
    public int getWalletByMemNo(String memNo){
        LOGGER.info("订单钱包服务 remote call start param  [{}]", memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单钱包服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "OrderWalletService.getWalletTotalByMemNo");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "memNo=" + memNo);
            ResponseData<TotalWalletVO> result = walletFeignService.getWalletTotalByMemNo(memNo);
            LOGGER.info("OrderWalletService.getWalletTotalByMemNo remote call end result result: [{}]", JSON.toJSONString(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            t.setStatus(Transaction.SUCCESS);
            if(result!=null&& ErrorCode.SUCCESS.getCode().equals(result.getResCode())){
                t.setStatus(Transaction.SUCCESS);
                return Integer.parseInt(result.getData().getTotal());
            }else{
                LOGGER.error("获取远程的订单钱包出现异常,errorCode!=000000,context[memNo={},result is {}]",memNo,result);
                RuntimeException e = new RuntimeException("remote order wallet exception:mem_no="+memNo);
                t.setStatus(e);
                Cat.logError("获取远程的订单钱包服务出现异常",e);
            }
        } catch (Exception e) {
            LOGGER.error("获取远程的订单钱包出现异常.memNo:[{}]", memNo, e);
            t.setStatus(e);
            Cat.logError("获取远程的订单钱包出现异常.", e);
        } finally {
            t.complete();
        }
        return 0;
    }
}
