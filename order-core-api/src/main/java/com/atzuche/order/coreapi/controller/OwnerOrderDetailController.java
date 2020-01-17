package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.coreapi.service.OwnerOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OwnerOrderDetailController {
    @Autowired
    private OwnerOrderDetailService ownerOrderDetailService;

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 租金明细
     *
     **/
    @GetMapping("/owner/ownerRentDetail")
    public ResponseData<OwnerRentDetailDTO> ownerRentDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主订单号不能为空");
            return responseData;
        }
        ResponseData<OwnerRentDetailDTO> responseData = ownerOrderDetailService.ownerRentDetail(orderNo,ownerOrderNo);
        return responseData;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 车主租客调价明细
     * 
     **/
    @GetMapping("/owner/renterOwnerPrice")
    public ResponseData<RenterOwnerPriceDTO> renterOwnerPrice(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主订单号不能为空");
            return responseData;
        }
        ResponseData<RenterOwnerPriceDTO> responseData = ownerOrderDetailService.renterOwnerPrice(orderNo,ownerOrderNo);
        return responseData;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 违约罚金明细
     *
     **/
    @GetMapping("/owner/fienAmtDetail")
    public ResponseData<FienAmtDetailDTO> fienAmtDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主订单号不能为空");
            return responseData;
        }
        ResponseData<FienAmtDetailDTO> responseData = ownerOrderDetailService.fienAmtDetail(orderNo,ownerOrderNo);
        return responseData;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 服务费明细
     *
     **/
    @GetMapping("/owner/serviceDetail")
    public ResponseData<ServiceDetailDTO> serviceDetail(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主订单号不能为空");
            return responseData;
        }
        ResponseData<ServiceDetailDTO> responseData = ownerOrderDetailService.serviceDetail(orderNo,ownerOrderNo);
        return responseData;
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
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主订单号不能为空");
            return responseData;
        }
        ResponseData<PlatformToOwnerDTO> responseData = ownerOrderDetailService.platformToOwner(orderNo,ownerOrderNo);
        return responseData;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 21:34
     * @Description: 平台给车主的补贴
     *
     **/
    @GetMapping("/owner/platformToOwnerSubsidy")
    public ResponseData<PlatformToOwnerSubsidyDTO> platformToOwnerSubsidy(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主订单号不能为空");
            return responseData;
        }
        ResponseData<PlatformToOwnerSubsidyDTO> responseData = ownerOrderDetailService.platformToOwnerSubsidy(orderNo,ownerOrderNo);
        return responseData;
    }

    @PostMapping("/owner/updateFien")
    public ResponseData<?> updateFien(){
        return null;
    }
}
