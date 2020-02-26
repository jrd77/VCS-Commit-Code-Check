package com.atzuche.order.admin.controller.order.remark;

import com.atzuche.order.admin.cat.CatLogRecord;
import com.atzuche.order.admin.constant.cat.UrlConstant;
import com.atzuche.order.admin.constant.description.DescriptionConstant;
import com.atzuche.order.admin.controller.BaseController;
import com.atzuche.order.admin.description.LogDescription;
import com.atzuche.order.admin.exception.remark.OrderRemarkException;
import com.atzuche.order.admin.service.remark.OrderRemarkLogService;
import com.atzuche.order.admin.service.remark.OrderRemarkService;
import com.atzuche.order.admin.vo.req.remark.*;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkLogPageListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkOverviewListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkPageListResponseVO;
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

import javax.validation.Valid;

@RequestMapping("/console/order/remark")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderRemarkController extends BaseController {


    private static final Logger logger = LoggerFactory.getLogger(OrderRemarkController.class);

    @Autowired
    OrderRemarkService orderRemarkService;

    @Autowired
    OrderRemarkLogService orderLogRemarkService;

	@AutoDocMethod(description = "备注总览", value = "备注总览", response = OrderRemarkOverviewListResponseVO.class)
	@GetMapping("/overview")
	public ResponseData<OrderRemarkOverviewListResponseVO> getOverview(@Valid OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
        //参数验证
	    validateParameter(bindingResult);
	    try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_OVERVIEW, DescriptionConstant.INPUT_TEXT),orderRemarkRequestVO.toString());
            OrderRemarkOverviewListResponseVO orderRemarkOverviewListResponseVO = orderRemarkService.getOrderRemarkOverview(orderRemarkRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_OVERVIEW, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_OVERVIEW,  orderRemarkRequestVO);
            return ResponseData.success(orderRemarkOverviewListResponseVO);
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_OVERVIEW, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_OVERVIEW, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_OVERVIEW, orderRemarkRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
	}

    @AutoDocMethod(description = "备注查询列表", value = "备注查询列表", response = OrderRemarkPageListResponseVO.class)
    @GetMapping("/list")
    public ResponseData<OrderRemarkPageListResponseVO> selectRemarkList(@Valid OrderRemarkListRequestVO orderRemarkListRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LIST, DescriptionConstant.INPUT_TEXT),orderRemarkListRequestVO.toString());
            OrderRemarkPageListResponseVO orderRemarkPageListResponseVO = orderRemarkService.selectRemarklist(orderRemarkListRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LIST, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_LIST,  orderRemarkListRequestVO);
            return ResponseData.success(orderRemarkPageListResponseVO);
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LIST, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LIST, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_LIST, orderRemarkListRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }

    @AutoDocMethod(description = "备注日志查询列表", value = "备注日志查询列表", response = OrderRemarkLogPageListResponseVO.class)
    @GetMapping("/log/list")
    public ResponseData<OrderRemarkLogPageListResponseVO> logList(@Valid OrderRemarkLogListRequestVO orderRemarkLogListRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LOG_LIST, DescriptionConstant.INPUT_TEXT),orderRemarkLogListRequestVO.toString());
            OrderRemarkLogPageListResponseVO orderRemarkLogPageListResponseVO = orderLogRemarkService.selectRemarkLoglist(orderRemarkLogListRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LOG_LIST, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_LOG_LIST,  orderRemarkLogListRequestVO);
            return ResponseData.success(orderRemarkLogPageListResponseVO);
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LOG_LIST, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_LOG_LIST, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_LOG_LIST, orderRemarkLogListRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }


    @AutoDocMethod(description = "添加备注", value = "添加备注", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> addOrderRemark(@Valid @RequestBody OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_ADD, DescriptionConstant.INPUT_TEXT),orderRemarkAdditionRequestVO.toString());
            orderRemarkService.addOrderRemark(orderRemarkAdditionRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_ADD, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_ADD,  orderRemarkAdditionRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_ADD, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_ADD, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_ADD, orderRemarkAdditionRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "删除备注", value = "删除备注", response = ResponseData.class)
    @DeleteMapping("/delete")
    public ResponseData<ResponseData> delete(@Valid @RequestBody OrderRemarkDeleteRequestVO orderRemarkDeleteRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_DELETE, DescriptionConstant.INPUT_TEXT),orderRemarkDeleteRequestVO.toString());
            orderRemarkService.deleteRemarkById(orderRemarkDeleteRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_DELETE, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_DELETE,  orderRemarkDeleteRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_DELETE, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_DELETE, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_DELETE, orderRemarkDeleteRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "编辑备注", value = "编辑备注", response = ResponseData.class)
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> update(@Valid @RequestBody OrderRemarkUpdateRequestVO orderRemarkUpdateRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_UPDATE, DescriptionConstant.INPUT_TEXT),orderRemarkUpdateRequestVO.toString());
            orderRemarkService.updateRemarkById(orderRemarkUpdateRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_UPDATE, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_UPDATE,  orderRemarkUpdateRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_UPDATE, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_UPDATE, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_UPDATE, orderRemarkUpdateRequestVO, e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "获取备注信息", value = "获取备注信息", response = OrderRemarkResponseVO.class)
    @GetMapping("/information")
    public ResponseData<OrderRemarkResponseVO> getRemarkInformation(@Valid OrderRemarkInformationRequestVO orderRemarkInformationRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_INFORMATION, DescriptionConstant.INPUT_TEXT),orderRemarkInformationRequestVO.toString());
            OrderRemarkResponseVO orderRemarkResponseVO = orderRemarkService.getOrderRemarkInformation(orderRemarkInformationRequestVO);
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_INFORMATION, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_INFORMATION,  orderRemarkInformationRequestVO);
            return ResponseData.success(orderRemarkResponseVO);
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_INFORMATION, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_REMARK_INFORMATION, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_REMARK_INFORMATION, orderRemarkInformationRequestVO, e);
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
