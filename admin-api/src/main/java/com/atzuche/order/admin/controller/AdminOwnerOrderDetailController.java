package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.OwnerOrderDetailService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.util.CompareBeanUtils;
import com.atzuche.order.admin.vo.req.FienAmtUpdateReqVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.commons.enums.cashcode.FineTypeCashCodeEnum;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AdminOwnerOrderDetailController {
    @Autowired
    private OwnerOrderDetailService ownerOrderDetailService;
    @Autowired
    private AdminLogService adminLogService;


    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:29
     * @Description: 车主租金明细
     *
     **/
    @AutoDocMethod(description = "车主租金明细", value = "车主租金明细", response = OwnerRentDetailDTO.class)
    @GetMapping("/console/owner/ownerRentDetail")
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
    
    
    /**
     * @Date 2020/1/15 21:29
     * @Description: 车主租客调价明细                      见：/renterPriceAdjustment/list 接口合并。
     * 
     **/
    @AutoDocMethod(description = "车主租客调价明细", value = "车主租客调价明细", response = RenterOwnerPriceDTO.class)
    @GetMapping("/console/owner/renterOwnerPrice")
    public ResponseData<RenterOwnerPriceDTO> renterOwnerPrice(@RequestParam("orderNo") String orderNo, @RequestParam("ownerOrderNo") String ownerOrderNo){
        ResponseData<RenterOwnerPriceDTO> responseData = ownerOrderDetailService.renterOwnerPrice(orderNo,ownerOrderNo);
        return responseData;
    }
    

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 违约罚金明细
     *
     **/
    @AutoDocMethod(description = "违约罚金明细", value = "违约罚金明细", response = FienAmtDetailDTO.class)
    @GetMapping("/console/owner/fienAmtDetail")
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

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 违约罚金明细
     *
     **/
    @AutoDocMethod(description = "违约罚金明细列表", value = "违约罚金明细列表", response = FienDetailRespListDTO.class)
    @GetMapping("/console/owner/fienAmtDetailList")
    public ResponseData<FienDetailRespListDTO> fienAmtDetailList(@RequestParam("orderNo") String orderNo, @RequestParam("ownerMemNo") String ownerMemNo){
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }

        if(ownerMemNo == null || ownerMemNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主会员号不能为空");
            return responseData;
        }
        FienDetailRespListDTO fienDetailRespDTOS = ownerOrderDetailService.fienAmtDetailList(orderNo,ownerMemNo);
        return ResponseData.success(fienDetailRespDTOS);
    }

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:31
     * @Description: 服务费明细
     *
     **/
    @AutoDocMethod(description = "车主服务费明细", value = "车主服务费明细", response = ServiceDetailDTO.class)
    @GetMapping("/console/owner/serviceDetail")
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

    /**
     * @Author ZhangBin
     * @Date 2020/1/15 21:37
     * @Description: 车主需支付给平台的费用
     * 
     **/
    @AutoDocMethod(description = "车主需支付给平台的费用", value = "车主需支付给平台的费用", response = PlatformToOwnerDTO.class)
    @GetMapping("/console/owner/platformToOwner")
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
    @AutoDocMethod(description = "平台给车主的补贴", value = "平台给车主的补贴", response = PlatformToOwnerSubsidyDTO.class)
    @GetMapping("/console/owner/platformToOwnerSubsidy")
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

    @AutoDocMethod(description = "修改违约罚金", value = "修改违约罚金", response = ResponseData.class)
    @PostMapping("/console/owner/updateFien")
    public ResponseData<?> updateFineAmt(@RequestBody FienAmtUpdateReqVO fienAmtUpdateReqDTO, BindingResult bindingResult){  //@Valid 
        BindingResultUtil.checkBindingResult(bindingResult);
        ResponseData<FienAmtDetailDTO> oldDataResponse = null;
        try{
            oldDataResponse = ownerOrderDetailService.fienAmtDetail(fienAmtUpdateReqDTO.getOrderNo(),fienAmtUpdateReqDTO.getOwnerOrderNo());
        }catch (Exception e){
            log.error("查询违约罚金异常！");
        }
        FienAmtUpdateReqDTO reqDTO = new FienAmtUpdateReqDTO();
        BeanUtils.copyProperties(fienAmtUpdateReqDTO,reqDTO);
        reqDTO.setOwnerGetReturnCarFienCashNo(FineTypeCashCodeEnum.GET_RETURN_CAR.getFineType());
        reqDTO.setOwnerModifyAddrAmtCashNo(FineTypeCashCodeEnum.MODIFY_ADDRESS_FINE.getFineType());
        ResponseData<?> responseData = ownerOrderDetailService.updateFineAmt(reqDTO);

        try{
            if(oldDataResponse != null && oldDataResponse.getData()!=null){
                String oldData = oldDataResponse.getData().getOwnerGetReturnCarFienAmt()==null?null:String.valueOf(oldDataResponse.getData().getOwnerGetReturnCarFienAmt());
                String newData = fienAmtUpdateReqDTO.getOwnerGetReturnCarFienAmt()==null?null:String.valueOf(fienAmtUpdateReqDTO.getOwnerGetReturnCarFienAmt());
                String desc = "";
                if(CompareBeanUtils.compareString(oldData,newData)){
                    desc += "车主罚金修改: 将原值 "+oldData+" 修改为 "+newData;
                }
                adminLogService.insertLog(AdminOpTypeEnum.OWNER_FINE,fienAmtUpdateReqDTO.getOrderNo(),null, fienAmtUpdateReqDTO.getOwnerOrderNo(), desc); ;
            }
        }catch (Exception e){
            log.error("修改违约罚金记录日志异常",e);
        }
        return responseData;
    }


}
