package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.orderDetailDto.ConsoleOwnerOrderFineDeatailDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.coreapi.service.OwnerOrderDetailService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class OwnerOrderDetailController {
    @Autowired
    private OwnerOrderDetailService ownerOrderDetailService;

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 租金明细
     *
     **/
    @GetMapping("/owner/ownerRentDetail")
    public ResponseData<OwnerRentDetailDTO> ownerRentDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }
        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            throw new InputErrorException("车主订单号不能为空");
        }
        OwnerRentDetailDTO ownerRentDetailDTO = ownerOrderDetailService.ownerRentDetail(orderNo,ownerOrderNo);
        return ResponseData.success(ownerRentDetailDTO);
    }

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 车主租客调价明细
     * 
     **/
    @GetMapping("/owner/renterOwnerPrice")
    public ResponseData<RenterOwnerPriceDTO> renterOwnerPrice(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            throw new InputErrorException("车主订单号不能为空");
        }
        RenterOwnerPriceDTO renterOwnerPriceDTO = ownerOrderDetailService.renterOwnerPrice(orderNo,ownerOrderNo);
        return ResponseData.success(renterOwnerPriceDTO);
    }

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 违约罚金明细
     *
     **/
    @GetMapping("/owner/fienAmtDetail")
    public ResponseData<FienAmtDetailDTO> fienAmtDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            throw new InputErrorException("车主订单号不能为空");
        }
        FienAmtDetailDTO fienAmtDetailDTO = ownerOrderDetailService.fienAmtDetail(orderNo,ownerOrderNo);
        return ResponseData.success(fienAmtDetailDTO);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 服务费明细
     *
     **/
    @GetMapping("/owner/serviceDetail")
    public ResponseData<ServiceDetailDTO> serviceDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
    	log.info("--------------- serviceDetail orderNo={},ownerOrderNo={}",orderNo,ownerOrderNo);
    	if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            throw new InputErrorException("车主订单号不能为空");
        }
        
        ServiceDetailDTO serviceDetailDTO = ownerOrderDetailService.serviceDetail(orderNo,ownerOrderNo);
        return ResponseData.success(serviceDetailDTO);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:37
     * @Description: 车主需支付给平台的费用
     * 
     **/
    @GetMapping("/owner/platformToOwner")
    public ResponseData<PlatformToOwnerDTO> platformToOwner(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            throw new InputErrorException("车主订单号不能为空");
        }
        PlatformToOwnerDTO platformToOwnerDTO = ownerOrderDetailService.platformToOwner(orderNo,ownerOrderNo);
        return ResponseData.success(platformToOwnerDTO);
    }

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:34
     * @Description: 平台给车主的补贴
     *
     **/
    @GetMapping("/owner/platformToOwnerSubsidy")
    public ResponseData<PlatformToOwnerSubsidyDTO> platformToOwnerSubsidy(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo,@RequestParam("memNo") String memNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            throw new InputErrorException("车主订单号不能为空");
        }
        PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = ownerOrderDetailService.platformToOwnerSubsidy(orderNo,ownerOrderNo,memNo);
        return ResponseData.success(platformToOwnerSubsidyDTO);
    }

    @GetMapping("/owner/fienAmtDetailList")
    public ResponseData<List<ConsoleOwnerOrderFineDeatailDTO>> fienAmtDetailList(@RequestParam("orderNo") String orderNo, @RequestParam("ownerMemNo") String ownerMemNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            throw new InputErrorException("主订单号不能为空");
        }

        if(ownerMemNo == null || ownerMemNo.trim().length()<=0){
            throw new InputErrorException("车主会员号不能为空");
        }
        List<ConsoleOwnerOrderFineDeatailDTO> list = ownerOrderDetailService.fienAmtDetailList(orderNo,ownerMemNo);
        return ResponseData.success(list);
    }


    @PostMapping("/owner/updateFien")
    public ResponseData<?> updateFineAmt(@RequestBody FienAmtUpdateReqDTO fienAmtUpdateReqDTO){  //@Valid   , BindingResult bindingResult
//        BindingResultUtil.checkBindingResult(bindingResult);
        ownerOrderDetailService.updateFien(fienAmtUpdateReqDTO);
        return ResponseData.success();
    }
}