package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.car.CarOrderReqVO;
import com.atzuche.order.admin.vo.resp.car.RenterInfoRespVO;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
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

@RequestMapping("/console/renter")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看租客信息接口文档")
public class RenterInfoController {
    
    private final static Logger logger = LoggerFactory.getLogger(RenterInfoController.class);
    

    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private FeignOrderDetailService feignOrderDetailService;

    @AutoDocMethod(description = "获取租客信息接口响应信息", value = "获取租客信息接口响应信息", response = RenterInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<RenterInfoRespVO> getRenterInfo(@RequestParam(value = "orderNo",required = true) String orderNo) {
        logger.info("reqVo is {}",orderNo);

        OrderDetailReqDTO reqDTO = new OrderDetailReqDTO();
        reqDTO.setOrderNo(orderNo);

        ResponseData<OrderDetailRespDTO> respDTOResponseData =feignOrderDetailService.getOrderDetail(reqDTO);
        if(respDTOResponseData==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(respDTOResponseData.getResCode())){
            throw new RenterNotFoundException(orderNo);
        }

        OrderDetailRespDTO detailRespDTO = respDTOResponseData.getData();
        String memNo = detailRespDTO.getRenterMember().getMemNo();


        OrderRenterInfoDTO orderRenterInfoDTO =  memProxyService.getRenterInfoByMemNo(memNo);
        RenterInfoRespVO respVO = new RenterInfoRespVO();
        BeanUtils.copyProperties(orderRenterInfoDTO,respVO);
        respVO.setReqAddr(detailRespDTO.getOrderSourceStat().getReqAddr());


        return ResponseData.success(respVO);
    }

    public static class RenterNotFoundException extends OrderException {

        public RenterNotFoundException(String mesg) {
            super(com.atzuche.order.commons.enums.ErrorCode.ORDER_QUERY_FAIL.getCode(), com.atzuche.order.commons.enums.ErrorCode.ORDER_QUERY_FAIL.getText()+":"+mesg);
        }
    }
}
