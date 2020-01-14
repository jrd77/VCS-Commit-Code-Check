package com.atzuche.order.admin.controller.car;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.service.CarDepositReturnDetailService;
import com.atzuche.order.admin.vo.req.car.CarDepositReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@AutoDocVersion(version = "车辆押金信息")
public class CarDepositReturnDetailController {
    @Autowired
    private CarDepositReturnDetailService carDepositReturnDetailService;

    @AutoDocMethod(description = "【liujun】车辆押金信息", value = "车辆押金信息", response = CarDepositRespVo.class)
    @PostMapping(value = "/console/deposit/getCarDepositReturnDetail")
    public ResponseData<CarDepositRespVo> getCarDepositReturnDetail(@Valid @RequestBody CarDepositReqVO reqVo, BindingResult bindingResult) {
        log.info("车辆押金信息-reqVo={}", JSON.toJSONString(reqVo));
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<CarDepositRespVo>  respVoResponseData = carDepositReturnDetailService.getCarDepositReturnDetail(reqVo);
        return respVoResponseData;
    }

    /*@AutoDocMethod(description = "【liujun】车辆押金暂扣处理", value = "车辆押金暂扣处理", response = CarDepositOtherRespVO.class)
    @GetMapping(value = "/console/deposit/return/detail/otherInfo")
    public ResponseData <?> getCarDepositReturnDetailOtherInfo(@Valid CarDepositOtherReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】车辆押金返还处理列表", value = "车辆押金返还处理列表", response = CarDepositReturnDetailResVO.class)
    @GetMapping(value = "/console/deposit/return/detail/list")
    public ResponseData <?> getCarDepositReturnDetail(@Valid CarDepositReturnDetailListReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "【liujun】保存车辆押金返还处理", value = "保存车辆押金返还处理", response = ResponseData.class)
    @GetMapping(value = "/console/deposit/return/detail/save")
    public ResponseData <?> saveCarDepositReturnDetail(@Valid CarDepositReturnDetailResVO reqVo, BindingResult bindingResult) {

        return null;
    }*/
}
