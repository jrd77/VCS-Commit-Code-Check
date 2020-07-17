package com.atzuche.order.cashieraccount.service.remote;

import java.lang.reflect.Type;

import javax.annotation.Resource;

import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.config.CommonConfig;
import com.atzuche.order.cashieraccount.exception.WithdrawalAmtException;
import com.atzuche.order.cashieraccount.vo.req.OwnerOpenReqVO;
import com.atzuche.order.cashieraccount.vo.req.WithdrawalsReqVO;
import com.atzuche.order.cashieraccount.vo.res.OpenInfoStatusVO;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pengcheng.fu
 * @date 2020/7/8 15:10
 */
@Service
@Slf4j
public class AutoSecondOpenRemoteService {


    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private CommonConfig commonConfig;


    public ResponseData sendWithdrawalRequest(WithdrawalsReqVO reqVO) {
        Transaction t = Cat.newTransaction(CatConstants.URL_CALL, "二清项目应用层服务");
        try {
            Cat.logEvent("URL.Api", "/api/second/open/withdrawal/cashOut");
            Cat.logEvent(CatConstants.URL_PARAM, JSON.toJSONString(reqVO));

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(commonConfig.secondOpenUrl + "api/second/open/withdrawal/cashOut",
                    reqVO, String.class);
            Cat.logEvent(CatConstants.URL_RESULT, responseEntity.getBody());

            ResponseData responseData;
            if (StringUtils.isBlank(responseEntity.getBody())) {
                responseData = ResponseData.error();
            } else {
                responseData = JSON.parseObject(responseEntity.getBody(), ResponseData.class);
            }
            t.setStatus(Transaction.SUCCESS);
            return responseData;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("Invoke /api/second/open/withdrawal/cashOut err.", e);
            log.error("Invoke /api/second/open/withdrawal/cashOut err.", e);
            throw new WithdrawalAmtException("上海银行提现接口异常");
        } finally {
            t.complete();
        }
    }


    /**
     * 验证车主是否开户
     *
     * @param memNo
     * @return
     */
    public boolean checkOwnerIsOpenVir(String memNo) {
        Transaction t = Cat.newTransaction(CatConstants.URL_CALL, "二清项目应用层服务");
        try {
            Cat.logEvent("URL.Api", "/second/open/person/owner/getOpenInfo");
            Cat.logEvent(CatConstants.URL_PARAM, memNo);

            OwnerOpenReqVO reqVo = new OwnerOpenReqVO();
            reqVo.setMemNo(memNo);
            String json = restTemplate.postForObject(commonConfig.secondOpenUrl + "second/open/person/owner/getOpenInfo", reqVo, String.class);

            t.setStatus(Transaction.SUCCESS);

            //定义类型
            Type type = new TypeToken<ResponseData<OpenInfoStatusVO>>() {
            }.getType();
            ResponseData<OpenInfoStatusVO> responseEntity = new Gson().fromJson(json, type);

            if (responseEntity == null || responseEntity.getData() == null) {
                log.info("postForEntity null,params memNo=[{}]", memNo);
                return false;
            } else {
                OpenInfoStatusVO statusVo = responseEntity.getData();
                log.info("postForEntity ok,result=[{}],params memNo=[{}]", GsonUtils.toJson(statusVo), memNo);
                //开户状态 0：未完成 1：完成
                if (statusVo.getSecondOpenOwner() != null && statusVo.getSecondOpenOwner().getOpenStatus().intValue() == 1) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("Invoke /second/open/person/owner/getOpenInfo err.", e);
            log.error("Invoke /second/open/person/owner/getOpenInfo err.", e);
            throw new WithdrawalAmtException("上海银行获取是否开户失败");
        } finally {
            t.complete();
        }
    }

}
