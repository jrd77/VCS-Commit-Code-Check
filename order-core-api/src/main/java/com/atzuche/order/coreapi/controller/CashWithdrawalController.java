package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.MemberDebtListReqDTO;
import com.atzuche.order.commons.entity.dto.MemberDebtListResDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.coreapi.entity.vo.OwnerGpsDeductVO;
import com.atzuche.order.coreapi.service.CashWithdrawalService;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.service.RemoteOldSysDebtService;
import com.atzuche.order.wallet.api.DebtDetailVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.utils.Page;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class CashWithdrawalController {
	
	@Autowired
	private CashWithdrawalService cashWithdrawalService;
	@Autowired
	private AccountDebtService accountDebtService;
	@Autowired
    private OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
    private CashierRefundApplyNoTService cashierRefundApplyNoTService;

	@Autowired
	private RemoteOldSysDebtService remoteOldSysDebtService;

	/**
	 * 提现
	 * @param req
	 * @param bindingResult
	 * @return ResponseData<?>
	 */
	@PostMapping("/account/withdraw")
	public ResponseData<?> cashWithdraw(@Valid @RequestBody AccountOwnerCashExamineReqVO req, BindingResult bindingResult) {
		log.info("提现AccountOwnerCashExamineReqVO=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		cashWithdrawalService.cashWithdrawal(req);
        return ResponseData.success();
    }
	
	
	/**
	 * 获取提现记录
	 * @param req
	 * @return ResponseData<List<AccountOwnerCashExamine>>
	 */
	@GetMapping("/account/withdraw/list")
    public ResponseData<List<AccountOwnerCashExamine>> listCashWithdrawal(@Valid SearchCashWithdrawalReqDTO req, BindingResult bindingResult) {
		log.info("获取提现记录 req=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		List<AccountOwnerCashExamine> list = cashWithdrawalService.listCashWithdrawal(req);
    	return ResponseData.success(list);
    }
	
	
	/**
	 * 获取可提现余额
	 * @param req
	 * @return ResponseData<?>
	 */
	@GetMapping("/account/withdraw/getbalance")
    public ResponseData<?> getBalance(@Valid SearchCashWithdrawalReqDTO req, BindingResult bindingResult) {
		log.info("获取可提现余额 req=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		Integer balance = cashWithdrawalService.getBalance(req);
    	return ResponseData.success(balance);
    }
	
	
	/**
	 * 获取用户总欠款
	 * @param req
	 * @return ResponseData<?>
	 */
	@GetMapping("/debt/get")
    public ResponseData<DebtDetailVO> getDebtAmt(@Valid SearchCashWithdrawalReqDTO req, BindingResult bindingResult) {
		log.info("获取用户总欠款 req=[{}]", GsonUtils.toJson(req));
		BindingResultUtil.checkBindingResult(bindingResult);
		DebtDetailVO debtDetailVO = accountDebtService.getTotalNewDebtAndOldDebtAmt(req.getMemNo());
		if(debtDetailVO != null) {
			log.info("getDebtAmt出参=[{}],入参=[{}]",GsonUtils.toJson(debtDetailVO),GsonUtils.toJson(req));
			debtDetailVO.setNoPaySupplementAmt(orderSupplementDetailService.getSumNoPaySupplementAmt(req.getMemNo()));
			 //4小时
			Integer sum = cashierRefundApplyNoTService.getCashierRefundApplyByTimeForPreAuthSum(req.getMemNo());
			debtDetailVO.setOrderDebtAmt(debtDetailVO.getOrderDebtAmt().intValue() + Math.abs(sum));
		}

    	return ResponseData.success(debtDetailVO);
    }
	@PostMapping("/debt/queryList")
	public ResponseData<Page> queryList(@Valid @RequestBody MemberDebtListReqDTO req, BindingResult bindingResult) {
		log.info("查询会员欠款入参[{}]",req);
		BindingResultUtil.checkBindingResult(bindingResult);
		Page page = remoteOldSysDebtService.queryList(req);
		log.info("获取欠款用户出参page=[{}]",page);
		List<MemberDebtListReqDTO> list = page.getList();
		String jsonString = JSON.toJSONString(list);
		List<MemberDebtListReqDTO> memberDebtListReqDTOS = JSONArray.parseArray(jsonString, MemberDebtListReqDTO.class);
		log.info("获取欠款用户出参list=[{}]",list);
		if(CollectionUtils.isNotEmpty(list)){
			List<MemberDebtListResDTO> memberDebtListResDTOList = new ArrayList<>();
			for (MemberDebtListReqDTO memberDebtListReqDTO : memberDebtListReqDTOS) {
				MemberDebtListResDTO memberDebtListResDTO = new MemberDebtListResDTO();
				DebtDetailVO debtDetailVO = accountDebtService.getTotalNewDebtAndOldDebtAmt(memberDebtListReqDTO.getMemNo());
				if(debtDetailVO != null) {
					debtDetailVO.setNoPaySupplementAmt(orderSupplementDetailService.getSumNoPaySupplementAmt(memberDebtListReqDTO.getMemNo()));
					//4小时
					Integer sum = cashierRefundApplyNoTService.getCashierRefundApplyByTimeForPreAuthSum(memberDebtListReqDTO.getMemNo());
					debtDetailVO.setOrderDebtAmt(debtDetailVO.getOrderDebtAmt().intValue() + Math.abs(sum));
				}
				memberDebtListResDTO.setMemNo(memberDebtListReqDTO.getMemNo());
				memberDebtListResDTO.setMobile(memberDebtListReqDTO.getMobile());
				memberDebtListResDTO.setRealName(memberDebtListReqDTO.getRealName());
				memberDebtListResDTO.setHistoryDebtAmt(debtDetailVO.getOrderDebtAmt());
				memberDebtListResDTO.setNoPaySupplementAmt(debtDetailVO.getNoPaySupplementAmt());
				memberDebtListResDTO.setOrderDebtAmt(debtDetailVO.getOrderDebtAmt());
				memberDebtListResDTOList.add(memberDebtListResDTO);
			}
			page.setList(memberDebtListResDTOList);
		}
		log.info("查询会员欠款出参[{}]",GsonUtils.toJson(page));
		return ResponseData.success(page);
	}


	/**
	 * 车主车载押金抵扣记录
	 * @param memNo
	 * @param carNo
	 * @return ResponseData<List<AccountOwnerCashExamine>>
	 */
	@GetMapping("/owner/gpsdeduct/list")
    public ResponseData<List<OwnerGpsDeductVO>> listCashWithdrawal(@RequestParam(value="memNo",required = true) String memNo, 
    		@RequestParam(value="carNo",required = true) Integer carNo) {
		log.info("获取车主车载押金抵扣记录 memNo=[{}],carNo=[{}]", memNo,carNo);
		List<OwnerGpsDeductVO> list = cashWithdrawalService.listOwnerGpsDeduct(memNo, carNo);
    	return ResponseData.success(list);
    } 
}
