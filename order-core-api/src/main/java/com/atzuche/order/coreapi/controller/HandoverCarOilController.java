package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.BindingResultUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/handover")
public class HandoverCarOilController {
    
    private final static Logger logger = LoggerFactory.getLogger(HandoverCarOilController.class);
    

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

        logger.info("updateHandoverCarInfo param is {}",handoverCarReqVO);
        
       // BindingResultUtil.checkBindingResult(bindingResult);

        handoverCarInfoService.updateHandoverCarInfo(handoverCarReqVO);
        return ResponseData.success();

    }

    @GetMapping("/oil/list")
    public ResponseData<HandoverCarRespVO> updateHandoverCarInfo(@RequestParam("orderNo") String  orderNo)
    {
        logger.info("updateHandoverCarInfo param is {}",orderNo);
        HandoverCarRespVO handoverCarRespVO = handoverCarService.getHandoverCarInfoByOrderNo(orderNo);
        return ResponseData.success(handoverCarRespVO);


    }



}
