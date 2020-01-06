package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.vo.req.car.*;
import com.atzuche.order.admin.vo.resp.car.CarDepositOtherRespVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositRespVo;
import com.atzuche.order.admin.vo.resp.car.CarDepositReturnDetailResVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AutoDocVersion(version = "车辆押金信息")
public class CarDepositReturnDetailController {

    @AutoDocMethod(description = "车辆押金信息", value = "车辆押金信息", response = CarDepositRespVo.class)
    @GetMapping(value = "/car/deposit/return/detail/baseInfo")
    public ResponseData <?> getCarDepositReturnDetail(@Valid CarDepositReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "车辆押金暂扣处理", value = "车辆押金暂扣处理", response = CarDepositOtherRespVO.class)
    @GetMapping(value = "/car/deposit/return/detail/otherInfo")
    public ResponseData <?> getCarDepositReturnDetailOtherInfo(@Valid CarDepositOtherReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "车辆押金返还处理列表", value = "车辆押金返还处理列表", response = CarDepositReturnDetailResVO.class)
    @GetMapping(value = "/car/deposit/return/detail/list")
    public ResponseData <?> getCarDepositReturnDetail(@Valid CarDepositReturnDetailListReqVO reqVo, BindingResult bindingResult) {

        return null;
    }

    @AutoDocMethod(description = "保存车辆押金返还处理", value = "保存车辆押金返还处理", response = ResponseData.class)
    @GetMapping(value = "/save/car/deposit/return/detail/baseInfo")
    public ResponseData <?> saveCarDepositReturnDetail(@Valid CarDepositReturnDetailResVO reqVo, BindingResult bindingResult) {

        return null;
    }
}
