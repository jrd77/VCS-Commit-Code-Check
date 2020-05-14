package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.coreapi.service.AdditionalDriverService;
import com.atzuche.order.renterorder.service.RenterAdditionalDriverService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.util.List;

@Slf4j
@RestController
public class AdditionalDriverController {

    @Autowired
    private AdditionalDriverService additionalDriverService;
    @Autowired
    private RenterAdditionalDriverService renterAdditionalDriverService;

    /*
     * @Author ZhangBin
     * @Date 2020/4/29 18:01
     * @Description: 添加附加驾驶人和附加驾驶人险
     *
     **/
    @PostMapping("/additionalDriver/insertAdditionalDriver")
    public ResponseData<?> insertAdditionalDriver(@Valid @RequestBody AdditionalDriverInsuranceIdsReqVO renterCostReqVO, BindingResult bindingResult){
        log.info("添加附加驾驶人和附加驾驶人险 renterCostReqVO={}", JSON.toJSONString(renterCostReqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        additionalDriverService.insertAdditionalDriver(renterCostReqVO);
        return ResponseData.success();
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/29 18:01
     * @Description: 查询添加的附加驾驶人
     *
     **/
    @PostMapping("/additionalDriver/queryAdditionalDriver")
    public ResponseData<?> queryAdditionalDriver(@RequestParam("renterOrderNo") String renterOrderNo){
        log.info("查询附加驾驶人和附加驾驶人险 renterOrderNo={}", renterOrderNo);
        List<String> driverIds = renterAdditionalDriverService.listDriverIdByRenterOrderNo(renterOrderNo);
        return ResponseData.success(driverIds);
    }

}
