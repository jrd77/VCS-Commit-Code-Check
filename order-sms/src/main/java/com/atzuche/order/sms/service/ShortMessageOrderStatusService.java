package com.atzuche.order.sms.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.ProcessRespDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 */
@Service
public class ShortMessageOrderStatusService {

    private Logger logger = LoggerFactory.getLogger(ShortMessageOrderStatusService.class);

    @Autowired
    FeignOrderDetailService feignOrderDetailService;

    /**
     * 獲取進行中的訂單
     * @return
     */
    public List<OrderDTO> findProcessOrderInfo() {

        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时查询当前进行中的订单");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDetailService.queryInProcess");
            ResponseData<ProcessRespDTO> orderResponseData = feignOrderDetailService.queryInProcess();
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            if (Objects.nonNull(orderResponseData) && orderResponseData.getResCode() != null
                    && ErrorCode.SUCCESS.getCode().equals(orderResponseData.getResCode())
                    && orderResponseData.getData() != null) {
                List<OrderDTO> orderList  = orderResponseData.getData().getOrderDTOs();
                if (CollectionUtils.isEmpty(orderList)) {
                    return new ArrayList<>();
                } else {
                    return orderList;
                }
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("执行 每天定时查询当前进行中的订单 异常", e);
            Cat.logError("执行 每天定时查询当前进行中的订单 异常", e);
        } finally {
            t.complete();
        }
        return new ArrayList<>();
    }

    /**
     * 獲取已取消的訂單
     * @return
     */
    public List<OrderDTO> findCancelOrderInfo() {

        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时查询所有已取消的订单");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "feignOrderDetailService.queryRefuse");
            ResponseData<ProcessRespDTO> orderResponseData = feignOrderDetailService.queryRefuse();
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            if (Objects.nonNull(orderResponseData) && orderResponseData.getResCode() != null
                    && ErrorCode.SUCCESS.getCode().equals(orderResponseData.getResCode())
                    && orderResponseData.getData() != null) {
                List<OrderDTO> orderList  = orderResponseData.getData().getOrderDTOs();
                if (CollectionUtils.isEmpty(orderList)) {
                    return new ArrayList<>();
                } else {
                    return orderList;
                }
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("执行 每天定时查询所有已取消的订单 异常", e);
            Cat.logError("执行 每天定时查询所有已取消的订单 异常", e);
        } finally {
            t.complete();
        }
        return new ArrayList<>();
    }

}
