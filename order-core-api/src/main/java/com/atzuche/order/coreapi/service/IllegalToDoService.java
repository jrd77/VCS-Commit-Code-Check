package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.entity.wz.RenterOrderWzDetailLogEntity;
import com.atzuche.order.commons.enums.WzLogOperateTypeEnums;
import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzQueryRecordEntity;
import com.atzuche.order.renterwz.service.*;
import com.atzuche.order.renterwz.vo.HttpResult;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.atzuche.order.renterwz.vo.Violation;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IllegalToDoService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Slf4j
@Service
public class IllegalToDoService {

    private Logger logger = LoggerFactory.getLogger(IllegalToDoService.class);

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RenterOrderWzRecordService renterOrderWzRecordService;

    @Resource
    private RenterOrderWzDetailService renterOrderWzDetailService;

    @Resource
    private RenterOrderWzStatusService renterOrderWzStatusService;

    @Resource
    private RenterOrderWzQueryRecordService renterOrderWzQueryRecordService;

    @Resource
    private RenterOrderWzEverydayTodoService renterOrderWzEverydayTodoService;

    @Resource
    private RenterOrderWzFinishedTodoService renterOrderWzFinishedTodoService;
    @Autowired
    RenterOrderWzDetailLogService renterOrderWzDetailLogService;

    private static final String ORDER_CENTER_EXCHANGE = "auto-order-center-wz";
    private static final String ORDER_CENTER_WZ_QUERY_INFO_ROUTING_KEY = "order.center.wz.query.info.feedback";
    private static final String EVERY_DAY = "everyday";
    private static final String FINISHED = "finished";

    private static final String ORDER_CENTER_WZ_WITHHOLD_EXCHANGE = "auto-order-center-wz";
    private static final String ORDER_CENTER_WZ_WITHHOLD_ROUTING_KEY = "order.center.wz.with.hold.feedback";


    /**
     * 违章处理状态：处理中租客处理
     */
    private static final int WZ_DISPOSE_STATUS = 25;
    private static final Integer WZ_DISPOSE_STATUS_UN_PROCESS = 25;

    public void queryNewDetail(List<IllegalToDO> list, boolean sendSms, String type) {
        // 删除所有不是今天的查询结果数据
        logger.info("delete violation record not today:{}", renterOrderWzRecordService.deleteNotToday());
        if (list != null && list.size() > 0) {
            for (IllegalToDO todo : list) {
                try {
                    todo.setSendSms(sendSms);
                    todo.setType(type);
                    rabbitTemplate.convertAndSend(ORDER_CENTER_EXCHANGE, ORDER_CENTER_WZ_QUERY_INFO_ROUTING_KEY, GsonUtils.toJson(todo));
                } catch (Exception e) {
                    logger.error("违章查询接口异常 e:", e);
                }
            }
        }
    }

    public void processCheLeHangInfoViolations(IllegalToDO dto){
        String orderNo = dto.getOrderNo();
        Date rentTime = dto.getRentTime();
        Date revertTime = dto.getRevertTime();
        String carNumber = dto.getPlateNum();
        Boolean sendSms = dto.getSendSms();
        String type = dto.getType();
        String resStr = dto.getResStr();
        List<Violation> violations = this.processInfo(rentTime, revertTime,carNumber, null, orderNo,resStr);
        violations = violations.stream().distinct().collect(Collectors.toList());
        int status;
        if (!CollectionUtils.isEmpty(violations)) {
            status = 4;
            List<RenterOrderWzDetailEntity> details = new ArrayList<>();
            // 遍历违章记录插入数据库中
            for (Violation violation : violations) {
                RenterOrderWzDetailEntity detail = new RenterOrderWzDetailEntity();
                detail.setCode(violation.getCode());
                detail.setOrderNo(orderNo);
                detail.setCarPlateNum(carNumber);
                detail.setIllegalAddr(violation.getAddress());
                detail.setIllegalAmt(violation.getFine());
                detail.setIllegalDeduct(violation.getPoint() + "");
                detail.setIllegalReason(violation.getReason());
                detail.setIllegalTime(violation.getTime());
                detail.setOrderFlag(1);
                //如果有违章，并且需要发送短信
                if (sendSms) {
                    //设置待发状态，后续短信发送的定时任务将对待发短信进行查询
                    detail.setIsSms(0);
                }

                //去重处理
                String authName = "系统";
                RenterOrderWzDetailLogEntity entity = new RenterOrderWzDetailLogEntity();
                try{
                    String IllegalTime = DateUtils.formate(detail.getIllegalTime(), DateUtils.DATE_DEFAUTE1);
                    List<RenterOrderWzDetailEntity> renterOrderWzDetailEntityList = renterOrderWzDetailService.getRepeatData(detail.getOrderNo(),IllegalTime);
                    if(renterOrderWzDetailEntityList != null && renterOrderWzDetailEntityList.size()>=1) {
                        RenterOrderWzDetailEntity renterOrderWzDetailEntity = renterOrderWzDetailEntityList.get(0);
                        //记录日志
                        String wzContent = RenterOrderWzDetailLogEntity.getWzContent(DateUtils.formate(detail.getIllegalTime(), DateUtils.DATE_DEFAUTE1),
                                detail.getIllegalAddr(),
                                detail.getIllegalReason(),
                                detail.getIllegalFine(),
                                detail.getIllegalDeduct(),
                                detail.getIllegalStatus());
                        entity.setOrderNo(detail.getOrderNo());
                        entity.setWzDetailId(renterOrderWzDetailEntity.getId());
                        entity.setOperateType(WzLogOperateTypeEnums.SYSTEM_DISTINCT.getCode());
                        entity.setContent(wzContent);
                        entity.setCreateOp(authName);
                        entity.setUpdateOp(authName);
                        log.info("凹凸自动同步导入违章信息记录日志entity={}", JSON.toJSONString(entity));
                        int insert = renterOrderWzDetailLogService.insert(entity);
                        log.info("凹凸自动同步导入违章信息记录日志insert={},entity={}", insert, JSON.toJSONString(entity));
                        continue;
                    }
                }catch (Exception e){
                    log.error("任云同步导入数据记录日志失败entity={},e",JSON.toJSONString(entity),e);
                }

                //根据时间、地点、订单号做比对
                int count = renterOrderWzDetailService.countIllegalDetailByOrderNo(orderNo,violation.getTime(),violation.getAddress(),violation.getCode(),carNumber);
                if (count == 0) {
                    details.add(detail);
                }
            }
            if (!CollectionUtils.isEmpty(details)) {
                details = details.stream().distinct().collect(Collectors.toList());
                List<RenterOrderWzDetailEntity> temps = new ArrayList<>();
                temps.addAll(details);
                renterOrderWzDetailService.batchInsert(temps);
            }

            List<RenterOrderWzDetailEntity> temps = renterOrderWzDetailService.findDetailByOrderNo(orderNo,carNumber);
            int decuct = 0;
            for (RenterOrderWzDetailEntity illegalDetail : temps) {
                Integer point = Integer.valueOf(illegalDetail.getIllegalDeduct());
                if (!StringUtils.isEmpty(point)) {
                    decuct += point;
                }
            }
            if (decuct >= 6) {
                rabbitTemplate.convertAndSend(ORDER_CENTER_WZ_WITHHOLD_EXCHANGE, ORDER_CENTER_WZ_WITHHOLD_ROUTING_KEY,orderNo );
            }
            //修改订单违章处理状态,5-未处理
            Integer nowWzDisposeStatus = renterOrderWzStatusService.getTransWzDisposeStatusByOrderNo(orderNo,carNumber);
            if(nowWzDisposeStatus == null || WZ_DISPOSE_STATUS_UN_PROCESS.equals(nowWzDisposeStatus)){
                renterOrderWzStatusService.updateTransWzDisposeStatus(orderNo, carNumber, WZ_DISPOSE_STATUS);
                logger.info("修改订单违章处理状态:orderno:[{}], carNumber:[{}] ,status:[{}]", orderNo,carNumber, WZ_DISPOSE_STATUS);
            }
        } else {
            status = 3;
        }
        // 事务3：回写renter_order_wz_status表
        renterOrderWzStatusService.updateTransIllegalQuery(status,orderNo,carNumber);
        if (dto.getId() != null && dto.getId() != 0) {
            if (EVERY_DAY.equals(type)) {
                renterOrderWzEverydayTodoService.updateStatus(dto.getId());
            } else if (FINISHED.equals(type)) {
                renterOrderWzFinishedTodoService.updateStatus(dto.getId());
            }
        }
    }

    private List<Violation> processInfo(Date rentTime, Date revertTime, String carNumber, String cityName, String orderNo, String resStr) {
        RenterOrderWzQueryRecordEntity entity = new RenterOrderWzQueryRecordEntity();
        List<Violation> violations = new ArrayList<>();
        // 初始化车辆无违章信息
        entity.setIllegalFlag(0);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(resStr)) {
            HttpResult result = JSONObject.parseObject(resStr, HttpResult.class);
            if (result.getResCode().equals(ErrorCode.SUCCESS.getCode())) {
                if (result.getData() != null) {
                    List list = (List) result.getData();
                    for (Object object : list) {
                        Violation violation = JSONObject.parseObject(object.toString(), Violation.class);
                        if(violation != null && violation.getTime()!= null && violation.getTime()!= null) {
	                        if (violation.getTime().before(revertTime) && violation.getTime().after(rentTime)) {
	                            // 车辆有违章信息
	                            entity.setIllegalFlag(1);
	                            violations.add(violation);
	                        }
                        }
                    }
                }
            } else {
                entity.setErrorMsg(result.getResMsg());
                entity.setReturnCode(result.getResCode());
            }
        }
        entity.setResultJson(resStr);
        entity.setOrderNo(orderNo);
        entity.setCarPlateNum(carNumber);
        entity.setCity(cityName);
        // 保存请求记录
        renterOrderWzQueryRecordService.insert(entity);
        return violations;
    }
}
