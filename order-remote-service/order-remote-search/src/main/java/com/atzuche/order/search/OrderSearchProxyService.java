package com.atzuche.order.search;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.search.dto.ConflictOrderSearchReqDTO;
import com.atzuche.order.search.dto.OrderInfoDTO;
import com.autoyol.search.api.OrderSearchService;
import com.autoyol.search.entity.ByCarBO;
import com.autoyol.search.entity.ErrorCode;
import com.autoyol.search.entity.ResponseData;
import com.autoyol.search.vo.ByCarVO;
import com.autoyol.search.vo.OrderVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装对远程订单搜索服务的相关调用
 *
 * @author pengcheng.fu
 * @date 2020/3/6 12:05
 */

@Service
public class OrderSearchProxyService {

    private final static Logger logger = LoggerFactory.getLogger(OrderSearchProxyService.class);


    @Resource
    private OrderSearchService orderSearchService;


    /**
     * 同意订单时拉取租期重叠的订单列表(包含新老订单)
     *
     * @param conflictOrderSearch 请求参数
     * @return List<OrderInfoDTO>
     */
    public List<OrderInfoDTO> getAgreeOrderConflictList(ConflictOrderSearchReqDTO conflictOrderSearch) {
        logger.info("同意订单时拉取租期重叠的订单列表(包含新老订单).param is,conflictOrderSearch:[{}]", JSON.toJSONString(conflictOrderSearch));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单搜索服务");
        try {
            ByCarVO reqVO = new ByCarVO();
            BeanUtils.copyProperties(conflictOrderSearch, reqVO);
            logger.info("Invoke remote method.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
            Cat.logEvent(CatConstants.FEIGN_METHOD, "orderSearchService.byCarOrderInfo");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ByCarBO>> response = orderSearchService.byCarOrderInfo(reqVO);
            logger.info("Invoke remote method.result is,response:[{}]", JSON.toJSONString(response));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(response));
            if (null == response || ObjectUtils.isEmpty(response.getData())) {
                return new ArrayList<>();
            }

            if (StringUtils.equals(response.getResCode(), ErrorCode.SUCCESS.getCode())) {
                return buildOrderInfoDTO(response.getData());
            }
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("Feign 获取同意订单时拉取租期重叠的订单列表(包含新老订单)失败", e);
            t.setStatus(e);
            Cat.logError("Feign 获取同意订单时拉取租期重叠的订单列表(包含新老订单)失败", e);
            throw e;
        } finally {
            t.complete();
        }

        return new ArrayList<>();
    }


    /**
     * 构建订单信息列表
     *
     * @param order 返回信息
     * @return List<OrderInfoDTO> 订单信息列表
     */
    private List<OrderInfoDTO> buildOrderInfoDTO(OrderVO<ByCarBO> order) {
        List<ByCarBO> list = order.getOrderList();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<OrderInfoDTO> orderInfos = new ArrayList<>();
        list.forEach(o -> {
            OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
            BeanUtils.copyProperties(o, orderInfoDTO);
            orderInfos.add(orderInfoDTO);
        });
        logger.info("同意订单时拉取租期重叠的订单列表.orderInfos:[{}]",JSON.toJSONString(orderInfos));
        return orderInfos;
    }
}
