package com.atzuche.order.coin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.auto.coin.service.api.AutoCoinFeignService;
import com.autoyol.auto.coin.service.vo.req.AutoCoiChargeRequestVO;
import com.autoyol.auto.coin.service.vo.req.AutoCoinAgainDeductRequestVO;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 该服务是远程凹凸币服务的本地代理
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/8 3:10 下午
 **/
@Service
public class AutoCoinProxyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCoinProxyService.class);

    @Resource
    private AutoCoinFeignService autoCoinFeignService;

    /**
     * 查询会员凹凸币信息
     *
     * @param memNo 租客注册号
     * @return 凹凸币总和
     */
    public int getCrmCustPoint(String memNo) {
        LOGGER.info("AutoCoinService.getCrmCustPoint remote call start param memNo: [{}]", memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.getCoinByMemNo");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "memNo=" + memNo);
            ResponseData<AutoCoinResponseVO> result = autoCoinFeignService.getCoinByMemNo(Integer.valueOf(memNo));
            LOGGER.info("AutoRemoteCoinService.getCrmCustPoint remote call end result result: [{}]", JSON.toJSONString(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            t.setStatus(Transaction.SUCCESS);
            if(result!=null&& ErrorCode.SUCCESS.getCode().equals(result.getResCode())){
                t.setStatus(Transaction.SUCCESS);
                return result.getData().getPointValue();
            }else{
                LOGGER.error("获取远程的凹凸币服务出现异常,errorCode!=000000,context[memNo={},result is {}]",memNo,result);
                RuntimeException e = new RuntimeException("remote auto coin exception:mem_no="+memNo);
                t.setStatus(e);
                Cat.logError("获取远程凹凸币服务出现异常",e);
            }
        } catch (Exception e) {
            LOGGER.error("查询会员凹凸币信息异常.memNo:[{}]", memNo, e);
            t.setStatus(e);
            Cat.logError("查询会员凹凸币信息异常.", e);
        } finally {
            t.complete();
        }
        return 0;
    }


    /**
     * 同步充值凹凸币
     *
     * @param vo 请求参数
     * @return Boolean
     */
    public boolean recharge(AutoCoiChargeRequestVO vo) {
        LOGGER.info("AutoRemoteCoinService recharge remote call start param vo:[{}]", vo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.recharge(vo)");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "vo=" + JSON.toJSONString(vo));
            ResponseData<Boolean> result = autoCoinFeignService.recharge(vo);
            LOGGER.info("AutoRemoteCoinService recharge remote call end result result: [{}]",
                    GsonUtils.toJson(vo));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            if(result!=null&&ErrorCode.SUCCESS.getCode().equals(result.getResCode())){
                t.setStatus(Transaction.SUCCESS);
                Boolean data = result.getData();
                if(data!=null&&data){
                    return true;
                }
                return false;
            }else{
                LOGGER.error("获取远程的凹凸币服务出现异常,errorCode!=000000,context[{},result is {}]",vo,result);
                RuntimeException e = new RuntimeException("remote auto coin exception:vo="+vo);
                t.setStatus(e);
                Cat.logError("获取远程凹凸币服务出现异常",e);
            }
        } catch (Exception e) {
            LOGGER.error("同步充值凹凸币信息异常.vo:[{}]", vo, e);
            t.setStatus(e);
            Cat.logError("同步充值凹凸币信息异常.", e);
        } finally {
            t.complete();
        }
        return false;
    }


    /**
     * 同步扣减凹凸币
     *
     * @param vo 请求参数
     * @return Boolean
     */
    public Boolean deduct(AutoCoiChargeRequestVO vo) {
        LOGGER.info("AutoRemoteCoinService deduct remote call start param vo:[{}]", vo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.deduct(vo)");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "vo=" + JSON.toJSONString(vo));
            ResponseData<Boolean> result = autoCoinFeignService.deduct(vo);
            LOGGER.info("AutoRemoteCoinService deduct remote call end result result: [{}]", GsonUtils.toJson(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            //FIXME: 判断响应码
            t.setStatus(Transaction.SUCCESS);
            return result.getData();
        } catch (Exception e) {
            LOGGER.error("同步充值凹凸币信息异常.vo:[{}]", vo, e);
            t.setStatus(e);
            Cat.logError("同步充值凹凸币信息异常.", e);
        } finally {
            t.complete();
        }
        return false;

    }


    /**
     * 补扣凹凸币
     *
     * @param autoCoinRechange 请求参数
     * @return Boolean
     */
    public Boolean againDeduct(AutoCoinAgainDeductRequestVO autoCoinRechange) {
        LOGGER.info("AutoRemoteCoinService againDeduct remote call start param vo:[{}]", autoCoinRechange);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.againDeduct(vo)");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "vo=" + JSON.toJSONString(autoCoinRechange));
            ResponseData<Boolean> result = autoCoinFeignService.againDeduct(autoCoinRechange);
            LOGGER.info("AutoRemoteCoinService againDeduct remote call end result result: [{}]", GsonUtils.toJson(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            //FIXME: 判断响应码
            t.setStatus(Transaction.SUCCESS);
            return result.getData();
        } catch (Exception e) {
            LOGGER.error("补扣凹凸币异常.vo:[{}]", autoCoinRechange, e);
            t.setStatus(e);
            Cat.logError("补扣凹凸币异常.", e);
        } finally {
            t.complete();
        }
        return false;

    }

    /**
     * 结算退还多余凹凸币
     */
    public Boolean returnCoin(AutoCoiChargeRequestVO autoCoinRechange) {
        LOGGER.info("AutoRemoteCoinService returnCoin remote call start param vo:[{}]", autoCoinRechange);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.returnCoin(vo)");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "vo=" + JSON.toJSONString(autoCoinRechange));
            ResponseData<Boolean> result = autoCoinFeignService.returnAutoCoin(autoCoinRechange);
            LOGGER.info("AutoRemoteCoinService returnCoin remote call end result result: [{}]", GsonUtils.toJson(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            //FIXME: 判断响应码
            t.setStatus(Transaction.SUCCESS);
            return result.getData();
        } catch (Exception e) {
            LOGGER.error("退还凹凸币异常.vo:[{}]", autoCoinRechange, e);
            t.setStatus(e);
            Cat.logError("退还凹凸币异常.", e);
        } finally {
            t.complete();
        }
        return false;

    }
}
