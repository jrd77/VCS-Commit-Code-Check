package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.vo.req.car.CarOrderReqVO;
import com.atzuche.order.admin.vo.resp.car.ChangeCarRecordRespVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/console/changeCar")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看更换车辆记录接口文档")
public class ChangeCarRecordController {

    /**
     * 老后台参考:
     * com.autoyolConsole.controller.TransController.detail(String, String, HttpServletRequest)
     * /autoyolConsole/src/main/webapp/WEB-INF/view/trans/detail.jsp
     */
    @AutoDocMethod(description = "查看更换车辆记录信息", value = "查看更换车辆记录信息", response = ChangeCarRecordRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<?> getChangeCarInfo(@Valid CarOrderReqVO reqVo, BindingResult bindingResult) {
        return null;
    }

}
