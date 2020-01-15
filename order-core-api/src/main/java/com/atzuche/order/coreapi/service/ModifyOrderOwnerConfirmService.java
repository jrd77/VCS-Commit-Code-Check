package com.atzuche.order.coreapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atzuche.order.coreapi.entity.dto.ModifyConfirmDTO;
import com.atzuche.order.coreapi.entity.request.ModifyApplyHandleReq;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderChangeApplyNotFindException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderOwnerConfirmService {
	@Autowired
	private ModifyOrderConfirmService modifyOrderConfirmService;
	@Autowired
	private RenterOrderChangeApplyService renterOrderChangeApplyService;
	/**
	 * 同意操作
	 */
	public static final String AGREE_OPERATION = "1";
	/**
	 * 拒绝操作
	 */
	public static final String REFUSE_OPERATION = "0";
	
	/**
	 * 车主操作修改申请
	 * @param modifyApplyHandleReq
	 * @return ResponseData
	 */
	@Transactional(rollbackFor=Exception.class)
	public ResponseData<?> modifyConfirm(ModifyApplyHandleReq modifyApplyHandleReq) {
		log.info("ModifyOrderConfirmService.modifyConfirm modifyApplyHandleReq=[{}]", modifyApplyHandleReq);
		if (modifyApplyHandleReq == null) {
			log.error("ModifyOrderConfirmService.modifyConfirm车主处理修改申请报错参数为空");
			Cat.logError("ModifyOrderConfirmService.modifyConfirm车主处理修改申请报错", new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		ModifyConfirmDTO modifyConfirmDTO = modifyOrderConfirmService.convertToModifyConfirmDTO(modifyApplyHandleReq);
		RenterOrderChangeApplyEntity changeApply = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(modifyConfirmDTO.getRenterOrderNo());
		if (changeApply == null) {
			log.error("ModifyOrderConfirmService.modifyConfirm未找到有效的修改申请记录modifyApplyHandleReq=[{}]", modifyApplyHandleReq);
			Cat.logError("ModifyOrderConfirmService.modifyConfirm车主处理修改申请报错", new ModifyOrderChangeApplyNotFindException());
			throw new ModifyOrderChangeApplyNotFindException();
		}
		modifyConfirmDTO.setOrderNo(changeApply.getOrderNo());
		modifyConfirmDTO.setOwnerOrderNo(changeApply.getOwnerOrderNo());
		if (AGREE_OPERATION.equals(modifyConfirmDTO.getFlag())) {
			// 同意
			modifyOrderConfirmService.agreeModifyOrder(changeApply.getOrderNo(), modifyConfirmDTO.getRenterOrderNo());
		} else if (REFUSE_OPERATION.equals(modifyConfirmDTO.getFlag())) {
			// 拒绝
			modifyOrderConfirmService.refuseModifyOrder(modifyConfirmDTO);
		}
		return ResponseData.success();
	}
}
