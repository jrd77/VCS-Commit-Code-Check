package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.OrderTransferRecordDTO;
import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.atzuche.order.rentermem.service.RenterMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.open.vo.ModifyOrderAppReqVO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.open.vo.request.TransferReq;
import com.atzuche.order.open.vo.ModifyOrderCompareVO;
import com.atzuche.order.coreapi.service.ModifyOrderConfirmService;
import com.atzuche.order.coreapi.service.ModifyOrderFeeService;
import com.atzuche.order.coreapi.service.ModifyOrderOwnerConfirmService;
import com.atzuche.order.coreapi.service.ModifyOrderService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;

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
}
