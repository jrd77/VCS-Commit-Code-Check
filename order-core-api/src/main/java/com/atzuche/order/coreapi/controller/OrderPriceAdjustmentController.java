package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.vo.req.OrderPriceAdjustmentReqVO;
import com.atzuche.order.commons.vo.res.OrderPriceAdjustmentVO;
import com.atzuche.order.ownercost.service.OrderPriceAdjustmentService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequestMapping("/order/price/adjustment")
@RestController
public class OrderPriceAdjustmentController {
    @Autowired
    private OrderPriceAdjustmentService orderPriceAdjustmentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPriceAdjustmentController.class);

    @RequestMapping("/list")
    public ResponseData <List <OrderPriceAdjustmentVO>> getOrderPriceAdjustmentByMembers(@RequestBody OrderPriceAdjustmentReqVO reqVO) {
        String json = null;
        try {
            List <OrderPriceAdjustmentVO> adjustmentVOS = orderPriceAdjustmentService.selectEnableObjsByOrderNoAndMemberCode(reqVO);

            return ResponseData.success(adjustmentVOS);
        } catch (OrderException e) {
            Cat.logError("获取车主给租客调价或租客给车主调价异常:" + json, e);
            LOGGER.error("获取车主给租客调价或租客给车主调价异常:" + json, e);

            return new ResponseData <>(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            Cat.logError("获取车主给租客调价或租客给车主调价异常:" + json, e);
            LOGGER.error("获取车主给租客调价或租客给车主调价异常:" + json, e);
        }

        return ResponseData.error();
    }

    @RequestMapping("/type")
    public ResponseData <OrderPriceAdjustmentVO> getOrderPriceAdjustmentByMemberOfType(@RequestBody OrderPriceAdjustmentReqVO reqVO) {
        String json = null;
        try {
            OrderPriceAdjustmentVO adjustment = orderPriceAdjustmentService.selectEnableObjByOrderNoAndMemberCode(reqVO);

            return ResponseData.success(adjustment);
        } catch (OrderException e) {
            Cat.logError("获取车主给租客调价或租客给车主调价异常:" + json, e);
            LOGGER.error("获取车主给租客调价或租客给车主调价异常:" + json, e);

            return new ResponseData <>(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            Cat.logError("获取车主给租客调价或租客给车主调价异常:" + json, e);
            LOGGER.error("获取车主给租客调价或租客给车主调价异常:" + json, e);
        }

        return ResponseData.error();
    }

    @PostMapping("/insert")
    public ResponseData <Boolean> insert(@RequestBody OrderPriceAdjustmentReqVO reqVO) {
        String json = null;
        try {
            Integer count = orderPriceAdjustmentService.insertAndUpdateIsDelete(reqVO);
            boolean flag = Objects.equals(count, 1);
            return ResponseData.success(flag);
        } catch (OrderException e) {
            Cat.logError("获取车主给租客调价或租客给车主调价异常:" + json, e);
            LOGGER.error("获取车主给租客调价或租客给车主调价异常:" + json, e);

            return new ResponseData <>(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            Cat.logError("获取车主给租客调价或租客给车主调价异常:" + json, e);
            LOGGER.error("获取车主给租客调价或租客给车主调价异常:" + json, e);
        }

        return ResponseData.error();
    }
}
