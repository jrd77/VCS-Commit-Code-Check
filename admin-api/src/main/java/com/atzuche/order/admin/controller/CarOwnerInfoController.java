package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.car.CarMemNoReqVO;
import com.atzuche.order.admin.vo.resp.car.CarOwnerInfoRespVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/console/carOwner")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看车主信息接口文档")
public class CarOwnerInfoController {

    /**
     * 老后台参考:
     * com.autoyolConsole.controller.TransController.detail(String, String, HttpServletRequest)
     * /autoyolConsole/src/main/webapp/WEB-INF/view/trans/detail.jsp
     */
    @AutoDocMethod(description = "查看车主信息接口信息", value = "查看车主信息接口信息", response = CarOwnerInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<?> getCarOwnerInfo(@Valid CarMemNoReqVO reqVo, BindingResult bindingResult) {
        return null;
    }

}
