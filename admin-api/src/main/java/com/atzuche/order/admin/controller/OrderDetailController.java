package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.OrderDetailService;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryListDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderHistoryRespDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.AdminOwnerOrderDetailDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单详情接口
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/6 7:03 下午
 **/
@RestController
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private FeignOrderDetailService feignOrderDetailService;

    @AutoDocMethod(description = "订单历史信息", value = "订单历史信息",response = OrderHistoryRespDTO.class)
    @RequestMapping(value = "console/order/history/list", method = RequestMethod.GET)
    public ResponseData<OrderHistoryRespDTO> listOrderHistory(@RequestParam("orderNo") String orderNo)throws Exception{
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setData(null);
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("订单号不能为空");
            return responseData;
        }
        ResponseData<OrderHistoryRespDTO> orderHistoryRespDTOResponseData = orderDetailService.listOrderHistory(orderNo);
        return orderHistoryRespDTOResponseData;
    }

    @AutoDocMethod(description = "车主子订单详情", value = "车主子订单详情",response = AdminOwnerOrderDetailDTO.class)
    @RequestMapping(value = "console/order/owner/detail", method = RequestMethod.GET)
    public ResponseData<AdminOwnerOrderDetailDTO> ownerOrderDetail(@RequestParam("ownerOrderNo") String ownerOrderNo,@RequestParam("orderNo")String orderNo)throws Exception{

        if(ownerOrderNo == null || ownerOrderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setData(null);
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("车主子订单号不能为空");
            return responseData;
        }
        ResponseData<AdminOwnerOrderDetailDTO> orderHistoryRespDTOResponseData = orderDetailService.ownerOrderDetail(ownerOrderNo,orderNo);
        return orderHistoryRespDTOResponseData;
    }

    @AutoDocMethod(description = "人工调度历史订单", value = "人工调度历史订单",response = OrderHistoryDTO.class)
    @RequestMapping(value = "console/order/dispatchHistory", method = RequestMethod.GET)
    public ResponseData<OrderHistoryListDTO> dispatchHistory(@RequestParam("orderNo")String orderNo)throws Exception{
        if(orderNo == null || orderNo.trim().length()<=0){
            ResponseData responseData = new ResponseData();
            responseData.setData(null);
            responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
            responseData.setResMsg("主订单号不能为空");
            return responseData;
        }
        ResponseData<OrderHistoryListDTO> responseData = orderDetailService.dispatchHistory(orderNo);
        return responseData;
    }

}
