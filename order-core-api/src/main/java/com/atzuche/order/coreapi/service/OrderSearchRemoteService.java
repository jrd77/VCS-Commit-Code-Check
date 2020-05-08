package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.exceptions.RemoteCallException;
import com.atzuche.order.coreapi.entity.dto.SuccessOrderStaCount;
import com.atzuche.order.coreapi.listener.sms.SMSOrderBaseEventService;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.parentorder.dto.SuccessOrderDTO;
import com.atzuche.order.renterwz.service.DeRenCarApproachCitiesService;
import com.atzuche.order.renterwz.service.RenterOrderWzDetailService;
import com.atzuche.order.renterwz.vo.OrderInfoForIllegal;
import com.atzuche.order.renterwz.entity.WzQueryDayConfEntity;
import com.atzuche.order.renterwz.service.WzQueryDayConfService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.autoyol.car.api.model.enums.OwnerTypeEnum;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.search.api.OrderSearchService;
import com.autoyol.search.entity.ErrorCode;
import com.autoyol.search.entity.ResponseData;
import com.autoyol.search.entity.ViolateBO;
import com.autoyol.search.vo.OrderVO;
import com.autoyol.search.vo.ViolateVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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

    @Resource
    private OrderStatusService orderStatusService;

    @Resource
    private RenterOrderWzDetailService renterOrderWzDetailService;

    @Resource
    private RenterGoodsService renterGoodsService;

    @Resource
    private DeRenCarApproachCitiesService deRenCarApproachCitiesService;

    @Resource
    private OrderService orderService;

    @Autowired
    SMSOrderBaseEventService smsOrderBaseEventService;

    @Value("${violation.h5.url}")
    private String h5Url;

    private static final List<Integer> CITIES = Arrays.asList(330100,320100,310100,110100,440100,440300);

    private static final LocalDateTime FESTIVAL_START_TIME = LocalDateTime.of(2019,10,1,0,0,0);
    private static final LocalDateTime FESTIVAL_END_TIME = LocalDateTime.of(2019,10,7,23,59,59);

    public List<IllegalToDO> violateProcessOrder() {
        //FIXME:需要修改成翻页
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时查询当前进行中的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("1");
            reqVO.setDate(DateUtils.minDays(2));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            return convertDto(orderList);
        } catch (Exception e) {
            logger.error("执行 每天定时查询当前进行中的订单 异常",e);
            Cat.logError("执行 每天定时查询当前进行中的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<IllegalToDO> violateSettleOrder() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "每天定时处理结算前15分钟订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("3");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            return convertDto(orderList);
        } catch (Exception e) {
            logger.error("执行 每天定时处理结算前15分钟订单 异常",e);
            Cat.logError("执行 每天定时处理结算前15分钟订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
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
        if (map.isEmpty()) {
            return null;
        }
        //用TREEMAP，默认按KEY的增值排序
        TreeMap<Integer, List<Integer>> treeMap = new TreeMap<>(map);
        //15,18,30,33
        Set<Integer> queryDays = treeMap.keySet();
        //城市编码
        List<Integer> cityCodeAll = new ArrayList<>();

        List<IllegalToDO> all = new ArrayList<>();
        //最小的天数
        Integer minKey = treeMap.firstKey();
        ViolateVO reqVO;
        for (Integer day : queryDays) {
            List<Integer> cityCode = treeMap.get(day);
            //查询天数
            reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("2");
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
        reqVO.setType("2");
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
            if(days!=null && days.equals(minKey)){
                if (cityCode.contains(illegal.getCityCode())) {
                    String startTime = DateUtils.formate(illegal.getRentTime(),DateUtils.DATE_DEFAUTE);
                    String endTime = DateUtils.formate(illegal.getRevertTime(),DateUtils.DATE_DEFAUTE);
                    return !DateUtils.isFestival(Long.parseLong(startTime), Long.parseLong(endTime));
                }
            }else{
                //正常的情况  非15天的情况   包含城市列表且  排除 附赠套餐的订单。
                return cityCode.contains(illegal.getCityCode());
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
            return !cityCode.contains(illegal.getCityCode())
                    || DateUtils.isFestival(Long.parseLong(startTime), Long.parseLong(endTime));
        }else {
            return false;
        }
    }

    private List<IllegalToDO> violatePendingOrderByDay(ViolateVO reqVO) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询按规则配置日期内完成的订单，获取待查询违章的对象列表");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            logger.info("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 [{}]",orderList);
            t.setStatus(Transaction.SUCCESS);
            return convertDto(orderList);
        } catch (Exception e) {
            logger.error("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 异常",e);
            Cat.logError("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    private List<IllegalToDO> convertDto(List<ViolateBO> orderList) {
        if(CollectionUtils.isEmpty(orderList)){
            return new ArrayList<>();
        }
        List<IllegalToDO> results = new ArrayList<>();
        IllegalToDO dto;
        for (ViolateBO violate : orderList) {
            dto = new IllegalToDO();
            dto.setOrderNo(violate.getOrderNo());
            dto.setRegNo(String.valueOf(violate.getCarNo()));
            dto.setPlateNum(violate.getPlateNum());
            dto.setRenterPhone(violate.getRenterPhone());
            dto.setRentNo(violate.getRenterNo());
            dto.setFrameNo(violate.getFrameNo());
            dto.setEngineNum(violate.getEngineNum());
            if(violate.getRentTime() != null){
                dto.setRentTime(DateUtils.localDateTimeToDate(violate.getRentTime()));
            }
            if(violate.getRevertTime() != null){
                dto.setRevertTime(DateUtils.localDateTimeToDate(violate.getRevertTime()));
            }
            dto.setCityName(violate.getCityName());
            dto.setEngineSource(String.valueOf(violate.getEngineSource()));
            if(StringUtils.isNotBlank(violate.getCity())){
                dto.setCityCode(Integer.parseInt(violate.getCity()));
            }
            String cities = deRenCarApproachCitiesService.queryCitiesByOrderNoAndCarNum(violate.getOrderNo(),violate.getPlateNum());
            dto.setCities(cities);
            results.add(dto);
        }
        return results;
    }

    public List<OrderInfoForIllegal> renterWzOrders() {
        List<WzQueryDayConfEntity> confs = wzQueryDayConfService.queryAll();
        confs = this.filterQueryDayConf(confs);
        Set<Integer> days = confs.stream().map(WzQueryDayConfEntity::getIllegalQueryDay).collect(Collectors.toSet());
        //节假日
        days.add(30);
        List<ViolateBO> violates = new ArrayList<>();
        for (Integer day : days) {
            List<ViolateBO> violateBos = violatePendingOrderByDay(day);
            if(!CollectionUtils.isEmpty(violateBos)){
                violates.addAll(violateBos);
            }
        }
        if(CollectionUtils.isEmpty(violates)){
            return new ArrayList<>();
        }
        return filter(violates,confs);
    }

    private List<WzQueryDayConfEntity> filterQueryDayConf(List<WzQueryDayConfEntity> confs) {
        List<WzQueryDayConfEntity> rs = new ArrayList<>();
        if(CollectionUtils.isEmpty(confs)){
            return rs;
        }
        for (WzQueryDayConfEntity conf : confs) {
            if(CITIES.contains(conf.getCityCode())){
                rs.add(conf);
            }
        }
        return rs;
    }

    private List<OrderInfoForIllegal> filter(List<ViolateBO> violates, List<WzQueryDayConfEntity> confs) {
        //所有订单
        List<ViolateBO> violateList  = new ArrayList<>();
        //节假日订单
        List<ViolateBO> festivalViolates = violates
                .stream()
                .filter(dto ->
                        (dto.getRentTime().isAfter(FESTIVAL_START_TIME) && dto.getRentTime().isBefore(FESTIVAL_END_TIME)) ||
                                (dto.getRevertTime().isAfter(FESTIVAL_START_TIME) && dto.getRevertTime().isBefore(FESTIVAL_END_TIME)) ||
                                (dto.getRentTime().isAfter(FESTIVAL_START_TIME) && dto.getRevertTime().isBefore(FESTIVAL_END_TIME)) ||
                                (dto.getRentTime().isBefore(FESTIVAL_START_TIME) && dto.getRevertTime().isAfter(FESTIVAL_END_TIME))).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(festivalViolates)){
            violateList.addAll(festivalViolates);
        }
        //非节假日订单
        for (WzQueryDayConfEntity conf : confs) {
            /*and (a.rent_time &gt; #{festivalEndTime} or a.`revert_time` &lt; #{festivalStartTime})*/
            List<ViolateBO> temp = violates
                    .stream()
                    .filter(dto -> String.valueOf(conf.getCityCode()).equals(dto.getCity()))
                    .filter(dto -> dto.getRentTime().isAfter(FESTIVAL_END_TIME) || dto.getRevertTime().isBefore(FESTIVAL_START_TIME))
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(temp)){
                violateList.addAll(temp);
            }
        }
        return violateList.stream().map(this::convertTo).collect(Collectors.toList());
    }

    private OrderInfoForIllegal convertTo(ViolateBO fromBean) {
        OrderInfoForIllegal dto = new OrderInfoForIllegal();
        LocalDateTime rentTime = fromBean.getRentTime();
        LocalDateTime revertTime = fromBean.getRevertTime();
        LocalDateTime realRevertTime = fromBean.getRealRevertTime();
        Integer ownerType = fromBean.getOwnerType();
        if(rentTime != null) {
            dto.setQtime(DateUtils.formate(rentTime,DateUtils.DATE_DEFAUTE1));
        }else {
            dto.setQtime(null);
        }
        if(revertTime != null) {
            dto.setHtime(DateUtils.formate(revertTime,DateUtils.DATE_DEFAUTE1));
        }else {
            dto.setHtime(null);
        }
        if(realRevertTime != null) {
            dto.setActhtime(DateUtils.formate(realRevertTime,DateUtils.DATE_DEFAUTE1));
        }else {
            dto.setActhtime(null);
        }
        //查询车辆归属类型取值
        dto.setCartype(OwnerTypeEnum.getRemark(ownerType));
        dto.setOwnerType(null);
        dto.setCarNo(String.valueOf(fromBean.getCarNo()));
        dto.setCzphone(fromBean.getOwnerPhone());
        dto.setEnginenum(fromBean.getEngineNum());
        dto.setEngineSource(String.valueOf(fromBean.getEngineSource()));
        dto.setZkphone(fromBean.getRenterPhone());
        dto.setYccity(fromBean.getCityName());
        dto.setRenterNo(fromBean.getRenterNo());
        dto.setPlatenum(fromBean.getPlateNum());
        dto.setOwnerNo(fromBean.getOwnerNo());
        dto.setOrderno(fromBean.getOrderNo());
        dto.setMoreLicenseFlag(String.valueOf(fromBean.getMoreLicenseFlag()));
        dto.setLicenseExpire(DateUtils.localDateTimeToDate(fromBean.getLicenseExpire()));
        dto.setFrameno(fromBean.getFrameNo());
        //
        //0,普通订单 1,代步车订单 2,携程套餐订单 3,携程到店订单 4,同程套餐订单 5,安联代步车订单 6,普通套餐订单 7,VIP订单,8.线上长租订单
        //新增长租分类：
        //内部分类 1:普通,2:套餐,3:长租
        String orderType = "0";
        if (StringUtils.isNotBlank(fromBean.getCategory()) && "1".equals(fromBean.getCategory())) {
        	orderType = "0";
        } else if(StringUtils.isNotBlank(fromBean.getCategory()) && "3".equals(fromBean.getCategory())) {
        	orderType = "8";
        }
        dto.setOrderType(orderType);
        
        dto.setChannelType("0");
        dto.setOwnerOfflineOrderStatus(0);
        dto.setOfflineOrderType("0");
        dto.setSuccessCount(0);
        dto.setRenterCount(0);
        dto.setIllegalCount(0);
        SuccessOrderStaCount successOrderStaCountBo = getSuccessOrderStaCount(dto.getCarNo(), dto.getLicenseExpire());
        if(successOrderStaCountBo != null) {
            dto.setSuccessCount(successOrderStaCountBo.getSuccessCount());
            dto.setRenterCount(successOrderStaCountBo.getRenterCount());
            dto.setIllegalCount(successOrderStaCountBo.getIllegalCount());
        }
        return dto;
    }

    private SuccessOrderStaCount getSuccessOrderStaCount(String carNo, Date licenseExpire) {
        if(carNo == null || licenseExpire == null) {
            return null;
        }
        try {
            //格式化时间
            Date currentYear = DateUtils.firstDayOfYear();
            //比较行驶证到期日期
            if(licenseExpire.before(currentYear)) {
                return null;
            }
            //查询待统计的信息
            List<SuccessOrderDTO> successList = this.getSuccessOrderStatistics(carNo, currentYear, licenseExpire);
            if(successList.size() > 0) {
                List<String> orders = successList.stream().map(SuccessOrderDTO::getOrderNo).collect(Collectors.toList());
                SuccessOrderStaCount successOrderStaCountBo = new SuccessOrderStaCount();
                //成功订单数
                int successCount = successList.stream().map(SuccessOrderDTO::getOrderNo).collect(Collectors.toSet()).size();
                //成功订单的租客数（去重）
                int renterCount = successList.stream().map(SuccessOrderDTO::getRenterNo).collect(Collectors.toSet()).size();
                //成功订单的违章数（有效的并且扣分大于0）
                if(!CollectionUtils.isEmpty(orders)){
                    int illegalCount = renterOrderWzDetailService.queryIllegalCountByCarNoAndOrders(orders,carNo);
                    successOrderStaCountBo.setIllegalCount(illegalCount);
                }
                successOrderStaCountBo.setSuccessCount(successCount);
                successOrderStaCountBo.setRenterCount(renterCount);
                return successOrderStaCountBo;
            }else {
                return null;
            }
        } catch (Exception e) {
            logger.error("统计成功订单数，成功订单的租客数（去重），成功订单的违章数（有效的并且扣分大于0）报错：",e);
            return null;
        }
    }

    private List<SuccessOrderDTO> getSuccessOrderStatistics(String carNo, Date startTime, Date endTime) {
        List<String> orderNos = orderStatusService.queryOrderNoByStartTimeAndEndTime(startTime,endTime);
        if(CollectionUtils.isEmpty(orderNos)){
            return new ArrayList<>();
        }
        List<SuccessOrderDTO> orderNoList = orderService.queryOrderNoByOrderNos(orderNos);
        if(CollectionUtils.isEmpty(orderNoList)){
            return new ArrayList<>();
        }
        orderNos = orderNoList.stream().map(SuccessOrderDTO::getOrderNo).collect(Collectors.toList());
        orderNos = renterGoodsService.queryOrderNosByOrderNosAndCarNo(carNo,orderNos);
        if(CollectionUtils.isEmpty(orderNos)){
            return new ArrayList<>();
        }
        List<SuccessOrderDTO> result = new ArrayList<>();
        for (SuccessOrderDTO successOrderDTO : orderNoList) {
            if(orderNos.contains(successOrderDTO.getOrderNo())){
                result.add(successOrderDTO);
            }
        }
        return result;
    }

    private List<ViolateBO> violatePendingOrderByDay(Integer day) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询按规则配置日期内完成的订单，获取待查询违章的对象列表");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("2");
            reqVO.setDate(DateUtils.minDays(day));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            return orderList;
        } catch (Exception e) {
            logger.error("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 异常",e);
            Cat.logError("执行 查询按规则配置日期内完成的订单，获取待查询违章的对象列表 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithOwnerHasNotAgree() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询下单后15分钟的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("4");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询下单后15分钟的订单 异常",e);
            Cat.logError("执行 查询下单后15分钟的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithRenterAutoCancel() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询下单后1小时的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("5");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询下单后1小时的订单 异常",e);
            Cat.logError("执行 查询下单后1小时的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithUnDispatchOrder() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询待调度后4小时的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("7");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询待调度后4小时的订单 异常",e);
            Cat.logError("执行 查询待调度后4小时的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithUnPayViolationDeposit() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询订单开始仍未支付违章押金的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("6");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询订单开始仍未支付违章押金的订单 异常",e);
            Cat.logError("执行 查询订单开始仍未支付违章押金的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithRevertCar4Hours() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询还车4小时后的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("8");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询还车4小时后的订单 异常",e);
            Cat.logError("执行 查询还车4小时后的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithExpRevertCar12Hours() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询预计还车12小时后的订单");
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("9");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询预计还车12小时后的订单 异常",e);
            Cat.logError("执行 查询预计还车12小时后的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public List<String> queryOrderNosWithTransIllegalSettle() {
        //获取城市违章天数配置
        List<WzQueryDayConfEntity> configs = wzQueryDayConfService.queryAll();
        //转换数据
        Map<Integer, List<Integer>> map = wzQueryDayConfService.convertTranQueryDayByList(configs);
        return this.queryIllegalSettle(map);
    }

    private List<String> queryIllegalSettle(Map<Integer, List<Integer>> map) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询可结算的订单");
        if (CollectionUtils.isEmpty(map)) {
            return new ArrayList<>();
        }
        //用treeMap，默认按KEY的增值排序
        TreeMap<Integer, List<Integer>> treeMap = new TreeMap<>(map);
        //最小的天数
        Integer min = treeMap.firstKey();
        try {
            ViolateVO reqVO = new ViolateVO();
            reqVO.setPageNum(1);
            reqVO.setPageSize(10000);
            reqVO.setType("10");
            reqVO.setDate(DateUtils.minDays(min));
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }
            orderList = orderList.stream().filter(this::filterCanSettle).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }
            return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("执行 查询可结算的订单 异常",e);
            Cat.logError("执行 查询可结算的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    private boolean filterCanSettle(ViolateBO violate) {
        if(violate == null){
            return false;
        }
        //已支付 违章押金
        if(violate.getWzPayStatus() != null && violate.getWzPayStatus().equals(1)){
            return true;
        }
        //被暂扣
        return violate.getIsDetain() != null && violate.getIsDetain().equals(1);
    }

    public List<String> queryWaitingForCar() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询到达订单开始时,但仍是待取车状态的订单");
        try {
            ViolateVO req = new ViolateVO();
            req.setPageNum(1);
            req.setPageSize(10000);
            req.setType("11");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(req));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询到达订单开始时,但仍是待取车状态的订单 异常",e);
            Cat.logError("执行 查询到达订单开始时,但仍是待取车状态的订单 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }

    public static void  checkResponse(ResponseData responseObject){
        if(responseObject==null||!com.autoyol.commons.web.ErrorCode.SUCCESS.getCode().equalsIgnoreCase(responseObject.getResCode())){
            RemoteCallException remoteCallException = null;
            if(responseObject!=null){
                remoteCallException = new RemoteCallException(responseObject.getResCode(),responseObject.getResMsg(),responseObject.getData());
            }else{
                remoteCallException = new RemoteCallException(com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getCode(),
                        com.atzuche.order.commons.enums.ErrorCode.REMOTE_CALL_FAIL.getText());
            }
            throw remoteCallException;
        }
    }

    /**
     * 發送一小時未支付租车押金
     * @param orderNo
     */
    public void sendSmsData(String orderNo) {
        Map paramsMap = Maps.newConcurrentMap();
        paramsMap.put("indexUrl", h5Url);
        Map map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_RENTER.getValue(), ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_OWNER.getValue(), paramsMap);
        smsOrderBaseEventService.sendShortMessage(map);

    }

    public List<String> queryRenterOrderChangeApply() {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "查询车主收到修改申请15分钟后没有操作的租客子订单号列表");
        try {
            ViolateVO req = new ViolateVO();
            req.setPageNum(1);
            req.setPageSize(10000);
            req.setType("12");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"orderSearchService.violateProcessOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(req));
            ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(req);
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(orderResponseData));
            checkResponse(orderResponseData);
            List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
            t.setStatus(Transaction.SUCCESS);
            if(CollectionUtils.isEmpty(orderList)){
                return new ArrayList<>();
            }else{
                return  orderList.stream().map(ViolateBO::getRenterOrderNo).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("执行 查询车主收到修改申请15分钟后没有操作的租客子订单号列表 异常",e);
            Cat.logError("执行 查询车主收到修改申请15分钟后没有操作的租客子订单号列表 异常",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
