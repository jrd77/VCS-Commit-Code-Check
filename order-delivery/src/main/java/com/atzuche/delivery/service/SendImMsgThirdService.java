package com.atzuche.delivery.service;

import com.atzuche.delivery.common.platform.PlatformMessageService;
import com.atzuche.delivery.mapper.DelegationCarAdminMapper;
import com.atzuche.delivery.vo.CarBO;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 胡春林
 * 发送MessageService
 */
@Service
public class SendImMsgThirdService {

    @Autowired
    CarService carService;

    @Autowired
    MemberService memberService;

    @Autowired
    private DelegationCarAdminMapper delegationCarAdminMapper;

    @Autowired
    PlatformMessageService platformMessageService;

    /**
     * 油量和里程
     *
     * @param memNo
     * @param orderNo
     */
    public void sendImMsg94(String memNo, String orderNo, String noFlag, boolean flag, String carNo) {

        memNo = sendMsgPlatformByFlag(flag, memNo, carNo);
        Map<String, String> map = new HashMap<>();
        map.put("messageId", UUID.randomUUID().toString().replaceAll("-", ""));
        map.put("event", "94");
        map.put("memNo", memNo);
        map.put("orderNo", orderNo);
        map.put("isRenter", noFlag);
        platformMessageService.asyncSendPlatform̨MessageToQueue(map);
    }


    /**
     * 油量和里程
     *
     * @param memNo
     * @param orderNo
     */
    public void sendImMsg273(String memNo, String orderNo, String noFlag, boolean flag, String carNo) {

        Map<String, String> map = new HashMap<>();
        map.put("messageId", UUID.randomUUID().toString().replaceAll("-", ""));
        map.put("event", "273");
        map.put("memNo", memNo);
        map.put("orderNo", orderNo);
        map.put("isRenter", noFlag);
        platformMessageService.asyncSendPlatform̨MessageToQueue(map);
    }

    /**
     * 取还车push
     *
     * @param event
     * @param memNo
     * @param orderNo
     * @param flag
     * @param carNo
     */
    public void sendImMsg9798(String event, String memNo, String orderNo, boolean flag, String carNo, String headName, String headPhone) {
        sendMsgPlatform(event, memNo, orderNo, flag, carNo, headName, headPhone);
    }

    /**
     * 取还车push
     *
     * @param event
     * @param memNo
     * @param orderNo
     * @param flag
     * @param carNo
     */
    public void sendImMsg130(String event, String memNo, String orderNo, boolean flag, String carNo, String headName, String headPhone) {
        sendMsgPlatform(event, memNo, orderNo, flag, carNo, headName, headPhone);
    }

    /**
     * 取还车push
     *
     * @param event
     * @param memNo
     * @param orderNo
     * @param flag
     * @param carNo
     */
    public void sendImMsg131(String event, String memNo, String orderNo, boolean flag, String carNo, String headName, String headPhone) {
        sendMsgPlatform(event, memNo, orderNo, flag, carNo, headName, headPhone);
    }

    public void sendMsgPlatform(String event, String memNo, String orderNo, boolean flag, String carNo, String headName, String headPhone) {

        memNo = sendMsgPlatformByFlag(flag, memNo, carNo);
        Map<String, String> map = Maps.newHashMap();
        map.put("messageId", UUID.randomUUID().toString().replaceAll("-", ""));
        map.put("event", event);
        map.put("memNo", memNo);
        map.put("orderNo", orderNo);
        map.put("headName", headName);
        map.put("headPhone", headPhone);
        platformMessageService.asyncSendPlatform̨MessageToQueue(map);
    }

    public String sendMsgPlatformByFlag(boolean flag, String memNo, String carNo) {
        if (flag) {
            CarBO carInfo = carService.getCarInfoByCarNo(carNo != null ? Integer.parseInt(carNo) : 0);
            int ownerType = carInfo.getOwnerType();
            if (ownerType == 35 || ownerType == 30) {
                Long delegatMobile = delegationCarAdminMapper.getAdminMobileByCarNo(carNo != null ? Integer.parseInt(carNo) : 0);
                if (delegatMobile != null) {
                    memNo = memberService.getMemNoByMobile(delegatMobile.toString()) + "";
                }
            }
        }
        return memNo;
    }

}
