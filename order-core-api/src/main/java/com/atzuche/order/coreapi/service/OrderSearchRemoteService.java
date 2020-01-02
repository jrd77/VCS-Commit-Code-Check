package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.autoyol.search.api.OrderSearchService;
import com.autoyol.search.entity.ErrorCode;
import com.autoyol.search.entity.ResponseData;
import com.autoyol.search.entity.ViolateBO;
import com.autoyol.search.vo.OrderVO;
import com.autoyol.search.vo.ViolateVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderSearchRemoteService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class OrderSearchRemoteService {

    private Logger logger = LoggerFactory.getLogger(OrderSearchRemoteService.class);

    @Resource
    private OrderSearchService orderSearchService;

    public List<IllegalToDO> violateProcessOrder() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时查询当前进行中的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderSearchRemoteService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            if(orderResponseData != null && orderResponseData.getResCode() != null
                    && ErrorCode.SUCCESS.getCode().equals(orderResponseData.getResCode()) && orderResponseData.getData() != null){
                List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
                return convertDto(orderList);
            }else{
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("执行 每天定时查询当前进行中的订单 异常",e);
            Cat.logError("执行 每天定时查询当前进行中的订单 异常",e);
        }finally {
            t.complete();
        }
        return new ArrayList<>();
    }

    private List<IllegalToDO> convertDto(List<ViolateBO> orderList) {
        if(CollectionUtils.isEmpty(orderList)){
            return new ArrayList<>();
        }
        List<IllegalToDO> results = new ArrayList<>();
        IllegalToDO dto = null;
        for (ViolateBO violate : orderList) {
            dto = new IllegalToDO();
            dto.setOrderNo(violate.getOrderNo());
            dto.setRegNo(violate.getCarNo());
            dto.setPlateNum(violate.getPlateNum());
            dto.setRenterPhone(violate.getRenterPhone());
            dto.setRentNo(violate.getRenterNo());
            dto.setFrameNo(violate.getFrameNo());
            dto.setEngineNum(violate.getEngineNum());
            if(violate.getRentTime() != null){
                dto.setRentTime(DateUtils.formateLocalDateTime(violate.getRentTime()));
            }
            if(violate.getRevertTime() != null){
                dto.setRevertTime(DateUtils.formateLocalDateTime(violate.getRevertTime()));
            }
            dto.setCityName(violate.getCityName());
            dto.setEngineSource(violate.getEngineSource());
            results.add(dto);
        }
        return results;
    }
}
