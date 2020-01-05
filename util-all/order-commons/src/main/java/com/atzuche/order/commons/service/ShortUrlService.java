package com.atzuche.order.commons.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.feign.ShortFeignClient;
import com.autoyol.vo.Response;
import com.autoyol.vo.req.ShortUrlReqVO;
import com.autoyol.vo.res.ShortUrlResVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * ShortUrlService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class ShortUrlService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String REMARK = "新版订单";

    private static final String SYSTEM_TYPE = "order-center";

    @Resource
    private ShortFeignClient shortFeignClient;

    /**
     * 新短链服务
     * @return 短链
     */
    public  String getShortUrlNew(String url) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "长链生成短链");
        try {
            ShortUrlReqVO shortUrlReqVO = new ShortUrlReqVO();
            shortUrlReqVO.setLongUrl(url);
            shortUrlReqVO.setRemarks(REMARK);
            shortUrlReqVO.setSystemType(SYSTEM_TYPE);
            logger.info("shortUrlService getShortUrlNew params:{}", shortUrlReqVO);
            Cat.logEvent(CatConstants.FEIGN_METHOD,"ShortUrlService.getShortUrlNew");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(shortUrlReqVO));
            Response<ShortUrlResVO> resContent = shortFeignClient.saveShortUrl(shortUrlReqVO);
            logger.info("SendMessageService sendMsg resContent:{}", resContent);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(resContent));
            if(ErrorCode.SUCCESS.getCode().equals(resContent.getResCode()) && resContent.getData() != null){
                ShortUrlResVO data = resContent.getData();
                return data.getShortUrl();
            }
            return url;
        } catch (Exception e) {
            logger.error("长链转短链出错：", e);
            Cat.logError("长链转短链出错：", e);
            return url;
        }finally {
            t.complete();
        }

    }
}
