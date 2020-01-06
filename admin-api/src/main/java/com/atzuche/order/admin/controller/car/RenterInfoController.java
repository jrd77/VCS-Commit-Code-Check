package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.vo.req.car.CarOrderReqVo;
import com.atzuche.order.admin.vo.resp.car.RenterInfoRespVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/console/renter")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看租客信息接口文档")
public class RenterInfoController {
    /**
     * 老后台参考:
     * com.autoyolConsole.controller.TransController.detail(String, String, HttpServletRequest)
     * /autoyolConsole/src/main/webapp/WEB-INF/view/trans/detail.jsp
     */
    @AutoDocMethod(description = "获取租客信息接口响应信息", value = "获取租客信息接口响应信息", response = RenterInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<?> getManagedcarInfo(@Valid CarOrderReqVo reqVo, BindingResult bindingResult) {
        return null;
    }
}
