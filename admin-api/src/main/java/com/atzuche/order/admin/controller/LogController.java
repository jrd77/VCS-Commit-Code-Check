package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.entity.AdminOperateLogEntity;
import com.atzuche.order.admin.mapper.log.QueryVO;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.vo.req.log.LogQueryVO;
import com.atzuche.order.admin.vo.resp.LogListRespVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 2:51 下午
 **/
@RestController
public class LogController {
    @Autowired
    AdminLogService adminLogService;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @AutoDocVersion(version = "操作日志")
    @AutoDocGroup(group = "操作日志")
    @AutoDocMethod(description = "操作日志列表", value = "操作日志列表",response = LogListRespVO.class)
    @GetMapping("console/log/list")
    public ResponseData<LogListRespVO> list(@RequestParam("orderNo")String orderNo){
        LogListRespVO respVO = new LogListRespVO();
        respVO.setLogs(adminLogService.findByOrderNo(orderNo));
        return ResponseData.success(respVO);
    }

    @AutoDocVersion(version = "操作日志")
    @AutoDocGroup(group = "操作日志")
    @AutoDocMethod(description = "操作日志查询", value = "操作日志查询",response = LogListRespVO.class)
    @GetMapping("console/log/query")
    public ResponseData<LogListRespVO> query(LogQueryVO queryVO){
        LogListRespVO respVO = new LogListRespVO();
        QueryVO mapperQueryVO = new QueryVO();
        String orderNo = StringUtils.trimToNull(queryVO.getOrderNo());
        if(orderNo==null){
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        mapperQueryVO.setOrderNo(orderNo);
        String startTime = StringUtils.trimToNull(queryVO.getStartTime());
        String endTime = StringUtils.trimToNull(queryVO.getEndTime());

        if(startTime!=null&&endTime!=null){
            mapperQueryVO.setStartTime(LocalDateTime.parse(startTime,dateTimeFormatter));
            mapperQueryVO.setEndTime(LocalDateTime.parse(endTime,dateTimeFormatter));
        }
        mapperQueryVO.setContent(queryVO.getContent());
        mapperQueryVO.setOperator(queryVO.getOperatorName());
        mapperQueryVO.setOpType(queryVO.getOpTypeCode());

        respVO.setLogs(adminLogService.findByQueryVO(mapperQueryVO));
        return ResponseData.success(respVO);
    }


}
