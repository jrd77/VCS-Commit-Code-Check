package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.car.CarOrderReqVO;
import com.atzuche.order.admin.vo.resp.car.RenterInfoRespVO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/console/renter")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看租客信息接口文档")
public class RenterInfoController {

    @Autowired
    private MemProxyService memProxyService;

    @AutoDocMethod(description = "获取租客信息接口响应信息", value = "获取租客信息接口响应信息", response = RenterInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<RenterInfoRespVO> getRenterInfo(@Valid CarOrderReqVO reqVo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        OrderRenterInfoDTO orderRenterInfoDTO =  memProxyService.getRenterInfoByMemNo(reqVo.getMemNo());
        RenterInfoRespVO respVO = new RenterInfoRespVO();
        BeanUtils.copyProperties(orderRenterInfoDTO,respVO);

        return ResponseData.success(respVO);
    }
}
