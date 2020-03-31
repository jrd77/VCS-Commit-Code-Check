package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.AdminOrderService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.OrderTransferRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.atzuche.order.admin.service.ModificationOrderService;
import com.atzuche.order.admin.vo.req.order.ModificationOrderRequestVO;
import com.atzuche.order.admin.vo.resp.order.ModificationOrderListResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class AdminModificationOrderController {

    private static final Logger logger = LoggerFactory.getLogger(AdminModificationOrderController.class);
    @Autowired
    private ModificationOrderService modificationOrderService;

    @Autowired
	private AdminOrderService adminOrderService;
    
	@AutoDocMethod(description = "订单修改信息列表", value = "订单修改信息列表", response = ModificationOrderListResponseVO.class)
	@RequestMapping(value="modifacion/infomation/list",method = RequestMethod.POST)
	public ResponseData queryModifyList(@RequestBody ModificationOrderRequestVO modificationOrderRequestVO, BindingResult bindingResult) {
		logger.info("queryModifyList param is [{}]",modificationOrderRequestVO);
		BindingResultUtil.checkBindingResult(bindingResult);
		ModificationOrderListResponseVO respVo = modificationOrderService.queryModifyList(modificationOrderRequestVO);
		return ResponseData.success(respVo);

	}
	@AutoDocMethod(description = "订单换车记录列表", value = "订单换车记录列表", response = TransRecord.class)
	@RequestMapping(value="transrecord/list",method = RequestMethod.GET)
	public ResponseData<TransRecord>  listTransferRecord(@RequestParam(value="orderNo",required = true) String orderNo){
		logger.info("listTransferRecord param is [{}]",orderNo);
		List<OrderTransferRecordDTO> orderTransferRecordDTOList =  adminOrderService.listTransferRecord(orderNo);
		return ResponseData.success(new TransRecord(orderTransferRecordDTOList));
	}

	public static class TransRecord {
		private List<OrderTransferRecordDTO> list = new ArrayList<>();

		public List<OrderTransferRecordDTO> getList() {
			return list;
		}

		public void setList(List<OrderTransferRecordDTO> list) {
			this.list = list;
		}

		public TransRecord(List<OrderTransferRecordDTO> list) {
			this.list = list;
		}
	}




}
