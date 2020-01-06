package com.atzuche.order.admin.controller.car;

import com.atzuche.order.admin.vo.req.car.CarBaseReqVO;
import com.atzuche.order.admin.vo.req.car.ManagedCarUpdateReqVO;
import com.atzuche.order.admin.vo.resp.car.ManagedCarRespVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/console/managedcar")
@RestController
@AutoDocVersion(version = "订单列表 - 查看托管车出入库信息接口文档")
public class ManagedCarController{

    /**
     * 老后台参考:
     * com.autoyolConsole.controller.TransController.detail(String, String, HttpServletRequest)
     * /autoyolConsole/src/main/webapp/WEB-INF/view/trans/detail.jsp
     */
    @AutoDocMethod(description = "获取托管车出入库初始化响应信息", value = "获取托管车出入库初始化响应信息", response = ManagedCarRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData <?> getManagedcarInfo(@Valid CarBaseReqVO reqVo, BindingResult bindingResult) {
        return null;
    }

    @AutoDocMethod(description = "托管车出入库信息保存", value = "托管车出入库信息保存")
    @PostMapping(value = "/saveManagedcarInfo")
    public ResponseData <?> saveManagedcarInfo(@Valid @RequestBody ManagedCarUpdateReqVO reqVo, BindingResult bindingResult) {
        return null;
    }

}
