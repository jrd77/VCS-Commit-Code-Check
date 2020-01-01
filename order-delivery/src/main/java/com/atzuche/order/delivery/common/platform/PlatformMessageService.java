package com.atzuche.order.delivery.common.platform;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.delivery.model.PlatformModel;
import com.atzuche.order.delivery.service.DeliveryOrderService;
import com.autoyol.aliyunmq.AliyunMnsService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * copy by hcl from yuan.shen on 2017/12/28.
 */
@Service
public class PlatformMessageService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformMessageService.class.getName());

    @Value("${com.autoyol.mns.queue.auto-platform-message-queue}")
    private String PLATFORM_MESSAGE_QUEUE_NAME;

    @Autowired
    private AliyunMnsService aliyunMnsService;
    @Autowired
    private DeliveryOrderService transService;

    /**车主了接受了您的订单，查看详情*/
    private static List<String> OWNER_ACCEPT_TRANS_EVENT = Arrays.asList("53");
    /**车主拒绝了订单,去查看*/
    private static List<String> OWNER_REFUSE_TRANS_EVENT = Arrays.asList("4");
    /**车主取消了订单,去查看*/
    private static List<String> OWNER_CANCEL_TRANS_EVENT = Arrays.asList("5","11", "12", "13");
    /**车主同意了您的订单修改请求，查看详情*/
    private static List<String> OWNER_ACCEPT_UPDATE_TRANS_EVENT = Arrays.asList("37");
    /**车主拒绝了您的订单修改请求,查看详情*/
    private static List<String> OWNER_REFUSE_UPDATE_TRANS_EVENT = Arrays.asList("38");
    /**您有新的订单请求，去处理*/
    private static List<String> OWNER_HAVE_NEW_TRANS_EVENT = Arrays.asList("1", "2");
    /**租客取消了订单,去查看*/
    private static List<String> RENTER_CANCEL_TRANS_EVENT = Arrays.asList("17", "19", "20", "24", "28");
    /**租客请求修改订单, 去处理*/
    private static List<String> RENTER_REQ_UPDATE_TRANS_EVENT = Arrays.asList("39");


    public void asyncSendPlatform̨MessageToQueue(PlatformModel platformModel) {
        this.asyncSendPlatform̨MessageToQueue(platformModel.convert2Map());
    }

    public void asyncSendPlatform̨MessageToQueue(Map<String, String> messageMap) {
        if (messageMap == null) {
            logger.error("send platform message error,message map is null!");
            return;
        }
        String memNo = messageMap.get("memNo");
        if (StringUtils.isEmpty(memNo) || StringUtils.equalsIgnoreCase("null", memNo)) {
            // 代管车手机号未在member表中未查询到会员信息，所以这里直接返回，不推送给平台消息服务
            return;
        }
        String event = messageMap.get("event");
        String orderNo = messageMap.get("orderNo");
        // 图片路径 todo
        String picture = transService.selectOrderCoverPic(messageMap.get("orderNo"));
        messageMap.put("picture", picture);
        //todo
        int count = transService.getPlatformMessageByEvent(Integer.valueOf(event));
        if (count > 0) {
            //如果是套餐订单,返回套餐订单号
            Long packageOrderNo = transService.getPackageOrderNoByOrderNo(Long.valueOf(orderNo));
            if (packageOrderNo != null) {
                messageMap.put("orderNo", String.valueOf(packageOrderNo));
                //todo
                String pictureTaocan = transService.selectOrderCoverPic(messageMap.get("orderNo"));
                messageMap.put("picture", pictureTaocan);
            }
        }
        String convertOrderNo = messageMap.get("orderNo");
        handleHomePageMessage(event, convertOrderNo, messageMap);
        logger.info("asyncSendPlatform̨MessageToQueue:{}", JSON.toJSONString(messageMap));
        this.asyncSendPlatform̨MessageToQueue(JSON.toJSONString(messageMap));
    }

    private void handleHomePageMessage(String event, String orderNo, Map<String, String> messageMap) {
        if (StringUtils.isNotBlank(event) && StringUtils.isNotBlank(orderNo)) {
            Map<String, Object> homePageMessage = new HashMap<>();
            if (OWNER_CANCEL_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "1");
                homePageMessage.put("messageId", "1");
                homePageMessage.put("title", "车主取消了您的订单, 查看详情");
                homePageMessage.put("memRole", "1");
            }
            if (OWNER_REFUSE_UPDATE_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "2");
                homePageMessage.put("messageId", "2");
                homePageMessage.put("title", "车主拒绝了您的订单修改请求,查看详情");
                homePageMessage.put("memRole", "1");
            }
            if (OWNER_ACCEPT_UPDATE_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "3");
                homePageMessage.put("messageId", "3");
                homePageMessage.put("title", "车主同意了您的订单修改请求，查看详情");
                homePageMessage.put("memRole", "1");
            }
            if (OWNER_REFUSE_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "4");
                homePageMessage.put("messageId", "4");
                homePageMessage.put("title", "车主拒绝了您的订单，查看详情");
                homePageMessage.put("memRole", "1");
            }
            if (OWNER_ACCEPT_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "5");
                homePageMessage.put("messageId", "5");
                homePageMessage.put("title", "车主接受了您的订单，查看详情");
                homePageMessage.put("memRole", "1");
            }
            if (RENTER_REQ_UPDATE_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "1");
                homePageMessage.put("messageId", "1");
                homePageMessage.put("title", "租客请求修改订单，去处理");
                homePageMessage.put("memRole", "2");
            }
            if (OWNER_HAVE_NEW_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "2");
                homePageMessage.put("messageId", "2");
                homePageMessage.put("title", "您有新的订单请求, 去处理");
                homePageMessage.put("memRole", "2");
            }
            if (RENTER_CANCEL_TRANS_EVENT.contains(event)) {
                homePageMessage.put("orderNo", orderNo);
                homePageMessage.put("priority", "3");
                homePageMessage.put("messageId", "3");
                homePageMessage.put("title", "租客取消了订单, 去查看");
                homePageMessage.put("memRole", "2");
            }
            if (MapUtils.isNotEmpty(homePageMessage)) {
                try {
                    String homePageMessageStr = net.sf.json.JSONObject.fromObject(homePageMessage).toString();
                    messageMap.put("homePageMessage", homePageMessageStr);
                } catch (Exception e) {
                    logger.error("parse map to json is error", e);
                }
            }
        }
    }

    public void asyncSendPlatform̨MessageToQueue(String messageBody) {
        logger.info("send platform message to im center ,message body = {}", messageBody);
        if (StringUtils.isEmpty(messageBody)) {
            logger.error("send platform message error,message body is empty!");
            return;
        }
        aliyunMnsService.asyncSend̨MessageToQueue(messageBody, PLATFORM_MESSAGE_QUEUE_NAME);
    }
}
