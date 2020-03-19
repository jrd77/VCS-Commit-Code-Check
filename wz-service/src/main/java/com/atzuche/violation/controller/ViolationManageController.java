package com.atzuche.violation.controller;

import com.atzuche.violation.cat.CatLogRecord;
import com.atzuche.violation.exception.ViolationManageException;
import com.atzuche.violation.service.ViolationManageService;
import com.atzuche.violation.vo.req.*;
import com.atzuche.violation.vo.resp.ViolationAlterationLogListResponseVO;
import com.atzuche.violation.vo.resp.ViolationHandleInformationResponseVO;
import com.atzuche.violation.vo.resp.ViolationInformationListResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

import com.autoyol.event.rabbit.violation.ViolationRabbitMQEventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/console/order/violation")
@RestController
@AutoDocVersion(version = "违章管理接口文档")
public class ViolationManageController {
    private static final Logger logger = LoggerFactory.getLogger(ViolationManageController.class);

    @Autowired
    ViolationManageService violationManageService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @AutoDocMethod(description = "无违章", value = "无违章", response = ResponseData.class)
    @PostMapping("/handle")
    public ResponseData manualHandle(@Valid @RequestBody ViolationHandleRequestVO violationHandleRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("无违章处理入参:{}",violationHandleRequestVO.toString());
            violationManageService.manualHandle(violationHandleRequestVO);
            CatLogRecord.successLog("无违章处理成功","console/order/violation/handle",violationHandleRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("无违章处理异常:{}",e);
            CatLogRecord.failLog("无违章处理异常","console/order/violation/handle",violationHandleRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }



    @AutoDocMethod(description = "获取违章信息", value = "获取违章信息", response = ViolationHandleInformationResponseVO.class)
    @GetMapping("/information")
    public ResponseData information(@Valid @RequestBody ViolationInformationRequestVO violationInformationRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("获取违章信息入参:{}",violationInformationRequestVO.toString());
            violationManageService.getViolationHandleInformation(violationInformationRequestVO);
            CatLogRecord.successLog("获取违章信息成功","console/order/violation/information",violationInformationRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("获取违章信息异常:{}",e);
            CatLogRecord.failLog("获取违章信息异常","console/order/violation/information",violationInformationRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }


    @AutoDocMethod(description = "获取违章信息变更记录", value = "获取违章信息变更记录", response = ViolationAlterationLogListResponseVO.class)
    @GetMapping("/alteration/log/list")
    public ResponseData alterationLogList(@Valid @RequestBody ViolationInformationRequestVO violationInformationRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("获取违章信息变更记录入参:{}",violationInformationRequestVO.toString());
            ViolationAlterationLogListResponseVO violationAlterationLogListResponseVO = violationManageService.selectAlterationLogList(violationInformationRequestVO);
            CatLogRecord.successLog("获取违章信息变更记录成功","console/order/violation/alteration/log/list",violationInformationRequestVO);
            return ResponseData.success(violationAlterationLogListResponseVO);
        } catch (Exception e) {
            logger.error("获取违章信息变更记录异常:{}",e);
            CatLogRecord.failLog("获取违章信息变更记录异常","console/order/violation/alteration/log/list",violationInformationRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }


    @AutoDocMethod(description = "违章编辑", value = "违章编辑", response = ResponseData.class)
    @PostMapping("/handle/update")
    public ResponseData updateViolationHandle(@Valid @RequestBody ViolationHandleAlterationRequestVO violationAlterationRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("违章编辑入参:{}",violationAlterationRequestVO.toString());
            //rabbitTemplate.convertAndSend(ViolationRabbitMQEventEnum.ORDER_VIOLATION_CHANGE.exchange, ViolationRabbitMQEventEnum .ORDER_VIOLATION_CHANGE.routingKey, "");
            violationManageService.updateViolationHandle(violationAlterationRequestVO);
            CatLogRecord.successLog("违章编辑处理成功","console/order/violation/update",violationAlterationRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("违章编辑异常:{}",e);
            CatLogRecord.failLog("违章编辑异常","console/order/violation/update",violationAlterationRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "确认违章办理完成", value = "确认违章办理完成", response = ResponseData.class)
    @PostMapping("/confirm/complete")
    public ResponseData confirmComplete(@Valid @RequestBody ViolationCompleteRequestVO violationCompleteRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("违章编辑入参:{}",violationCompleteRequestVO.toString());
            //rabbitTemplate.convertAndSend(ViolationRabbitMQEventEnum.ORDER_VIOLATION_CHANGE.exchange, ViolationRabbitMQEventEnum .ORDER_VIOLATION_CHANGE.routingKey, "");
            violationManageService.updateConfirmComplete(violationCompleteRequestVO);
            CatLogRecord.successLog("违章编辑处理成功","console/order/violation/update",violationCompleteRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("违章编辑异常:{}",e);
            CatLogRecord.failLog("违章编辑异常","console/order/violation/update",violationCompleteRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }



    @AutoDocMethod(description = "新增违章", value = "新增违章", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData add(@Valid @RequestBody ViolationAdditionRequestVO violationAdditionRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("新增违章入参:{}",violationAdditionRequestVO.toString());

            CatLogRecord.successLog("新增违章成功","console/order/violation/add",violationAdditionRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("新增违章异常:{}",e);
            CatLogRecord.failLog("新增违章异常","console/order/violation/add",violationAdditionRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }


    @AutoDocMethod(description = "删除违章", value = "删除违章", response = ResponseData.class)
    @PostMapping("/delete")
    public ResponseData delete(@Valid @RequestBody ViolationDeleteRequestVO violationDeleteRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("删除违章入参:{}",violationDeleteRequestVO.toString());

            CatLogRecord.successLog("删除违章成功","console/order/violation/delete",violationDeleteRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("删除违章异常:{}",e);
            CatLogRecord.failLog("删除违章异常","console/order/violation/delete",violationDeleteRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }


    @AutoDocMethod(description = "确认已处理", value = "确认已处理", response = ResponseData.class)
    @PostMapping("/updateStatus")
    public ResponseData updateStatus(@Valid @RequestBody ViolationDeleteRequestVO violationDeleteRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("确认已处理入参:{}",violationDeleteRequestVO.toString());

            CatLogRecord.successLog("确认已处理成功","console/order/violation/updateStatus",violationDeleteRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("确认已处理异常:{}",e);
            CatLogRecord.failLog("确认已处理异常","console/order/violation/updateStatus",violationDeleteRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }

    @AutoDocMethod(description = "获取违章列表", value = "获取违章列表", response = ViolationInformationListResponseVO.class)
    @GetMapping("/list")
    public ResponseData alterationLogList(@Valid @RequestBody ViolationListRequestVO violationListRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("获取违章列表入参:{}",violationListRequestVO.toString());

            CatLogRecord.successLog("获取违章列表成功","console/order/violation/list",violationListRequestVO);
            return ResponseData.success();
        } catch (Exception e) {
            logger.error("获取违章列表异常:{}",e);
            CatLogRecord.failLog("获取违章列表异常","console/order/violation/list",violationListRequestVO, e);
            throw new ViolationManageException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }
    }





    /**
     * 验证参数
     * @param bindingResult
     */
    private void validateParameter(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ViolationManageException(ErrorCode.PARAMETER_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }
    }


}
