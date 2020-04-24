package com.atzuche.order.delivery.controller;

import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoPriceService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.CodeUtils;
import com.atzuche.order.delivery.vo.handover.HandoverCarInfoDTO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;


/**
 * @author 胡春林
 * 测试仁云各接口
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class DeliveryController {

    @Autowired
    BaseProducer baseProducer;

    @Autowired
    DeliveryCarTask deliveryCarTask;

    @Autowired
    DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    CodeUtils codeUtils;



   // @GetMapping("/test")
    public ResponseData addHandoverCarInfo(@RequestParam(value = "orderNo") String orderNo,@RequestParam(value = "type") Integer type){

        //String orderNo = "83579391400299";
        HandoverCarVO handoverCarVO = new HandoverCarVO();
        RenterOrderDeliveryEntity lastOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(orderNo, type);
        if (Objects.nonNull(lastOrderDeliveryEntity)) {
            lastOrderDeliveryEntity.setIsDelete(1);
            renterOrderDeliveryService.updateDeliveryByPrimaryKey(lastOrderDeliveryEntity);
        }

        RenterOrderDeliveryEntity orderDeliveryEntity = new RenterOrderDeliveryEntity();
        BeanUtils.copyProperties(lastOrderDeliveryEntity,orderDeliveryEntity);
        orderDeliveryEntity.setIsDelete(0);
        orderDeliveryEntity.setOrderNoDelivery(codeUtils.createDeliveryNumber());
        orderDeliveryEntity.setAheadOrDelayTimeInfo(null, null, null);
        orderDeliveryEntity.setStatus(1);
        orderDeliveryEntity.setIsNotifyRenyun(1);
        renterOrderDeliveryService.insert(orderDeliveryEntity);

        HandoverCarInfoDTO handoverCarInfoDTO = new HandoverCarInfoDTO();
        handoverCarInfoDTO.setCreateOp("");
        handoverCarInfoDTO.setOrderNo(orderDeliveryEntity.getOrderNo());
        handoverCarInfoDTO.setRenterOrderNo(orderDeliveryEntity.getRenterOrderNo());
        handoverCarInfoDTO.setAheadTimeAndType(0,0,orderDeliveryEntity);
        handoverCarInfoDTO.setRealReturnAddr(orderDeliveryEntity.getRenterGetReturnAddr());
        handoverCarInfoDTO.setRealReturnAddrLat(orderDeliveryEntity.getRenterGetReturnAddrLat());
        handoverCarInfoDTO.setRealReturnAddrLon(orderDeliveryEntity.getRenterGetReturnAddrLon());
        handoverCarVO.setHandoverCarInfoDTO(handoverCarInfoDTO);
        handoverCarService.addHandoverCarInfo(handoverCarVO,1);
        handoverCarService.addHandoverCarInfo(handoverCarVO,2);
        return ResponseData.success();
    }

   // @PostMapping("/delivery/add")
    public ResponseData<?> add() {

        //给车主租客发送支付成功短信
//        OrderMessage orderMessage = OrderMessage.builder().build();
//
////        Map paramMaps = Maps.newHashMap();
////        paramMaps.put("indexUrl","http://t.cn/R72yPL2");
//        Map smsMap = SmsParamsMapUtil.getParamsMap("88281130400299", ShortMessageTypeEnum.NOTIFY_RENTER_TRANS_REQACCEPTED.getValue(), ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_OWNER.getValue(), null);
//        orderMessage.setMap(smsMap);
////        Map map = SmsParamsMapUtil.getParamsMap("17814260300299", PushMessageTypeEnum.RENTER_PAY_CAR_SUCCESS.getValue(), PushMessageTypeEnum.RENTER_PAY_CAR_2_OWNER.getValue(), null);
////        orderMessage.setPushMap(map);
//        baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_SETTLEMENT_SUCCESS.exchange,NewOrderMQActionEventEnum.ORDER_SETTLEMENT_SUCCESS.routingKey,orderMessage);

// OrderMessage orderMessage =OrderMessage.builder().phone("13628645717").context("renterOptCancel").build();
//        baseProducer.sendTopicMessage("auto-order-action","action.order.create4",orderMessage);

//        return null;
//        RenYunFlowOrderDTO renYunFlowOrderDTO = new RenYunFlowOrderDTO();
//
//        renYunFlowOrderDTO.setOrdernumber("65656565");
//        renYunFlowOrderDTO.setOrderType("0");
//        renYunFlowOrderDTO.setServicetype("back");
//        renYunFlowOrderDTO.setTermtime("2020-01-02 15:02");
//        renYunFlowOrderDTO.setReturntime("2020-01-02 15:02");
//        renYunFlowOrderDTO.setCarno("666666");
//        renYunFlowOrderDTO.setVehiclemodel("123");
//        renYunFlowOrderDTO.setVehicletype("1");
//        renYunFlowOrderDTO.setDeliverycarcity("上海市");
//        renYunFlowOrderDTO.setDefaultpickupcaraddr("上海市");
//        renYunFlowOrderDTO.setAlsocaraddr("上海");
//        renYunFlowOrderDTO.setOwnername(" shanghic deshi ");
//        renYunFlowOrderDTO.setOwnerphone("123123");
//        renYunFlowOrderDTO.setSuccessordenumber("11");
//        renYunFlowOrderDTO.setTenantname("测试");
//        renYunFlowOrderDTO.setTenantphone("13628645717");
//        renYunFlowOrderDTO.setTenantturnoverno("10");
//        renYunFlowOrderDTO.setPickupcaraddr("上海市");
//        renYunFlowOrderDTO.setOwnerType("1");
//        renYunFlowOrderDTO.setSceneName("12");
//        renYunFlowOrderDTO.setDisplacement("123");
//        renYunFlowOrderDTO.setSource("1");
//        renYunFlowOrderDTO.setAfterTime(DateUtils.formate(new Date(), DateUtils.DATE_DEFAUTE_1));
//        renYunFlowOrderDTO.setBeforeTime(DateUtils.formate(new Date(), DateUtils.DATE_DEFAUTE_1));
//        renYunFlowOrderDTO.setGetKilometre("0");
//        renYunFlowOrderDTO.setReturnKilometre("0");
//        renYunFlowOrderDTO.setDelegaAdmin("托管人员");
//        renYunFlowOrderDTO.setDelegaAdminPhone("13628645717");
//        renYunFlowOrderDTO.setGuideDayPrice("123");
//        renYunFlowOrderDTO.setDetectStatus("2");
//        renYunFlowOrderDTO.setDayMileage("1221");
//        renYunFlowOrderDTO.setOfflineOrderType("2");
//        renYunFlowOrderDTO.setSsaRisks("12");
//        renYunFlowOrderDTO.setEmerContact("托管人员");
//        renYunFlowOrderDTO.setEmerContactPhone("上海市");
//        renYunFlowOrderDTO.setTankCapacity("123");
//        renYunFlowOrderDTO.setRealGetCarLon("123");
//        renYunFlowOrderDTO.setRealGetCarLat("123");
//        renYunFlowOrderDTO.setRealReturnCarLat("123");
//        renYunFlowOrderDTO.setRealReturnCarLon("12312");
//        renYunFlowOrderDTO.setCarLat("33.222222");
//        renYunFlowOrderDTO.setCarLon("114.333333");
//        renYunFlowOrderDTO.setEngineType("1");
//        renYunFlowOrderDTO.setDayUnitPrice("213");
//        renYunFlowOrderDTO.setDisplacement("12312");
//        renYunFlowOrderDTO.setHolidayAverage("12312");
//        renYunFlowOrderDTO.setHolidayPrice("213");
//        renYunFlowOrderDTO.setRentAmt("123");
//        renYunFlowOrderDTO.setPartner("213");
//        renYunFlowOrderDTO.setOilPrice("123");
//        renYunFlowOrderDTO.setDepositPayTime(DateUtils.formate(new Date(),"yyyyMMddHHmmss"));
//        renYunFlowOrderDTO.setRenterMemberFlag("1");
//        renYunFlowOrderDTO.setUseType("1");
//        renYunFlowOrderDTO.setTakeNote("1");
//        renYunFlowOrderDTO.setRiskControlAuditState("1");
//        renYunFlowOrderDTO.setFlightNo("1");
//        renYunFlowOrderDTO.setYear(2F);
//        renYunFlowOrderDTO.setOilScaleDenominator("16");
//        renYunFlowOrderDTO.setOwnerNo("12312");
//        renYunFlowOrderDTO.setRenterNo("123");
//        renYunFlowOrderDTO.setOrderType("0");
//        renYunFlowOrderDTO.setChannelType("0");
//        renYunFlowOrderDTO.setOwnerGetAddr("上海市");
//        renYunFlowOrderDTO.setOwnerGetLon("12312");
//        renYunFlowOrderDTO.setOwnerGetLat("12321");
//        renYunFlowOrderDTO.setOwnerReturnAddr("上海市");
//        renYunFlowOrderDTO.setOwnerReturnLat("12");
//        renYunFlowOrderDTO.setOwnerReturnLon("12");
//        renYunFlowOrderDTO.setOwnerLables("12321");
//        renYunFlowOrderDTO.setOwnerLevel("12");
//        renYunFlowOrderDTO.setTenantLables("123");
//        renYunFlowOrderDTO.setTenantLevel("123");
//
//        deliveryCarTask.addRenYunFlowOrderInfo(renYunFlowOrderDTO);
//        return ResponseData.success();
        return null;

    }

//    /**
//     * 获取油费
//     * @param deliveryCarPriceReqVO
//     * @return
//     */
//    @PostMapping("/oil/getOilCrash")
//    public ResponseData<DeliveryOilCostRepVO> getOilCostByRenterOrderNo(@RequestBody DeliveryCarPriceReqVO deliveryCarPriceReqVO, BindingResult bindingResult) {
//        log.info("获取油费入参：{}", deliveryCarPriceReqVO.toString());
//        BindingResultUtil.checkBindingResult(bindingResult);
//        try {
//            DeliveryOilCostRepVO deliveryOilCostRepVO = DeliveryOilCostRepVO.builder().build();
//            DeliveryOilCostVO deliveryOilCostVO = deliveryCarInfoPriceService.getOilCostByRenterOrderNo(deliveryCarPriceReqVO.getOrderNo(), deliveryCarPriceReqVO.getCarEngineType());
//            BeanUtils.copyProperties(deliveryOilCostVO,deliveryOilCostRepVO);
//            return ResponseData.success(deliveryOilCostRepVO);
//        } catch (Exception e) {
//            log.error("获取油费异常:", e);
//            return ResponseData.error();
//        }
//    }
}
