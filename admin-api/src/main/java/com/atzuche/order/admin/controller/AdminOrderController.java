package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.service.AdminOrderService;
import com.atzuche.order.admin.service.ModificationOrderService;
import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.admin.service.car.CarService;
import com.atzuche.order.admin.vo.req.AdminTransferCarReqVO;
import com.atzuche.order.admin.vo.req.order.*;
import com.atzuche.order.admin.vo.resp.order.AdminModifyOrderFeeCompareVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.entity.dto.ModifyOrderConsoleDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailReqDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDetailRespDTO;
import com.atzuche.order.commons.vo.DebtDetailVO;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.commons.vo.res.AdminOrderJudgeDutyResVO;
import com.atzuche.order.open.service.FeignOrderDetailService;
import com.atzuche.order.open.vo.request.TransferReq;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.caucho.hessian.io.RemoteDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
public class AdminOrderController {
    private final static Logger logger = LoggerFactory.getLogger(AdminOrderController.class);
    
    @Autowired
    private AdminOrderService adminOrderService;
    @Autowired
    private RemoteFeignService remoteFeignService;

    @Autowired
    private CarService carService;
    @Autowired
    private ModificationOrderService modificationOrderService;

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
        //OrderDetailReqDTO reqDTO = new OrderDetailReqDTO();
        //reqDTO.setOrderNo(orderNo);

        //ResponseData<OrderDetailRespDTO> respDTOResponseData =feignOrderDetailService.getOrderDetail(reqDTO);
        ResponseData<OrderDetailRespDTO> respDTOResponseData =remoteFeignService.getOrderdetailFromRemote(orderNo);

        OrderDetailRespDTO detailRespDTO = respDTOResponseData.getData();
        String  memNo = detailRespDTO.getRenterMember().getMemNo();
        modifyOrderReqVO.setMemNo(memNo);
        modifyOrderReqVO.setConsoleFlag(true);
        modifyOrderReqVO.setOperator(AdminUserUtil.getAdminUser().getAuthName());
        //adminOrderService.modifyOrder(modifyOrderReqVO);
        // 获取修改前数据
 		ModifyOrderConsoleDTO modifyOrderConsoleDTO = remoteFeignService.getInitModifyOrderDTO(modifyOrderReqVO);
        remoteFeignService.modifyOrder(modifyOrderReqVO);
        // 保存操作日志
        modificationOrderService.saveModifyOrderLog(modifyOrderReqVO, modifyOrderConsoleDTO);
        return ResponseData.success();
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
        BindingResultUtil.checkBindingResult(bindingResult);

        ResponseData responseData = adminOrderService.cancelOrder(cancelOrderVO);
        return responseData;
    }


    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "车主同意订单", value = "车主同意订单",response = ResponseData.class)
    @RequestMapping(value="console/order/owner/agree",method = RequestMethod.POST)
    public ResponseData agreeOrder(@Valid @RequestBody OwnerAgreeOrRefuseOrderReqVO reqVO,
                               BindingResult bindingResult){
        log.info("车主同意订单-reqVo={}", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        return adminOrderService.agreeOrder(reqVO);
    }


    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "车主拒绝订单", value = "车主拒绝订单",response = ResponseData.class)
    @RequestMapping(value="console/order/owner/refuse",method = RequestMethod.POST)
    public ResponseData refuseOrder(@Valid @RequestBody OwnerAgreeOrRefuseOrderReqVO reqVO,
                                    BindingResult bindingResult){
        log.info("车主拒绝订单-reqVo={}", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);

        return adminOrderService.refuseOrder(reqVO);
    }


    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "取消订单责任判定", value = "取消订单责任判定",response = ResponseData.class)
    @RequestMapping(value="console/order/cancel/judgeDuty",method = RequestMethod.POST)
    public ResponseData judgeDuty(@Valid @RequestBody CancelOrderJudgeDutyReqVO reqVO,
                                    BindingResult bindingResult){
        log.info("取消订单责任判定-reqVo={}", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        return adminOrderService.cancelOrderJudgeDuty(reqVO);
    }


    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "取消订单责任判定列表(取消/申诉--手动/自动判责)", value = "取消订单责任判定列表(取消/申诉--手动/自动判责)", response = AdminOrderJudgeDutyResVO.class)
    @RequestMapping(value = "console/order/cancel/judgeDuty/list", method = RequestMethod.POST)
    public ResponseData<AdminOrderJudgeDutyResVO> judgeDutyList(@Valid @RequestBody CancelOrderJudgeDutyListReqVO reqVO,
                                                                BindingResult bindingResult) {
        log.info("取消/申诉--手动/自动判责列表-reqVo={}", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        return adminOrderService.cancelOrderJudgeDutyList(reqVO.getOrderNo());
    }


    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "车主拒绝或者同意租客的订单修改申请接口", value = "车主拒绝或者同意租客的订单修改申请接口",response = ResponseData.class)
    @RequestMapping(value="console/order/modify/confirm",method = RequestMethod.POST)
    public ResponseData modifyApplicationConfirm(@Valid @RequestBody OrderModifyConfirmReqVO reqVO,BindingResult result){
        log.info("reqVo is {}",reqVO);
        if(result.hasErrors()){
            Optional<FieldError> error = result.getFieldErrors().stream().findFirst();
            return ResponseData.createErrorCodeResponse(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }

        adminOrderService.modificationConfirm(reqVO);
        return ResponseData.success();

    }

    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "订单修改前的费用对比", value = "订单修改前的费用对比",response = AdminModifyOrderFeeCompareVO.class)
    @RequestMapping(value="console/order/modify/prefee",method = RequestMethod.POST)
    public ResponseData<AdminModifyOrderFeeCompareVO> preModifyOrder(@Valid @RequestBody AdminModifyOrderReqVO reqVO,BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);

        String renterNo = adminOrderService.getRenterMemNo(reqVO.getOrderNo());

        AdminModifyOrderFeeCompareVO compareVO = adminOrderService.preModifyOrderFee(reqVO,renterNo);
        // 获取欠款
		DebtDetailVO debtDetailVO = adminOrderService.getDebtAmt(renterNo);
		compareVO.setDebtDetailVO(debtDetailVO);
		 
        return ResponseData.success(compareVO);

    }


    @AutoDocVersion(version = "订单修改")
    @AutoDocGroup(group = "订单修改")
    @AutoDocMethod(description = "管理后台换车", value = "管理后台换车")
    @RequestMapping(value="console/changeCar",method = RequestMethod.POST)
    public ResponseData<?> changeCar(@Valid @RequestBody AdminTransferCarReqVO reqVO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        if (StringUtils.isBlank(reqVO.getCarNo()) && StringUtils.isBlank(reqVO.getPlateNum())) {
        	return ResponseData.createErrorCodeResponse("408508", "车辆号和车牌号二者必选其一");
        }
        // 根据车牌号获取车辆注册号
        String carNo = StringUtils.isBlank(reqVO.getCarNo()) ? carService.getCarNoByPlateNum(reqVO.getPlateNum()):reqVO.getCarNo();
        TransferReq req = new TransferReq();
        req.setOperator(AdminUserUtil.getAdminUser().getAuthName());
        BeanUtils.copyProperties(reqVO,req);
        req.setCarNo(carNo);
        String oldPlateNum = remoteFeignService.getCarPlateNum(reqVO.getOrderNo());
        adminOrderService.transferCar(req);
        String updPlateNum = remoteFeignService.getCarPlateNum(reqVO.getOrderNo());
        // 保存操作日志
        modificationOrderService.saveTransferLog(reqVO.getOrderNo(), oldPlateNum, updPlateNum);
        return ResponseData.success();


    }




}
