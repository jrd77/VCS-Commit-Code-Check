package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.coreapi.entity.dto.Illegal;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzDetailService;
import com.dianping.cat.Cat;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * IllegalSmsSendService
 *
 * @author shisong
 * @date 2020/1/3
 */
@Service
public class IllegalSmsSendService {

    private Logger logger = LoggerFactory.getLogger(IllegalSmsSendService.class);

    @Resource
    private RenterOrderWzDetailService renterOrderWzDetailService;

    @Resource
    private WeChatTemplateService weChatTemplateService;

    @Resource
    private RenterMemberService renterMemberService;

    @Resource
    private OwnerMemberService ownerMemberService;

    @Resource
    private JPushService jPushService;

    @Resource
    private OrderSourceStatService orderSourceStatService;

    @Value("${wechat.push.template.id}")
    private String templateId;

    @Value("${violation.h5.url}")
    private String h5Url;

    private static String STYLE_2 = "yyyy年MM月dd日 HH时mm分";

    private static String MSG = "您的订单产生违章，不要忘记去APP处理哟~";

    public static final String SMS_TYPE_RENTER = "1";
    public static final String SMS_TYPE_OWNER = "2";


    public void smsNotice() {
        try {
            List<RenterOrderWzDetailEntity> illegalDetailList = renterOrderWzDetailService.findSendSmsIllegalRecord();
            logger.info("开始查询待发短信的违章订单，开始时间:{}", new Date());
            if(CollectionUtils.isEmpty(illegalDetailList)){
                return;
            }
            Map<String, RenterOrderWzDetailEntity> map = this.updateIllegalMessage(illegalDetailList);
            logger.info("根据待发违章列表，组装MAP数据：{}",map);
            List<String> orderNoList = illegalDetailList.stream().map(RenterOrderWzDetailEntity::getOrderNo).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderNoList)) {
                Map<String, Illegal> illegalMap = this.mapIllegalByOrderNos(orderNoList);
                map.forEach((key, illegalDetail) -> {
                    String orderNo = illegalDetail.getOrderNo();
                    //查询是否是携程订单
                    boolean isCtripOrder = orderSourceStatService.isCtripOrderByOrderNo(orderNo);
                    Illegal illegal = illegalMap.get(orderNo);
                    if (illegal == null) {
                        logger.error("违章短信PUSH异常,订单号{}未查询到订单", orderNo);
                        return;
                    }
                    String renterNo = illegal.getRentNo();
                    String renterPhone = illegal.getRenterPhone();
                    String ownerNo = illegal.getOwnerNo();
                    String ownerPhone = illegal.getOwnerPhone();
                    if (renterNo == null || StringUtils.isEmpty(renterPhone)) {
                        logger.error("违章短信PUSH-租客发送失败,orderNo={},renterNo={},renterPhone={}", orderNo, renterNo, renterPhone);
                    }
                    jPushService.updateIllegalMessage(orderNo, renterNo, renterPhone, SMS_TYPE_RENTER,isCtripOrder);
                    if (ownerNo == null || StringUtils.isEmpty(ownerPhone)) {
                        logger.error("违章短信PUSH-车主发送失败,orderNo={},ownerNo={},ownerPhone={}", orderNo, ownerNo, ownerPhone);
                    }
                    jPushService.updateIllegalMessage(orderNo, ownerNo, ownerPhone, SMS_TYPE_OWNER,isCtripOrder);
                    //根据AUT-2003需求增加违章微信推送
                    try {
                        if(!isCtripOrder){
                            Map<String, String> templateMap = new HashMap<>(11);
                            templateMap.put("templateId", templateId);
                            templateMap.put("memberNo", String.valueOf(renterNo));
                            templateMap.put("sendMsg", MSG);
                            templateMap.put("violationTime", DateUtils.formate(illegalDetail.getIllegalTime(),STYLE_2));
                            templateMap.put("violationAddress", illegalDetail.getIllegalAddr());
                            templateMap.put("violationType", illegalDetail.getIllegalReason());
                            templateMap.put("violationFine", illegalDetail.getIllegalAmt()+"元");
                            templateMap.put("violationPoints", "扣"+illegalDetail.getIllegalDeduct()+"分");
                            templateMap.put("remark", "");
                            templateMap.put("url", h5Url + "/" + orderNo);
                            templateMap.put("pagePath", "");
                            String mes = new Gson().toJson(templateMap);
                            weChatTemplateService.weChatPushTemplateParamerMQSend(mes);
                        }
                    } catch (Exception e) {
                        logger.info("违章记录生成给租客微信推送异常！！orderNo:{}",orderNo,e);
                        Cat.logError("违章记录生成给租客微信推送异常！！orderNo:"+orderNo,e);
                    }
                });
            }
            logger.info("jPush操作完成：{}",new Date());
        } catch (Exception e) {
            logger.error("查询待发短信违章订单失败:",e);
        }
    }

    private Map<String, Illegal> mapIllegalByOrderNos(List<String> orderNos) {
        Map<String, Illegal> resultMap = new HashMap<>(orderNos.size());
        List<RenterMemberEntity> renters = renterMemberService.queryMemNoAndPhoneByOrderList(orderNos);
        List<OwnerMemberEntity> owners = ownerMemberService.queryMemNoAndPhoneByOrderList(orderNos);
        Map<String, RenterMemberEntity> renterMap = new HashMap<>(renters.size());
        if(!CollectionUtils.isEmpty(renters)){
            renterMap = renters.stream().collect(Collectors.toMap(RenterMemberEntity::getOrderNo, Function.identity()));
        }
        Map<String, OwnerMemberEntity> ownerMap = new HashMap<>(owners.size());
        if(!CollectionUtils.isEmpty(owners)){
            ownerMap = owners.stream().collect(Collectors.toMap(OwnerMemberEntity::getOrderNo, Function.identity()));
        }
        for (String orderNo : orderNos) {
            Illegal illegal = new Illegal();
            illegal.setOrderNo(orderNo);
            if(!CollectionUtils.isEmpty(renterMap)){
                RenterMemberEntity renterMemberEntity = renterMap.get(orderNo);
                if(renterMemberEntity != null){
                    illegal.setRentNo(renterMemberEntity.getMemNo());
                    illegal.setRenterPhone(renterMemberEntity.getPhone());
                }
            }
            if(!CollectionUtils.isEmpty(ownerMap)){
                OwnerMemberEntity ownerMemberEntity = ownerMap.get(orderNo);
                if(ownerMemberEntity != null){
                    illegal.setOwnerNo(ownerMemberEntity.getMemNo());
                    illegal.setOwnerPhone(ownerMemberEntity.getPhone());
                }
            }
            resultMap.put(orderNo,illegal);
        }
        return resultMap;
    }

    private Map<String, RenterOrderWzDetailEntity> updateIllegalMessage(List<RenterOrderWzDetailEntity> illegalDetailList) {
        Map<String, RenterOrderWzDetailEntity> map = new HashMap<>(illegalDetailList.size());
        for (RenterOrderWzDetailEntity detail : illegalDetailList) {
            try {
                //失败跳过，执行下一次循环
                String orderNo = detail.getOrderNo();
                String illegalDate = DateUtils.formate(detail.getIllegalTime(), DateUtils.DATE_DEFAUTE);
                String key = orderNo + "#" + illegalDate;
                if (!map.containsKey(key)) {
                    map.put(key, detail);
                }
            } catch (Exception e) {
                logger.error("组装违章信息map出现异常："+e);
            }
        }
        return map;
    }
}
