package com.atzuche.order.cashieraccount.service.remote;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.config.CommonConfig;
import com.atzuche.order.cashieraccount.exception.WithdrawalAmtException;
import com.atzuche.order.cashieraccount.vo.req.WithdrawalsReqVO;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

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
            t.setStatus(Transaction.SUCCESS);
            if (StringUtils.isBlank(responseEntity.getBody())) {
                return ResponseData.error();
            }
            return JSON.parseObject(responseEntity.getBody(), ResponseData.class);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("Invoke /api/second/open/withdrawal/cashOut err.", e);
            log.error("Invoke /api/second/open/withdrawal/cashOut err.", e);
            throw new WithdrawalAmtException("上海银行提现失败");
        } finally {
            t.complete();
        }
    }


}
