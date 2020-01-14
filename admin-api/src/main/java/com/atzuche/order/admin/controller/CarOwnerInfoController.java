package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.car.CarMemNoReqVO;
import com.atzuche.order.admin.vo.resp.car.CarOwnerInfoRespVO;
import com.atzuche.order.admin.vo.resp.car.RenterInfoRespVO;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.commons.entity.dto.OrderFlowListResponseDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.open.service.FeignOrderFlowService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/console/carOwner")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看车主信息接口文档")
public class CarOwnerInfoController {
    private final static Logger logger = LoggerFactory.getLogger(CarOwnerInfoController.class);
    

    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private CarProxyService carProxyService;

    @Autowired
    private FeignOrderFlowService feignOrderDetailService;

    @AutoDocMethod(description = "查看车主信息接口信息", value = "查看车主信息接口信息", response = CarOwnerInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<?> getCarOwnerInfo(@RequestParam("orderNo")String orderNo,@RequestParam("memNo")String memNo) {
        logger.info("reqVo is order={},memNo={}",orderNo,memNo);
        OrderRenterInfoDTO orderRenterInfoDTO =  memProxyService.getRenterInfoByMemNo(memNo);
        CarOwnerInfoRespVO respVO = new CarOwnerInfoRespVO();
        respVO.setRealName(orderRenterInfoDTO.getRealName());
        respVO.setCity(orderRenterInfoDTO.getCity());
        respVO.setDriLicRecordNo(orderRenterInfoDTO.getDriLicRecordNo());
        respVO.setEmail(orderRenterInfoDTO.getEmail());
        respVO.setGender(orderRenterInfoDTO.getGender());
        respVO.setIdNo(orderRenterInfoDTO.getIdNo());
        respVO.setOwnerNo(orderRenterInfoDTO.getMemNo());
        respVO.setProvince(orderRenterInfoDTO.getProvince());

        OrderFlowRequestDTO reqDTO = new OrderFlowRequestDTO();
        reqDTO.setOrderNo("86943331100299");
        ResponseData<OrderFlowListResponseDTO> result = feignOrderDetailService.selectOrderFlowList(reqDTO);

        logger.info("feign is {}",result.getResMsg());

        //TODO:车主等级
        return ResponseData.success(respVO);
    }

}
