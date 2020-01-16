package com.atzuche.order.delivery.controller;

import com.atzuche.order.delivery.common.DeliveryCarTask;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author 胡春林
 * 测试仁云各接口
 */
@RestController
@RequestMapping("/api")
public class DeliveryController {

    @Autowired
    BaseProducer baseProducer;

    @Autowired
    DeliveryCarTask deliveryCarTask;
    @PostMapping("/delivery/add")
    public ResponseData<?> add() {

        OrderMessage orderMessage =OrderMessage.builder().phone("18164031722").context("测试短信首付").build();
        baseProducer.sendTopicMessage("auto-order-action","order.create",orderMessage);

        return null;
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

    }
}
