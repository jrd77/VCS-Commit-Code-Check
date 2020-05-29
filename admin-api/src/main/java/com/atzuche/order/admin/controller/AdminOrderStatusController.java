package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.enums.OrderStatusEnums;
import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.admin.vo.req.order.ModificationOrderRequestVO;
import com.atzuche.order.admin.vo.req.order.OrderStatusRequestVO;
import com.atzuche.order.admin.vo.resp.order.OrderStatusListResponseVO;
import com.atzuche.order.admin.vo.resp.order.OrderStatusResponseVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.OrderFlowDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowListResponseDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusRespDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.open.service.FeignOrderFlowService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class AdminOrderStatusController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderStatusController.class);

    @Autowired
	private FeignOrderFlowService feignOrderFlowService;
    @Autowired
    private FeignOrderDetailService feignOrderDetailService;
    @Autowired
    private RemoteFeignService remoteFeignService;


	@AutoDocMethod(description = "订单状态流转列表", value = "订单状态流转列表", response = OrderStatusListResponseVO.class)
	@GetMapping("status/list")
	public ResponseData statusList(OrderStatusRequestVO orderStatusRequestVO, BindingResult bindingResult) {
        BindingResultUtil.checkBindingResult(bindingResult);

        logger.info("订单状态流转列表入参{}",orderStatusRequestVO);
        OrderStatusListResponseVO orderStatusListResponseVO = new OrderStatusListResponseVO();
        try{
            //获取订单流转列表
            OrderFlowRequestDTO orderFlowRequestDTO = new OrderFlowRequestDTO();
            orderFlowRequestDTO.setOrderNo(orderStatusRequestVO.getOrderNo());
            //ResponseData<OrderFlowListResponseDTO> responseDataOrderFlowListResponseDTO = feignOrderFlowService.selectOrderFlowList(orderFlowRequestDTO);
            ResponseData<OrderFlowListResponseDTO> responseDataOrderFlowListResponseDTO = remoteFeignService.selectOrderFlowListFromRemote(orderFlowRequestDTO);
            List<OrderStatusResponseVO> orderStatusList = new ArrayList();
            OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
            orderDetailReqDTO.setOrderNo(orderStatusRequestVO.getOrderNo());
            if(responseDataOrderFlowListResponseDTO.getResCode().equals(ErrorCode.SUCCESS.getCode())){
                List<OrderFlowDTO>  orderFlowList = responseDataOrderFlowListResponseDTO.getData().getOrderFlowList();
                if(!CollectionUtils.isEmpty(orderFlowList)) {
                    orderFlowList.forEach(orderFlowDTO -> {
                        OrderStatusResponseVO orderStatusResponseVO = new OrderStatusResponseVO();
                        orderStatusResponseVO.setChangeTime(orderFlowDTO.getCreateTime().toString());
                        orderStatusResponseVO.setStatusDescription(orderFlowDTO.getOrderStatusDesc());
                        orderStatusResponseVO.setSource(orderFlowDTO.getSource());
                        orderStatusList.add(orderStatusResponseVO);
                    });
                }
                orderStatusListResponseVO.setOrderStatusList(orderStatusList);
            }
            //获取订单当前状态描述
            //ResponseData<OrderStatusRespDTO> responseDataOrderStatusRespDTO = feignOrderDetailService.getOrderStatus(orderDetailReqDTO);
            ResponseData<OrderStatusRespDTO> responseDataOrderStatusRespDTO = remoteFeignService.getOrderStatusFromRemote(orderDetailReqDTO);
            if(responseDataOrderStatusRespDTO.getResCode().equals(ErrorCode.SUCCESS.getCode())){
                Integer status=responseDataOrderStatusRespDTO.getData().getOrderStatusDTO().getStatus();
                orderStatusListResponseVO.setStatusDescription(OrderStatusEnums.getDescriptionByStatus(status));

            }
        }catch (Exception e) {
            logger.info("订单状态流转列表异常{}",e);
        }


		return ResponseData.success(orderStatusListResponseVO);
	}




}
