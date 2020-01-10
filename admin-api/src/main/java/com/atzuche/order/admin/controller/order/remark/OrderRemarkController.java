package com.atzuche.order.admin.controller.order.remark;

import com.atzuche.order.admin.controller.BaseController;
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
	public ResponseData<OrderRemarkOverviewListResponseVO> getOverview(OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
        //参数验证
	    validateParameter(bindingResult);
	    try{
            logger.info("获取备注总览入参:{}",orderRemarkRequestVO.toString());
            return ResponseData.success(orderRemarkService.getOrderRemarkOverview(orderRemarkRequestVO));
        } catch (Exception e) {
            logger.info("获取备注总览异常:{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
	}

    @AutoDocMethod(description = "备注查询列表", value = "备注查询列表", response = OrderRemarkPageListResponseVO.class)
    @GetMapping("/list")
    public ResponseData<OrderRemarkPageListResponseVO> selectRemarklist(OrderRemarkListRequestVO orderRemarkListRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("备注查询列表入参:{}",orderRemarkListRequestVO.toString());
            return ResponseData.success(orderRemarkService.selectRemarklist(orderRemarkListRequestVO));
        } catch (Exception e) {
            logger.info("备注查询列表异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }

    @AutoDocMethod(description = "备注日志查询列表", value = "备注日志查询列表", response = OrderRemarkLogPageListResponseVO.class)
    @GetMapping("/log/list")
    public ResponseData<OrderRemarkLogPageListResponseVO> logList(OrderRemarkLogListRequestVO orderRemarkLogListRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("备注日志查询列表入参:{}",orderRemarkLogListRequestVO.toString());
            return ResponseData.success(orderLogRemarkService.selectRemarkLoglist(orderRemarkLogListRequestVO));
        } catch (Exception e) {
            logger.info("备注日志查询列表异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }


    @AutoDocMethod(description = "添加备注", value = "添加备注", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> addOrderRemark(@RequestBody OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("添加备注入参:{}",orderRemarkAdditionRequestVO.toString());
            orderRemarkService.addOrderRemark(orderRemarkAdditionRequestVO);
            return ResponseData.success(null);
        } catch (Exception e) {
            logger.info("添加备注异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "删除备注", value = "删除备注", response = ResponseData.class)
    @DeleteMapping("/delete")
    public ResponseData<ResponseData> delete(@RequestBody OrderRemarkDeleteRequestVO orderRemarkDeleteRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("删除备注入参:{}",orderRemarkDeleteRequestVO.toString());
            orderRemarkService.deleteRemarkById(orderRemarkDeleteRequestVO);
            return ResponseData.success(null);
        } catch (Exception e) {
            logger.info("删除备注异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "编辑备注", value = "编辑备注", response = ResponseData.class)
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> update(@RequestBody OrderRemarkUpdateRequestVO orderRemarkUpdateRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("编辑备注入参:{}",orderRemarkUpdateRequestVO.toString());
            orderRemarkService.updateRemarkById(orderRemarkUpdateRequestVO);
            return ResponseData.success(null);
        } catch (Exception e) {
            logger.info("编辑备注异常{}",e);
            throw new OrderRemarkException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "获取备注信息", value = "获取备注信息", response = OrderRemarkResponseVO.class)
    @GetMapping("/information")
    public ResponseData<OrderRemarkResponseVO> getRemarkInformation(OrderRemarkInformationRequestVO orderRemarkInformationRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("获取备注信息入参:{}",orderRemarkInformationRequestVO.toString());
            return ResponseData.success(orderRemarkService.getOrderRemarkInformation(orderRemarkInformationRequestVO));
        } catch (Exception e) {
            logger.info("获取备注信息异常{}",e);
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
