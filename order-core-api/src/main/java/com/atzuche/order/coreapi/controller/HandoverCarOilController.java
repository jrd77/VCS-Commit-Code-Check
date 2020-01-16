package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/handover")
public class HandoverCarOilController {

    @Autowired
    HandoverCarInfoService handoverCarInfoService;

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
}
