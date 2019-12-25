package common.event.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.autoyol.aliyunmq.AliyunMnsService;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import common.DeliveryConstants;
import lombok.extern.slf4j.Slf4j;
import model.ctrip.CtripStatusTransFinishVo;
import model.ctrip.SDActualFee;
import model.ctrip.TransDistributeBO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.HandoverCarService;
import utils.*;
import vo.HandoverCarVO;

import java.util.*;

/**
 * @author 胡春林
 * 处理仁云推送过来的消息数据
 */
@Component
@Slf4j
public class HandoverCarRoutesEvent {

    @Autowired
    private AliyunMnsService aliyunMnsService;

    @Autowired
    HandoverCarService handoverCarService;

    @Subscribe
    @AllowConcurrentEvents
    public void onProcessHandoverCarInfo(Object object){

        if (Objects.isNull(object)) {
            log.info("仁云推送过来的消息数据存在问题：object:{}", object);
            return;
        }
        Message message = (Message)object;
        byte[] messageBody = message.getMessageBodyAsBytes();
        String json = ZipUtils.uncompress(messageBody);
        HandoverCarVO handoverCarVO = JSONObject.parseObject(json, HandoverCarVO.class);
        handoverCarVO.setMessageId(message.getMessageId());
        // todo 插入记录数据
        sendMessageToAliMnsQueue(handoverCarVO);
    }

    public void sendMessageToAliMnsQueue(HandoverCarVO handoverCarVO)
    {
            // 携程套餐订单完成进行回执
            String description = handoverCarVO.getDescription();
            String proId = handoverCarVO.getProId();
            String orderNo = handoverCarVO.getOrderNo();
            if (StringUtils.isNotBlank(description) && DeliveryConstants.CAR_ON_ROAD_WAITING.equals(description) && StringUtils.isNotBlank(proId) && "7".equals(proId)) {
                long orderNum = orderNo != null ? Long.parseLong(orderNo) : 0L;
                int count = 0;
                //todo Integer count = partnerReturnMessageMapper.isExistReturnMessage(orderNum);
                if (count < 1) {
                    sendCtripTransFinish(orderNum);
                }
                handoverCarService.handlerHandoverCarStepByTransInfo(handoverCarVO);
            }
    }

    public void sendCtripTransFinish(long orderNo) {

            log.info("订单状态完成回执前的订单号：" + orderNo);
            // todo TransDistributeBO transDistributeBO = transMapper.selectDataByOrderId(orderNo);
            TransDistributeBO transDistributeBO = null;
            if (transDistributeBO != null) {
                log.info("订单所属渠道:{}", transDistributeBO.getPartnerCode());
                if (DeliveryConstants.CTRIP.equalsIgnoreCase(transDistributeBO.getPartnerCode())) {
                    log.info("进入订单状态完成回执的订单号：" + orderNo);
                    log.info("进入订单状态完成回执的transDistributeBO：" + JSON.toJSONString(transDistributeBO));
                    sendMessage(transDistributeBO);
                }
            }
    }

    /**
     * 这个优化有点悬
     * @param transDistributeBO
     */
    private void sendMessage(TransDistributeBO transDistributeBO) {

        CtripStatusTransFinishVo vo = new CtripStatusTransFinishVo();
        vo.setOperateSerialNumber(DateUtils.getNowDateLong() + CommonUtil.getUUID().substring(0, 8));
        vo.setVendorCode(DeliveryConstants.VENDOR_CODE);
        vo.setCtripOrderId(Long.parseLong(transDistributeBO.getPartnerOrderNo()));
        vo.setVendorOrderStatus(1);
        try {
            vo.setActualPickupTime(DateUtils.formatTime(transDistributeBO.getRentTime()));
        } catch (Exception e) {
            log.info("转换还车时间格式出错：RentTime:{}", transDistributeBO.getRentTime());
        }
        vo.setActualReturnTime(DateUtils.parseDateDefaute(new Date(), DateUtils.DATE_DEFAUTE_3));
        vo.setPeccancyDepositAmount(0);
        vo.setPeccancyDepositReturnTime(DateUtils.parseDateDefaute(new Date(), DateUtils.DATE_DEFAUTE_3));
        vo.setBackFeeList(new String[]{});

        List<SDActualFee> list = new ArrayList<>();
        SDActualFee rentFee = new SDActualFee();

        rentFee.setType(1);
        rentFee.setQuantity(Float.parseFloat(transDistributeBO.getHolidayRentDays()));
        rentFee.setUnitPrice(Double.parseDouble(transDistributeBO.getHolidayAverage()));
        rentFee.setTotalPrice(Double.parseDouble(transDistributeBO.getRentAmt()));
        rentFee.setName("租车费");
        rentFee.setDescription(transDistributeBO.getHolidayAverage() + " *" + Float.parseFloat(transDistributeBO.getHolidayRentDays()));
        rentFee.setIsOffline(false);
        list.add(rentFee);

        SDActualFee abatementFee = new SDActualFee();
        abatementFee.setType(12);
        abatementFee.setQuantity(Float.parseFloat(transDistributeBO.getHolidayRentDays()));
        abatementFee.setUnitPrice(Double.parseDouble(transDistributeBO.getAbatementInsure()));
        abatementFee.setTotalPrice(Double.parseDouble(transDistributeBO.getAbatementTotalInsure()));
        abatementFee.setName("全面保障服务");
        abatementFee.setDescription(transDistributeBO.getAbatementInsure() + "*" + transDistributeBO.getHolidayRentDays());
        abatementFee.setIsOffline(false);
        list.add(abatementFee);

        SDActualFee insureFee = new SDActualFee();
        insureFee.setType(2);
        insureFee.setQuantity(Float.parseFloat(transDistributeBO.getHolidayRentDays()));
        insureFee.setUnitPrice(Double.parseDouble(transDistributeBO.getInsure()));
        insureFee.setTotalPrice(Double.parseDouble(transDistributeBO.getInsureTotalPrices()));
        insureFee.setName("基础保障费");
        insureFee.setDescription(transDistributeBO.getInsure() + "*" + transDistributeBO.getHolidayRentDays());
        insureFee.setIsOffline(false);
        list.add(insureFee);
        vo.setSDActualFeeList(list);

        SDActualFee fee = new SDActualFee();
        fee.setType(15);
        fee.setQuantity(1);
        fee.setUnitPrice(35);
        fee.setTotalPrice(35);
        fee.setName("手续费");
        fee.setDescription(transDistributeBO.getInsure() + "*" + 1);
        fee.setIsOffline(false);
        list.add(fee);

        SDActualFee nightGetService = new SDActualFee();
        nightGetService.setType(6);
        nightGetService.setQuantity(1);
        nightGetService.setUnitPrice(Double.parseDouble(transDistributeBO.getSrvNightGetCost()));
        nightGetService.setTotalPrice(Double.parseDouble(transDistributeBO.getSrvNightGetCost()));
        nightGetService.setName("夜间取车服务费");
        nightGetService.setDescription(transDistributeBO.getSrvNightGetCost() + "*" + 1);
        nightGetService.setIsOffline(false);
        list.add(nightGetService);

        SDActualFee nightReturnService = new SDActualFee();
        nightReturnService.setType(7);
        nightReturnService.setQuantity(1);
        nightReturnService.setUnitPrice(Double.parseDouble(transDistributeBO.getSrvNightReturnCost()));
        nightReturnService.setTotalPrice(Double.parseDouble(transDistributeBO.getSrvNightReturnCost()));
        nightReturnService.setName("夜间还车服务费");
        nightReturnService.setDescription(transDistributeBO.getSrvNightReturnCost() + "*" + 1);
        nightReturnService.setIsOffline(false);
        list.add(nightReturnService);

        vo.setSDActualFeeList(list);
        vo.setActualAmount(rentFee.getTotalPrice() + abatementFee.getTotalPrice() + insureFee.getTotalPrice() + fee.getTotalPrice() + nightGetService.getTotalPrice() + nightReturnService.getTotalPrice());

        Map<String, Object> map = Maps.newHashMap();
        map.put("payload", JSON.toJSONString(vo));
        String now = String.valueOf(System.currentTimeMillis());
        map.put("timestamp", now);
        map.put("sign", MD5Util.getMD5(now + DeliveryConstants.CTRIP_CODE + JSON.toJSONString(vo) + DeliveryConstants.CTRIP_CODE));
        map.put("orderNo", transDistributeBO.getOrderNo());
        log.info("进入订单状态完成回执的订单参数：" + JSON.toJSONString(map));
        aliyunMnsService.asyncSend̨MessageToQueue(JSON.toJSONString(map), DeliveryConstants.CTRIP_STATUS_TRANS_FINISH);
    }

}
