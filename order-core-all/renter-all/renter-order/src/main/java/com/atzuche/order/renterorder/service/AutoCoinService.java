package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.auto.coin.service.api.AutoCoinFeignService;
import com.autoyol.auto.coin.service.vo.req.AutoCoiChargeRequestVO;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 凹凸币服务相关业务处理类
 *
 * @author pengcheng.fu
 * @date 2019/12/27 11:09
 */
@Service
public class AutoCoinService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformCouponService.class);

    @Resource
    private AutoCoinFeignService autoCoinFeignService;

    /**
     * 查询会员凹凸币信息
     *
     * @param memNo 租客注册号
     * @return CrmCustPointDTO 凹凸币信息
     */
    public AutoCoinResponseVO getCrmCustPoint(String memNo) {
        LOGGER.info("AutoCoinService.getCrmCustPoint remote call start param memNo: [{}]", memNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.getCoinByMemNo");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "memNo=" + memNo);
            ResponseData<AutoCoinResponseVO> result = autoCoinFeignService.getCoinByMemNo(Integer.valueOf(memNo));
            LOGGER.info("AutoRemoteCoinService.getCrmCustPoint remote call end result result: [{}]", JSON.toJSONString(result));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
            t.setStatus(Transaction.SUCCESS);
            if (Objects.isNull(result)) {
                return null;
            }
            return result.getData();
        } catch (Exception e) {
            LOGGER.error("查询会员凹凸币信息异常.memNo:[{}]", memNo, e);
            t.setStatus(e);
            Cat.logError("查询会员凹凸币信息异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }


    /**
     * 同步充值凹凸币
     *
     * @param vo 请求参数
     * @return Boolean
     */
    public Boolean recharge(AutoCoiChargeRequestVO vo) {
        LOGGER.info("AutoRemoteCoinService recharge remote call start param vo:[{}]", vo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "凹凸币服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "autoCoinFeignService.recharge(vo)");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "vo=" + JSON.toJSONString(vo));
            ResponseData<Boolean> result = autoCoinFeignService.recharge(vo);
            LOGGER.info("AutoRemoteCoinService recharge remote call end result result: [{}]",
                    GsonUtils.toJson(vo));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(result));
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

}
