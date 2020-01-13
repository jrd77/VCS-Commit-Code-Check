package com.atzuche.order.admin.controller.order.remark;

import com.atzuche.order.admin.cat.CatLogRecord;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.constant.cat.UrlConstant;
import com.atzuche.order.admin.constant.description.DescriptionConstant;
import com.atzuche.order.admin.controller.BaseController;
import com.atzuche.order.admin.description.LogDescription;
import com.atzuche.order.admin.dto.remark.OrderRiskStatusRequestDTO;
import com.atzuche.order.admin.exception.remark.OrderRemarkException;
import com.atzuche.order.admin.service.remark.OrderRemarkService;
import com.atzuche.order.admin.vo.req.remark.OrderCarServiceRemarkRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRentCityRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRiskStatusRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderOtherInformationResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkResponseVO;

import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusRespDTO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.open.service.FeignOrderUpdateService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.autoyol.event.rabbit.risk.RiskRabbitMQEventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/console/order/other/information")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderOtherInformationController extends BaseController{


    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkController.class);


    @Autowired
    OrderRemarkService orderRemarkService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    FeignOrderDetailService feignOrderDetailService;

    @Autowired
    FeignOrderUpdateService feignOrderUpdateService;



	@AutoDocMethod(description = "修改租车城市", value = "修改租车城市", response = ResponseData.class)
    @RequestMapping(value = "/rent/city/update", method = RequestMethod.PUT)
	public ResponseData<ResponseData> updateRentCity(@Valid @RequestBody OrderRentCityRequestVO orderRentCityRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RENT_CITY_UPDATE, DescriptionConstant.INPUT_TEXT),orderRentCityRequestVO.toString());
            //调用订单服务修改租车城市
            RentCityAndRiskAccidentReqDTO rentCityAndRiskAccidentReqDTO = new RentCityAndRiskAccidentReqDTO();
            rentCityAndRiskAccidentReqDTO.setRentCity(orderRentCityRequestVO.getRentCity());
            ResponseData<?> responseData = feignOrderUpdateService.updateRentCityAndRiskAccident(rentCityAndRiskAccidentReqDTO);
            if(!ObjectUtils.isEmpty(responseData)) {
                if(ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                    CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RENT_CITY_UPDATE, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_RENT_CITY_UPDATE,  orderRentCityRequestVO);
                    return ResponseData.success();
                }
            }
            return ResponseData.error();
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RENT_CITY_UPDATE, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RENT_CITY_UPDATE, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_RENT_CITY_UPDATE,  orderRentCityRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

	}

    @AutoDocMethod(description = "是否风控事故修改", value = "是否风控事故修改", response = ResponseData.class)
    @RequestMapping(value = "/risk/status/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> updateRiskStatus(@Valid @RequestBody OrderRiskStatusRequestVO orderRiskStatusRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);

        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RISK_STATUS_UPDATE, DescriptionConstant.INPUT_TEXT),orderRiskStatusRequestVO.toString());
            //调用订单服务修改风控事故状态
            RentCityAndRiskAccidentReqDTO rentCityAndRiskAccidentReqDTO = new RentCityAndRiskAccidentReqDTO();
            rentCityAndRiskAccidentReqDTO.setIsRiskAccident(Integer.parseInt(orderRiskStatusRequestVO.getRiskAccidentStatus()));
            ResponseData<?> responseData = feignOrderUpdateService.updateRentCityAndRiskAccident(rentCityAndRiskAccidentReqDTO);
            if(!ObjectUtils.isEmpty(responseData)) {
                if(ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                    //修改成功发送MQ事件
                    OrderRiskStatusRequestDTO orderRiskStatusRequestDTO = new OrderRiskStatusRequestDTO();
                    BeanUtils.copyProperties(orderRiskStatusRequestVO, orderRiskStatusRequestDTO);
                    orderRiskStatusRequestDTO.setOperator(AdminUserUtil.getAdminUser().getAuthName());
                    String mqJson = GsonUtils.toJson(orderRiskStatusRequestDTO);
                    rabbitTemplate.convertAndSend(RiskRabbitMQEventEnum.ORDER_RISK_STATUS_CHANGE.exchange, RiskRabbitMQEventEnum.ORDER_RISK_STATUS_CHANGE.routingKey, mqJson);
                    CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RISK_STATUS_UPDATE, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_RISK_STATUS_UPDATE,  orderRiskStatusRequestVO);
                    return ResponseData.success();
                }
            }
            return ResponseData.error();
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RISK_STATUS_UPDATE, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_RISK_STATUS_UPDATE, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_RISK_STATUS_UPDATE,  orderRiskStatusRequestVO, e);

            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "取送车备注修改", value = "取送车备注修改", response = ResponseData.class)
    @RequestMapping(value = "/car/service/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> updateGetReturnCarRemark(@Valid @RequestBody OrderCarServiceRemarkRequestVO orderCarServiceRemarkRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_CAR_SERVICE_UPDATE, DescriptionConstant.INPUT_TEXT),orderCarServiceRemarkRequestVO.toString());
            orderRemarkService.updateCarServiceRemarkByOrderNo(orderCarServiceRemarkRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_CAR_SERVICE_UPDATE, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_CAR_SERVICE_UPDATE,  orderCarServiceRemarkRequestVO);

            return ResponseData.success();
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_CAR_SERVICE_UPDATE, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_CAR_SERVICE_UPDATE, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_CAR_SERVICE_UPDATE,  orderCarServiceRemarkRequestVO, e);

            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }

    @AutoDocMethod(description = "获取其他备注信息(包括租车城市,风控事故，取送车备注)", value = "获取其他备注信息(包括租车城市,风控事故，取送车备注)", response = OrderOtherInformationResponseVO.class)
    @GetMapping("/detail")
    public ResponseData<OrderOtherInformationResponseVO> getOtherRemarkInformation(OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_DETAIL, DescriptionConstant.INPUT_TEXT),orderRemarkRequestVO.toString());
            OrderOtherInformationResponseVO orderOtherInformationResponseVO = new OrderOtherInformationResponseVO();
            //调用订单服务获取租车城市和风控事故状态
            OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
            orderDetailReqDTO.setOrderNo(orderRemarkRequestVO.getOrderNo());
            ResponseData<OrderStatusRespDTO> orderStatusRespDTOResponse = feignOrderDetailService.getOrderStatus(orderDetailReqDTO);
            if(!ObjectUtils.isEmpty(orderStatusRespDTOResponse)){
                OrderStatusRespDTO orderStatusRespDTO = orderStatusRespDTOResponse.getData();
                orderOtherInformationResponseVO.setRentCity(orderStatusRespDTO.getOrderDTO().getRentCity());
                orderOtherInformationResponseVO.setRiskAccidentStatus(orderStatusRespDTO.getOrderStatusDTO().getIsRiskAccident().toString());
            }
            OrderRemarkResponseVO orderRemarkResponseVO = orderRemarkService.getOrderCarServiceRemarkInformation(orderRemarkRequestVO);
            orderOtherInformationResponseVO.setRemarkContent(orderRemarkResponseVO.getRemarkContent());
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_DETAIL, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_DETAIL,  orderRemarkRequestVO);
            return ResponseData.success(orderOtherInformationResponseVO);
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_DETAIL, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_OTHER_INFORMATION_DETAIL, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_OTHER_INFORMATION_DETAIL,  orderRemarkRequestVO, e);

            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }


    /**
     * 验证参数
     * @param bindingResult
     */
    private void validateParameter(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new OrderRemarkException(ErrorCode.PARAMETER_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }
    }



}
