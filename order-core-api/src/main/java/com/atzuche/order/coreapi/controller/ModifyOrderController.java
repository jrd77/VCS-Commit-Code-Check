package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.OrderTransferRecordDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.entity.vo.DispatchCarInfoVO;
import com.atzuche.order.coreapi.service.*;
import com.atzuche.order.open.vo.ModifyOrderAppReqVO;
import com.atzuche.order.open.vo.ModifyOrderCompareVO;
import com.atzuche.order.open.vo.ModifyOrderScanCodeVO;
import com.atzuche.order.open.vo.ModifyOrderScanPickUpVO;
import com.atzuche.order.open.vo.request.TransferReq;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class ModifyOrderController {


	@Autowired
	private ModifyOrderService modifyOrderService;
	@Autowired
	private ModifyOrderOwnerConfirmService modifyOrderOwnerConfirmService;
	@Autowired
	private ModifyOrderFeeService modifyOrderFeeService;
    @Autowired
	private RenterMemberService renterMemberService;
    @Autowired
    private ModifyOrderConfirmService modifyOrderConfirmService;
    @Autowired
    private ModifyOrderScanCodeService modifyOrderScanCodeService;
    @Autowired
    private ModifyOrderCheckService modifyOrderCheckService;
    @Autowired
    private RenterOrderChangeApplyService renterOrderChangeApplyService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private ModifyOrderExtendService modifyOrderExtendService;
	/**
	 * 修改订单（APP端或H5端）
	 * @param modifyOrderAppReq
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/modify")
	public ResponseData<?> modifyOrder(@Valid @RequestBody ModifyOrderAppReqVO modifyOrderAppReq, BindingResult bindingResult) {
		log.info("修改订单（APP端或H5端）modifyOrderAppReq=[{}]", modifyOrderAppReq);
		BindingResultUtil.checkBindingResult(bindingResult);
        // 属性拷贝
        ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
        BeanUtils.copyProperties(modifyOrderAppReq, modifyOrderReq);
        modifyOrderService.modifyOrder(modifyOrderReq);

        return ResponseData.success();
    }

    /**
     * 修改订单（管理后台）
     *
     * @param modifyOrderReq
     * @param bindingResult
     * @return ResponseData
     */
    @PostMapping("/order/modifyconsole")
    public ResponseData<?> modifyOrderForConsole(@Valid @RequestBody ModifyOrderReq modifyOrderReq, BindingResult bindingResult) {
        log.info("修改订单（管理后台）modifyOrderReq=[{}] ", modifyOrderReq);
		BindingResultUtil.checkBindingResult(bindingResult);
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(modifyOrderReq.getOrderNo());
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus() || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()){
            log.error("已经结算不允许编辑orderNo={}",modifyOrderReq.getOrderNo());
            throw new NotAllowedEditException();
        }

		// 设置为管理后台修改
        modifyOrderReq.setConsoleFlag(true);
        modifyOrderService.modifyOrder(modifyOrderReq);

        return ResponseData.success();
    }

    /**
     * 车主处理修改申请
     *
     * @param modifyApplyHandleReq
     * @param bindingResult
     * @return ResponseData
     */
    @PostMapping("/order/modifyconfirm")
    public ResponseData<?> ownerHandleModifyApplication(@Valid @RequestBody ModifyApplyHandleReq modifyApplyHandleReq, BindingResult bindingResult) {
		log.info("ownerHandleModifyApplication param is [{}]",modifyApplyHandleReq);
    	BindingResultUtil.checkBindingResult(bindingResult);
        modifyOrderOwnerConfirmService.modifyConfirm(modifyApplyHandleReq);
		return ResponseData.success();
    }


    /**
     * 换车操作
     *
     * @param modifyOrderReq
     * @param bindingResult
     * @return ResponseData
     */
    @PostMapping("/order/transfer")
    public ResponseData<?> transfer(@Valid @RequestBody TransferReq transferReq, BindingResult bindingResult) {
        log.info("换车操作transferReq=[{}] ", transferReq);
		BindingResultUtil.checkBindingResult(bindingResult);

        String memNo = renterMemberService.getRenterNoByOrderNo(transferReq.getOrderNo());
        transferReq.setMemNo(memNo);

		// 属性拷贝
		ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
		BeanUtils.copyProperties(transferReq, modifyOrderReq);
		// 设置为管理后台修改
		modifyOrderReq.setConsoleFlag(true);
		// 设置为换车操作
		modifyOrderReq.setTransferFlag(true);
		modifyOrderService.modifyOrder(modifyOrderReq);
		return ResponseData.success();
	}
	
	/**
	 * 修改前费用计算
	 * @param modifyOrderAppReq
	 * @param bindingResult
	 * @return ResponseData<ModifyOrderCompareVO>
	 */
	@PostMapping("/order/modifyorderFee")
	public ResponseData<ModifyOrderCompareVO> modifyOrderFee(@Valid @RequestBody ModifyOrderAppReqVO modifyOrderAppReq, BindingResult bindingResult) {
		log.info("修改前费用计算modifyOrderAppReq=[{}] ", modifyOrderAppReq);
		BindingResultUtil.checkBindingResult(bindingResult);
		// 属性拷贝
		ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
		BeanUtils.copyProperties(modifyOrderAppReq, modifyOrderReq);
		ModifyOrderCompareVO modifyOrderCompareVO = modifyOrderFeeService.getModifyOrderCompareVO(modifyOrderReq);
		return ResponseData.success(modifyOrderCompareVO);
	}
	
	
	/**
	 * 获取换车成本
	 * @param transferReq
	 * @param bindingResult
	 * @return ResponseData<DispatchCarInfoVO>
	 */
	@PostMapping("/order/transferPreFee")
	public ResponseData<DispatchCarInfoVO> getDispatchCarInfoVO(@Valid @RequestBody TransferReq transferReq, BindingResult bindingResult) {
		log.info("换车操作transferReq=[{}] ", transferReq);
		BindingResultUtil.checkBindingResult(bindingResult);
        String memNo = renterMemberService.getRenterNoByOrderNo(transferReq.getOrderNo());
        transferReq.setMemNo(memNo);
		// 属性拷贝
		ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
		BeanUtils.copyProperties(transferReq, modifyOrderReq);
		// 设置为管理后台修改
		modifyOrderReq.setConsoleFlag(true);
		// 设置为换车操作
		modifyOrderReq.setTransferFlag(true);
		DispatchCarInfoVO dispatchCarInfoVO = modifyOrderFeeService.getDispatchCarInfoVO(modifyOrderReq);
		return ResponseData.success(dispatchCarInfoVO);
	}
	
	
	/**
	 * 获取换车记录
	 * @param orderNo
	 * @return ResponseData<List<OrderTransferRecordDTO>>
	 */
	@GetMapping("/order/transferrecord/list")
    public ResponseData<List<OrderTransferRecordDTO>> listTransferRecord(@RequestParam(value="orderNo",required = true) String orderNo) {
		log.info("order/transferrecord/list orderNo=[{}]", orderNo);
		List<OrderTransferRecordDTO> list = modifyOrderConfirmService.listOrderTransferRecordByOrderNo(orderNo);
    	return ResponseData.success(list);
    }
	
	
	/**
	 * 扫码取车
	 * @param modifyOrderScanPickUpVO
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/scan/pickup")
	public ResponseData<?> pickup(@Valid @RequestBody ModifyOrderScanPickUpVO modifyOrderScanPickUpVO, BindingResult bindingResult) {
		log.info("扫码取车/order/scan/pickupmodifyOrderScanPickUpVO=[{}]", modifyOrderScanPickUpVO);
		BindingResultUtil.checkBindingResult(bindingResult);
		modifyOrderScanCodeService.pickUpScanCode(modifyOrderScanPickUpVO);
        return ResponseData.success();
    }
	
	
	/**
	 * 扫码还车（车主确认结算方式）
	 * @param modifyOrderScanCodeVO
	 * @param bindingResult
	 * @return ResponseData
	 */
	@PostMapping("/order/scan/confirm")
	public ResponseData<?> confirmScanCode(@Valid @RequestBody ModifyOrderScanCodeVO modifyOrderScanCodeVO, BindingResult bindingResult) {
		log.info("扫码还车/order/scan/confirm（车主确认结算方式）modifyOrderScanCodeVO=[{}]", modifyOrderScanCodeVO);
		BindingResultUtil.checkBindingResult(bindingResult);
		modifyOrderScanCodeService.confirmScanCode(modifyOrderScanCodeVO);
        return ResponseData.success();
    }
	
	
	/**
	 * 修改订单校验
	 * @param orderNo
	 * @param memNo
	 * @return ResponseData
	 */
	@GetMapping("/order/modify/check")
    public ResponseData<?> modifyCheck(@RequestParam(value="orderNo",required = true) String orderNo, 
    		@RequestParam(value="memNo",required = true) String memNo) {
		log.info("order/modify/check orderNo=[{}],memNo=[{}]", orderNo, memNo);
		modifyOrderCheckService.checkModifyOrderForApp(orderNo, memNo);
    	return ResponseData.success();
    }
	
	
	/**
	 * 获取前端修改次数
	 * @param orderNo
	 * @return ResponseData
	 */
	@GetMapping("/order/modify/applycount/get")
    public ResponseData<?> getApplyCount(@RequestParam(value="orderNo",required = true) String orderNo) {
		log.info("/order/modify/applycount/get orderNo=[{}]", orderNo);
		Integer changeApplyCount = renterOrderChangeApplyService.getRenterOrderChangeApplyAllCountByOrderNo(orderNo);
		changeApplyCount = changeApplyCount == null ? 0:changeApplyCount;
    	return ResponseData.success(changeApplyCount);
    }
	
	
	/**
     * 获取修改前数据
     * @param modifyOrderReq
     * @param bindingResult
     * @return ResponseData
     */
    @PostMapping("/order/beforemodifydata/get")
    public ResponseData<ModifyOrderDTO> getInitModifyOrderDTO(@Valid @RequestBody ModifyOrderReq modifyOrderReq, BindingResult bindingResult) {
        log.info("获取修改前数据 modifyOrderReq=[{}] ", modifyOrderReq);
		BindingResultUtil.checkBindingResult(bindingResult);
		ModifyOrderDTO modifyOrderDTO = modifyOrderExtendService.getInitModifyOrderDTO(modifyOrderReq);
        return ResponseData.success(modifyOrderDTO);
    }
    
    
    /**
	 * 根据订单号获取车牌号
	 * @param orderNo
	 * @return ResponseData<String>
	 */
	@GetMapping("/order/carplatenum/get")
    public ResponseData<String> getCarPlateNum(@RequestParam(value="orderNo",required = true) String orderNo) {
		log.info("/order/carplatenum/get orderNo=[{}]", orderNo);
		String carPlateNum = modifyOrderExtendService.getCarPlateNum(orderNo);
    	return ResponseData.success(carPlateNum);
    }
}
