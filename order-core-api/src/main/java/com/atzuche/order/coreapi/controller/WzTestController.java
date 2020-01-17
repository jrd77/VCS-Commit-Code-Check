package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WzTestController {
    @Autowired
    private IllegalInOrderQueryTask illegalInOrderQueryTask;

    @Autowired
    private IllegalQueryTask illegalQueryTask;

    @Autowired
    private IllegalSmsSendTask illegalSmsSendTask;

    @Autowired
    private IllegalTransStatTask illegalTransStatTask;

    @Autowired
    private OrderIllegalRenYunTask orderIllegalRenYunTask;

    @Autowired
    private UnDispatchOrderTask unDispatchOrderTask;

    /**
     * 每天定时查询当前进行中的订单，查询是否有违章记录
     */
    @GetMapping("/wz/illegalInOrderQueryTaskTest")
    public String illegalInOrderQueryTaskTest() {
        try {
            illegalInOrderQueryTask.execute("");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    /**
     * 查询按规则配置日期内完成的订单
     */
    @GetMapping("/wz/illegalQueryTask")
    public String illegalQueryTask(){
        try {
            illegalQueryTask.execute("");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    /**
     * 查询违章待发短信，jPush通知和App推送信息
     */
    @GetMapping("/wz/illegalSmsSendTask")
    public String illegalSmsSendTask(){
        try {
            illegalSmsSendTask.execute("");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    /**
     * 每天定时处理结算前15分钟订单，查询是否有违章记录
     */
    @GetMapping("/wz/illegalTransStatTask")
    public String illegalTransStatTask(){
        try {
            illegalTransStatTask.execute("");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }
    /**
     * 每天定时处理 实际还车15天后系统自动生成，调用流程系统，查询是否有违章记录
     */
    @GetMapping("/wz/orderIllegalRenYunTask")
    public String orderIllegalRenYunTask(){
        try {
            orderIllegalRenYunTask.execute("");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    @GetMapping("/wz/unDispatchOrderTask")
    public String unDispatchOrderTask(){
        try {
            unDispatchOrderTask.execute("");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }
}
