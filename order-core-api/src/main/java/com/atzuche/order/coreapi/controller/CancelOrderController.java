package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.enums.DispatcherStatusEnum;
import com.atzuche.order.commons.enums.PlatformCancelReasonEnum;
import com.atzuche.order.commons.vo.req.*;
import com.atzuche.order.commons.vo.res.AdminOrderJudgeDutyResVO;
import com.atzuche.order.commons.vo.res.order.OrderJudgeDutyVO;
import com.atzuche.order.coreapi.service.*;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 11:49
 */

@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class CancelOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderController.class);

    @Autowired
    private CancelOrderService cancelOrderService;
    @Autowired
    private OwnerOrderFineApplyHandelService ownerOrderFineApplyHandelService;
    @Autowired
    private PlatformCancelOrderService platformCancelOrderService;
    @Autowired
    private CancelOrderFeeService cancelOrderFeeService;
    @Autowired
    private CancelOrderAppealService cancelOrderAppealService;
    @Autowired
    private CancelOrderJudgeDutyService cancelOrderJudgeDutyService;


    @AutoDocMethod(description = "取消订单(车主/租客取消订单)", value = "取消订单(车主/租客取消订单)")
    @PostMapping("/normal/cancel")
    public ResponseData<?> cancelOrder(@Valid @RequestBody CancelOrderReqVO cancelOrderReqVO,
                                       BindingResult bindingResult) {
        LOGGER.info("Cancel order.param is,cancelOrderReqVO:[{}]", JSON.toJSONString(cancelOrderReqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        cancelOrderService.cancel(cancelOrderReqVO);
        return ResponseData.success();
    }

    @AutoDocMethod(description = "取消订单(平台代车主/租客取消订单)", value = "取消订单(平台代车主/租客取消订单)")
    @PostMapping("/admin/cancel")
    public ResponseData<?> adminCancelOrder(@Valid @RequestBody AdminCancelOrderReqVO adminCancelOrderReqVO,
                                            BindingResult bindingResult) {
        LOGGER.info("User [{}] console cancel order.param is,adminCancelOrderReqVO:[{}]", adminCancelOrderReqVO.getOperatorName(),
                JSON.toJSONString(adminCancelOrderReqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        cancelOrderService.cancel(adminCancelOrderReqVO, true);
        return ResponseData.success();
    }

    @AutoDocMethod(description = "管理后台取消订单(平台取消)", value = "管理后台取消订单(平台取消)")
    @PostMapping("/admin/platform/cancel")
    public ResponseData<?> adminPlatformCancelOrder(@Valid @RequestBody AdminOrderPlatformCancelReqVO reqVO,
                                                    BindingResult bindingResult) {
        LOGGER.info("User [{}] console platform cancel order.param is,reqVO:[{}]", reqVO.getOperator(),
                JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        platformCancelOrderService.cancel(reqVO.getOrderNo(), reqVO.getOperator(),
                PlatformCancelReasonEnum.from(reqVO.getCancelType()));
        return ResponseData.success();
    }


    @AutoDocMethod(description = "管理后台责任判定", value = "管理后台责任判定")
    @PostMapping("/admin/judgeDuty")
    public ResponseData<?> adminCancelOrderJudgeDuty(@Valid @RequestBody AdminOrderCancelJudgeDutyReqVO reqVO,
                                                     BindingResult bindingResult) {
        LOGGER.info("User [{}] console order judge duty.param is,reqVO:[{}]", reqVO.getOperatorName(),
                JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        cancelOrderService.orderCancelJudgeDuty(reqVO);
        return ResponseData.success();
    }

    @AutoDocMethod(description = "管理后台责任判定信息列表", value = "管理后台责任判定信息列表", response = AdminOrderJudgeDutyResVO.class)
    @PostMapping("/admin/judgeDuty/list")
    public ResponseData<AdminOrderJudgeDutyResVO> adminOrderJudgeDutyList(@Valid @RequestBody AdminOrderJudgeDutyReqVO reqVO,
                                                                          BindingResult bindingResult) {
        LOGGER.info("Console order judge duty list.param is,reqVO:[{}]",
                JSON.toJSONString(reqVO));

        List<OrderJudgeDutyVO> orderJudgeDuties =
                cancelOrderJudgeDutyService.queryOrderJudgeDutysByOrderNo(reqVO.getOrderNo());
        LOGGER.info("Console order judge duty list.result is,orderJudgeDuties:[{}]",
                JSON.toJSONString(orderJudgeDuties));
        if(CollectionUtils.isEmpty(orderJudgeDuties)) {
            return ResponseData.success();
        }
        AdminOrderJudgeDutyResVO resVO = new AdminOrderJudgeDutyResVO();
        resVO.setOrderJudgeDuties(orderJudgeDuties);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "订单取消申诉接口", value = "订单取消申诉接口")
    @PostMapping("/normal/appeal")
    public ResponseData<?> orderCancelAppeal(@Valid @RequestBody OrderCancelAppealReqVO reqVO, BindingResult bindingResult) {
        LOGGER.info("Cancel order appeal.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        cancelOrderAppealService.appeal(reqVO);
        return ResponseData.success();
    }

    @GetMapping("/cancelfee")
    public ResponseData<?> cancelOrderFee(@RequestParam(value = "orderNo", required = true) String orderNo) {
        Integer penalty = cancelOrderFeeService.getCancelPenalty(orderNo);
        return ResponseData.success(penalty);
    }

}
