package com.atzuche.order.admin.controller.order.remark;

import com.atzuche.order.admin.controller.BaseController;
import com.atzuche.order.admin.exception.remark.OrderRemarkException;
import com.atzuche.order.admin.service.remark.OrderRemarkService;
import com.atzuche.order.admin.vo.req.remark.OrderCarServiceRemarkRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRemarkRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRentCityRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRiskStatusRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderOtherInformationResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/console/order/other/information")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderOtherInformationController extends BaseController{


    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkController.class);


    @Autowired
    OrderRemarkService orderRemarkService;


	@AutoDocMethod(description = "修改租车城市", value = "修改租车城市", response = ResponseData.class)
    @RequestMapping(value = "/rent/city/update", method = RequestMethod.PUT)
	public ResponseData<ResponseData> updateRentCity(@RequestBody OrderRentCityRequestVO orderRentCityRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        //调用订单服务
        try{
            logger.info("修改租车城市入参:{}",orderRentCityRequestVO.toString());
            return ResponseData.success();
        } catch (Exception e) {
            logger.info("修改租车城市异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

	}

    @AutoDocMethod(description = "是否风控事故修改", value = "是否风控事故修改", response = ResponseData.class)
    @RequestMapping(value = "/risk/status/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> updateRiskStatus(@RequestBody OrderRiskStatusRequestVO orderRiskStatusRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        //调用订单服务
        try{
            logger.info("是否风控事故修改入参:{}",orderRiskStatusRequestVO.toString());
            return ResponseData.success();
        } catch (Exception e) {
            logger.info("是否风控事故修改异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "取送车备注修改", value = "取送车备注修改", response = ResponseData.class)
    @RequestMapping(value = "/car/service/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> updateGetReturnCarRemark(@RequestBody OrderCarServiceRemarkRequestVO orderCarServiceRemarkRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        try{
            logger.info("取送车备注修改入参:{}",orderCarServiceRemarkRequestVO.toString());
            orderRemarkService.updateCarServiceRemarkByOrderNo(orderCarServiceRemarkRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.info("取送车备注修改异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }

    @AutoDocMethod(description = "获取其他备注信息(包括租车城市,风控事故，取送车备注)", value = "获取其他备注信息(包括租车城市,风控事故，取送车备注)", response = OrderOtherInformationResponseVO.class)
    @GetMapping("/detail")
    public ResponseData<OrderOtherInformationResponseVO> getOtherRemarkInformation(OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
        validateParameter(bindingResult);
        try{
            logger.info("获取其他备注信息入参:{}",orderRemarkRequestVO.toString());
            OrderOtherInformationResponseVO orderOtherInformationResponseVO = new OrderOtherInformationResponseVO();
            //调用订单服务
            orderOtherInformationResponseVO.setRentCity("上海");
            orderOtherInformationResponseVO.setRiskAccidentStatus("1");
            OrderRemarkResponseVO orderRemarkResponseVO = orderRemarkService.getOrderCarServiceRemarkInformation(orderRemarkRequestVO);
            orderOtherInformationResponseVO.setRemarkContent(orderRemarkResponseVO.getRemarkContent());
            return ResponseData.success(orderOtherInformationResponseVO);
        } catch (Exception e) {
            logger.info("获取其他备注信息异常{}",e);
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
