package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.admin.vo.resp.car.RenterInfoRespVO;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterAdditionalDriverDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterMemberDTO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.mem.dto.OrderRenterInfoDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.autoyol.member.detail.vo.res.CommUseDriverInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequestMapping("/console/renter")
@RestController
@AutoDocVersion(version = "订单详细信息 - 查看租客信息接口文档")
public class AdminRenterInfoController {
    
    private final static Logger logger = LoggerFactory.getLogger(AdminRenterInfoController.class);
    
    @Autowired
    private MemProxyService memProxyService;

    @Autowired
    private RemoteFeignService remoteFeignService;

    @AutoDocMethod(description = "获取租客信息接口响应信息", value = "获取租客信息接口响应信息", response = RenterInfoRespVO.class)
    @GetMapping(value = "/info")
    public ResponseData<RenterInfoRespVO> getRenterInfo(@RequestParam(value = "orderNo",required = true) String orderNo) {
        logger.info("reqVo is {}",orderNo);

        //OrderDetailReqDTO reqDTO = new OrderDetailReqDTO();
        //reqDTO.setOrderNo(orderNo);

        //ResponseData<OrderDetailRespDTO> respDTOResponseData =feignOrderDetailService.getOrderDetail(reqDTO);
        ResponseData<OrderDetailRespDTO> respDTOResponseData =remoteFeignService.getOrderdetailFromRemote(orderNo);
        if(respDTOResponseData==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(respDTOResponseData.getResCode())){
            throw new RenterNotFoundException(orderNo);
        }

        OrderDetailRespDTO detailRespDTO = respDTOResponseData.getData();
        String memNo = detailRespDTO.getRenterMember().getMemNo();
        RenterMemberDTO renterMemberDTO = detailRespDTO.getRenterMember();
        List<RenterAdditionalDriverDTO> renterAdditionalDriverDTOS =  detailRespDTO.getRenterAdditionalDriverList();


        OrderRenterInfoDTO orderRenterInfoDTO =  memProxyService.getRenterInfoByMemNo(memNo);
        RenterInfoRespVO respVO = new RenterInfoRespVO();
        BeanUtils.copyProperties(orderRenterInfoDTO,respVO);
        BeanUtils.copyProperties(renterMemberDTO,respVO);
        respVO.setReqAddr(detailRespDTO.getOrderSourceStat().getReqAddr());
        //List<RenterAdditionalDriverEntity> renterAdditionalDriverEntitys = renterAdditionalDriverService.listDriversByRenterOrderNo(detailRespDTO.getRenterOrder().getRenterOrderNo());

        //respVO.setAdditionalDrivers(convert(renterAdditionalDriverEntitys));
        respVO.setAdditionalDrivers(convertRenterAdditionalDriverDTO(renterAdditionalDriverDTOS));
        return ResponseData.success(respVO);
    }

    public static List<CommUseDriverInfo> convertRenterAdditionalDriverDTO(List<RenterAdditionalDriverDTO> dtos){
        List<CommUseDriverInfo> commUseDriverInfos = new ArrayList<>();
        for(RenterAdditionalDriverDTO dto:dtos){
            CommUseDriverInfo commUseDriverInfo = new CommUseDriverInfo();
            BeanUtils.copyProperties(dto,commUseDriverInfo);
            if(Objects.nonNull(dto.getPhone())){
                commUseDriverInfo.setMobile(Long.valueOf(dto.getPhone()));
            }
            commUseDriverInfos.add(commUseDriverInfo);
        }
        return commUseDriverInfos;
    }

    public static class RenterNotFoundException extends OrderException {

        public RenterNotFoundException(String mesg) {
            super(com.atzuche.order.commons.enums.ErrorCode.ORDER_QUERY_FAIL.getCode(), com.atzuche.order.commons.enums.ErrorCode.ORDER_QUERY_FAIL.getText()+":"+mesg);
        }
    }
}
