package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.StringUtil;
import com.atzuche.order.commons.vo.req.handover.rep.HandoverCarRespVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/handover")
public class HandoverCarOilController {

    @Autowired
    HandoverCarInfoService handoverCarInfoService;
    @Autowired
    HandoverCarService handoverCarService;

    /**
     * 更新油耗里程
     * @param handoverCarReqVO
     * @return
     */
    @PostMapping("/oil/setOil")
    public ResponseData<?> updateHandoverCarInfo(@RequestBody HandoverCarInfoReqVO handoverCarReqVO) {

        if(Objects.isNull(handoverCarReqVO))
        {
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "需要得参数不符合");
        }

        try {
            handoverCarInfoService.updateHandoverCarInfo(handoverCarReqVO);
            return ResponseData.success();
        } catch (Exception e) {
            log.error("取还车更新接口出现异常", e);
            Cat.logError("取还车更新接口出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "取还车更新接口出现错误");
        }
    }

    @GetMapping("/oil/list")
    public ResponseData<HandoverCarRespVO> updateHandoverCarInfo(@RequestParam("orderNo") String  orderNo)
    {
        if(StringUtils.isBlank(orderNo))
        {
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "需要得参数不符合");
        }
        try {
            HandoverCarRespVO handoverCarRespVO = handoverCarService.getHandoverCarInfoByOrderNo(orderNo);
            return ResponseData.success(handoverCarRespVO);
        } catch (Exception e) {
            log.error("取还车更新接口出现异常", e);
            Cat.logError("取还车更新接口出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "取还车更新接口出现错误");
        }

    }



}
