package com.atzuche.order.coreapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atzuche.order.commons.vo.req.ReturnCarReqVO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOwnerAddrOwnerOrderNotFindException;
import com.atzuche.order.open.vo.ModifyOrderScanCodeVO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.rentermem.service.RenterMemberService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderScanCodeService {
	
	@Autowired
	private ModifyOrderService modifyOrderService;
	@Autowired
	private OwnerReturnCarService ownerReturnCarService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private OwnerOrderService ownerOrderService;
	
	
	// 1-按照订单结束时间结算
	//private static final String SCAN_CODE_REVERT_TIME = "1";
	// 2-按照实际还车时间结算
	private static final String SCAN_CODE_REAL_REVERT_TIME = "2";

	/**
	 * 扫码后车主确认还车
	 * @param modifyOrderScanCodeVO
	 */
	@Transactional(rollbackFor=Exception.class)
	public void confirmScanCode(ModifyOrderScanCodeVO modifyOrderScanCodeVO) {
		log.info("confirmScanCode修改订单主逻辑入参modifyOrderScanCodeVO=[{}]", modifyOrderScanCodeVO);
		// 获取修改前有效车主订单信息
		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(modifyOrderScanCodeVO.getOrderNo());
		String ownerMemNo = modifyOrderScanCodeVO.getOwnerMemNo() == null ? "":modifyOrderScanCodeVO.getOwnerMemNo();
		if (ownerOrderEntity == null || !ownerMemNo.equals(ownerOrderEntity.getMemNo())) {
			throw new ModifyOwnerAddrOwnerOrderNotFindException();
		}
		String settleFlag = modifyOrderScanCodeVO.getSettleFlag();
		if (SCAN_CODE_REAL_REVERT_TIME.equals(settleFlag)) {
			// 按照实际还车时间结算
	        ModifyOrderReq modifyOrderReq = new ModifyOrderReq();
	        BeanUtils.copyProperties(modifyOrderScanCodeVO, modifyOrderReq);
	        // 获取租客会员号
	        String memNo = renterMemberService.getRenterNoByOrderNo(modifyOrderScanCodeVO.getOrderNo());
	        modifyOrderReq.setMemNo(memNo);
	        modifyOrderReq.setConsoleFlag(true);
	        modifyOrderReq.setScanCodeFlag(true);
	        modifyOrderReq.setOperator("扫码确认");
	        modifyOrderService.modifyOrder(modifyOrderReq);
		}
		// 调确认还车
		ReturnCarReqVO returnCarReqVO = new ReturnCarReqVO();
		returnCarReqVO.setOrderNo(modifyOrderScanCodeVO.getOrderNo());
		returnCarReqVO.setRevertTime(modifyOrderScanCodeVO.getRevertTime());
		ownerReturnCarService.returnCar(returnCarReqVO);
	}
}
