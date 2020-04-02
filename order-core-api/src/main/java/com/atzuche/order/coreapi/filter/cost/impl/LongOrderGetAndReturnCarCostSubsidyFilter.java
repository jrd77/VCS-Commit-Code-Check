package com.atzuche.order.coreapi.filter.cost.impl;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.entity.dto.cost.req.OrderCostBaseReqDTO;
import com.atzuche.order.coreapi.filter.cost.OrderCostFilter;
import com.atzuche.order.coreapi.submit.exception.OrderCostFilterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 计算长租订单取还车服务费补贴金额
 *
 * @author pengcheng.fu
 * @date 2020/4/1 14:30
 */
@Service
@Slf4j
public class LongOrderGetAndReturnCarCostSubsidyFilter implements OrderCostFilter {

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {

        OrderCostBaseReqDTO baseReqDTO = context.getReqContext().getBaseReqDTO();
        log.info("计算长租订单取还车服务费补贴金额.param is,baseReqDTO:[{}]", JSON.toJSONString(baseReqDTO));

        if (Objects.isNull(baseReqDTO)) {
            log.info("param is empty.");
            return;
        }





    }
}
