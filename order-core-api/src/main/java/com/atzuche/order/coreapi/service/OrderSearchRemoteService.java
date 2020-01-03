package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.renterwz.entity.WzQueryDayConfEntity;
import com.atzuche.order.renterwz.service.WzQueryDayConfService;
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
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private WzQueryDayConfService wzQueryDayConfService;

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

    public List<IllegalToDO> violateSettleOrder() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时处理结算前15分钟订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderSearchRemoteService.violateSettleOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateSettleOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            if(orderResponseData != null && orderResponseData.getResCode() != null
                    && ErrorCode.SUCCESS.getCode().equals(orderResponseData.getResCode()) && orderResponseData.getData() != null){
                List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
                return convertDto(orderList);
            }else{
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("执行 每天定时处理结算前15分钟订单 异常",e);
            Cat.logError("执行 每天定时处理结算前15分钟订单 异常",e);
        }finally {
            t.complete();
        }
        return new ArrayList<>();
    }

     public List<IllegalToDO> violatePendingOrder() {
            try {
                //获取城市违章天数配置
                List<WzQueryDayConfEntity> confs = wzQueryDayConfService.queryAll();
                //转换数据
                Map<Integer, List<Integer>> map = wzQueryDayConfService.convertCityByList(confs);
                //查询订单信息
                return this.queryIllegalList(map);
            } catch (Exception e) {
                logger.error("执行 queryAll  or  convertCityByList 异常",e);
                Cat.logError("执行 queryAll  or  convertCityByList 异常",e);
            }
            return new ArrayList<>();
        }

    private List<IllegalToDO> queryIllegalList(Map<Integer, List<Integer>> map) {
        if (map.isEmpty() || map == null) {
            return null;
        }
        //用TREEMAP，默认按KEY的增值排序
        TreeMap<Integer, List<Integer>> treeMap = new TreeMap<>(map);
        //15,18,30,33
        Set<Integer> queryDays = treeMap.keySet();
        //城市编码
        List<Integer> cityCodeAll = new ArrayList<Integer>();

        List<IllegalToDO> all = new ArrayList<>();
        //最小的天数
        Integer minKey = treeMap.firstKey();
        ViolateVO reqVO = null;
        for (Integer day : queryDays) {
            List<Integer> cityCode = treeMap.get(day);
            //查询天数
            reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setDate(DateUtils.minDays(day));
            //所有城市的列表,15天的
            cityCodeAll.addAll(cityCode);
            List<IllegalToDO> list = this.violatePendingOrderByDay(reqVO);
            //不符合条件的过滤
            list = list.parallelStream().filter(illegal -> isInQueryRange(illegal, cityCode, day, minKey)).collect(Collectors.toList());
            all.addAll(list);
        }
        //30
        Integer maxKey = treeMap.lastKey();
        //查询最大
        reqVO = new ViolateVO();
        reqVO.setPageNum(1);
        reqVO.setPageSize(10000);
        reqVO.setDate(DateUtils.minDays(maxKey));
        List<IllegalToDO> list = this.violatePendingOrderByDay(reqVO);
        //过滤城市cityCodeAll集合之外的数据（默认都以最大值查询）
        list = list.parallelStream().filter(illegal -> isOutOfQueryRange(illegal, cityCodeAll)).collect(Collectors.toList());
        all.addAll(list);
        return all;
    }

    private boolean isInQueryRange(IllegalToDO illegal, List<Integer> cityCode, Integer days, Integer minKey) {
        if (illegal!=null
                && illegal.getCityCode()!=null
                && cityCode!=null
                && cityCode.size()>0) {
            //15天的订单的特殊处理，春节订单往后延时处理
            if(minKey!=null && days!=null && days.equals(minKey)){
                if (cityCode.contains(illegal.getCityCode())) {
                    String startTime = DateUtils.formate(illegal.getRentTime(),DateUtils.DATE_DEFAUTE);
                    String endTime = DateUtils.formate(illegal.getRevertTime(),DateUtils.DATE_DEFAUTE);
                    if (DateUtils.isFestival(Long.parseLong(startTime), Long.parseLong(endTime))) {
                        return false;
                    }
                    return true;
                }
            }else{
                //正常的情况  非15天的情况   包含城市列表且  排除 附赠套餐的订单。
                if (cityCode.contains(illegal.getCityCode())) {
                    return true;
                }
            }
        }else {
            return false;
        }
        return false;
    }

    private boolean isOutOfQueryRange(IllegalToDO illegal, List<Integer> cityCode) {
        if (illegal!=null
                && illegal.getCityCode()!=null
                && cityCode!=null
                && cityCode.size()>0) {

            String startTime = DateUtils.formate(illegal.getRentTime(),DateUtils.DATE_DEFAUTE);
            String endTime = DateUtils.formate(illegal.getRevertTime(),DateUtils.DATE_DEFAUTE);
            /*
             * 配置的城市编码之外的，都按最大的时间来处理  111111  30
             */
            if (!cityCode.contains(illegal.getCityCode())
                    || DateUtils.isFestival(Long.parseLong(startTime), Long.parseLong(endTime))) {
                return true;
            }
        }else {
            return false;
        }
        return false;
    }

    private List<IllegalToDO> violatePendingOrderByDay(ViolateVO reqVO) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询按规则配置日期内完成的订单，获取待查询违章的对象列表");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD,"OrderSearchRemoteService.violatePendingOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violatePendingOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            if(orderResponseData != null && orderResponseData.getResCode() != null
                    && ErrorCode.SUCCESS.getCode().equals(orderResponseData.getResCode()) && orderResponseData.getData() != null){
                List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
                return convertDto(orderList);
            }else{
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 异常",e);
            Cat.logError("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 异常",e);
        }finally {
            t.complete();
        }
        return null;
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
            dto.setCityCode(violate.getCity());
            results.add(dto);
        }
        return results;
    }
}
