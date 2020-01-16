package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.service.AdminOrderService;
import com.atzuche.order.admin.vo.req.order.CancelOrderByPlatVO;
import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

/**
 * 订单操作接口
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/7 11:08 上午
 **/
@Slf4j
@RestController
public class OrderController {
    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    private AdminOrderService adminOrderService;
    @Autowired
    private FeignOrderDetailService feignOrderDetailService;

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "修改订单", value = "修改订单",response = ResponseData.class)
    @RequestMapping(value="console/order/modifyOrder",method = RequestMethod.POST)
    public ResponseData modifyOrder(@RequestBody ModifyOrderReqVO modifyOrderReqVO, BindingResult bindingResult)throws Exception{
        log.info("车辆押金信息-modifyOrderReqVO={}", JSON.toJSONString(modifyOrderReqVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        String orderNo = modifyOrderReqVO.getOrderNo();
        OrderDetailReqDTO reqDTO = new OrderDetailReqDTO();
        reqDTO.setOrderNo(orderNo);

        ResponseData<OrderDetailRespDTO> respDTOResponseData =feignOrderDetailService.getOrderDetail(reqDTO);
        if(respDTOResponseData==null||!ErrorCode.SUCCESS.getCode().equalsIgnoreCase(respDTOResponseData.getResCode())){
            throw new RenterInfoController.RenterNotFoundException(orderNo);
        }

        OrderDetailRespDTO detailRespDTO = respDTOResponseData.getData();
        String  memNo = detailRespDTO.getRenterMember().getMemNo();
        modifyOrderReqVO.setMemNo(memNo);

        ResponseData responseData = adminOrderService.modifyOrder(modifyOrderReqVO);
        return responseData;
    }

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "平台取消", value = "平台取消",response = ResponseData.class)
    @RequestMapping(value="console/order/cancel/plat",method = RequestMethod.POST)
    public ResponseData cancelOrderByPlat(@RequestBody CancelOrderByPlatVO cancelOrderByPlatVO,BindingResult result, HttpServletRequest request, HttpServletResponse response)throws Exception{
         logger.info("admin={},cancelOrderByPlatVO is {}", AdminUserUtil.getAdminUser(),cancelOrderByPlatVO);
         if(result.hasErrors()){
             Optional<FieldError> error = result.getFieldErrors().stream().findFirst();
             return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                     error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
         }
         cancelOrderByPlatVO.setOperator(AdminUserUtil.getAdminUser().getAuthName());
         adminOrderService.cancelOrderByAdmin(cancelOrderByPlatVO);
         return ResponseData.success();


    }

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "车主或者租客取消", value = "车主或者租客取消",response = ResponseData.class)
    @RequestMapping(value="console/order/cancel",method = RequestMethod.POST)
    public ResponseData cancelOrder(@Valid @RequestBody CancelOrderVO cancelOrderVO, BindingResult bindingResult)throws Exception{
        log.info("车主或者租客取消-reqVo={}", JSON.toJSONString(cancelOrderVO));
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData responseData = adminOrderService.cancelOrder(cancelOrderVO);
        return responseData;
    }


}
