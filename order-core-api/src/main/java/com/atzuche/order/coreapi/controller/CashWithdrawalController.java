package com.atzuche.order.coreapi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.MemberOrderDebtDTO;
import com.atzuche.order.commons.entity.dto.SearchCashWithdrawalReqDTO;
import com.atzuche.order.commons.vo.req.AccountOwnerCashExamineReqVO;
import com.atzuche.order.commons.vo.req.Page;
import com.atzuche.order.commons.vo.req.SearchMemberOrderDebtListReqVO;
import com.atzuche.order.commons.vo.req.income.AcctOwnerWithdrawalRuleReqVO;
import com.atzuche.order.commons.vo.res.account.income.AcctOwnerWithdrawalRuleResVO;
import com.atzuche.order.coreapi.entity.vo.OwnerGpsDeductVO;
import com.atzuche.order.coreapi.service.CashWithdrawalService;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.service.RemoteOldSysDebtService;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.wallet.api.DebtDetailVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;

import lombok.extern.slf4j.Slf4j;

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
	private AccountDebtDetailNoTService accountDebtDetailNoTService;
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
     *
     * @param req
     * @return ResponseData<List < AccountOwnerCashExamine>>
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
     *
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
     *
     * @param req
     * @return ResponseData<?>
     */
    @GetMapping("/debt/get")
    public ResponseData<DebtDetailVO> getDebtAmt(@Valid SearchCashWithdrawalReqDTO req, BindingResult bindingResult) {
        log.info("获取用户总欠款 req=[{}]", GsonUtils.toJson(req));
        BindingResultUtil.checkBindingResult(bindingResult);
        DebtDetailVO debtDetailVO = accountDebtService.getTotalNewDebtAndOldDebtAmt(req.getMemNo());
        if (debtDetailVO != null) {
            log.info("getDebtAmt出参=[{}],入参=[{}]", GsonUtils.toJson(debtDetailVO), GsonUtils.toJson(req));
            debtDetailVO.setNoPaySupplementAmt(orderSupplementDetailService.getSumNoPaySupplementAmt(req.getMemNo()));
            //4小时
            Integer sum = cashierRefundApplyNoTService.getCashierRefundApplyByTimeForPreAuthSum(req.getMemNo());
            debtDetailVO.setOrderDebtAmt(debtDetailVO.getOrderDebtAmt().intValue() + Math.abs(sum));
        }

        return ResponseData.success(debtDetailVO);
    }


    @AutoDocMethod(description = "车主提现前置判断接口(判断提现金额是否包含二清)", value = "车主提现前置判断接口", response = AcctOwnerWithdrawalRuleResVO.class)
    @PostMapping("/account/withdraw/pre/rule")
    public ResponseData<AcctOwnerWithdrawalRuleResVO> getAcctOwnerWithdrawalRule(@Valid @RequestBody AcctOwnerWithdrawalRuleReqVO req, BindingResult bindingResult) {
        log.info("提现前判断提现金额是否包含上海二清.param is,req:[{}]", JSON.toJSONString(req));
        BindingResultUtil.checkBindingResult(bindingResult);
        AcctOwnerWithdrawalRuleResVO resVO = cashWithdrawalService.getWithdrawalRule(req);
        log.info("提现前判断提现金额是否包含上海二清.result is,resVO:[{}]", JSON.toJSONString(resVO));
        return ResponseData.success(resVO);
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
	
	@PostMapping("/debt/queryDebtOrderList")
	public ResponseData<?> queryDebtOrderList(@RequestBody SearchMemberOrderDebtListReqVO req){
		List<MemberOrderDebtDTO> memberOrderDebtList = new ArrayList<>();
		List<MemberOrderDebtDTO> memberNewOrderDebtList = accountDebtDetailNoTService.selectMemberOrderDebtList(req);
		List<MemberOrderDebtDTO> memberOldOrderDebtList = remoteOldSysDebtService.selectMemberOrderDebtList(req);
		if(!CollectionUtils.isEmpty(memberNewOrderDebtList)){
			memberOrderDebtList.addAll(memberNewOrderDebtList);
		}
		if(!CollectionUtils.isEmpty(memberOldOrderDebtList)){
			memberOrderDebtList.addAll(memberOldOrderDebtList);
		}
		Page page = new Page();
		page.setTotal(memberOrderDebtList.size());
		page.setList(memberOrderDebtList);
		page.setPageNum(req.getPageNum());
		page.setPageSize(req.getPageSize());
		return ResponseData.success(page);
}

}
