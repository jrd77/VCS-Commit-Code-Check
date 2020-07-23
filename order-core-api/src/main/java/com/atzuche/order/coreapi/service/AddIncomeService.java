package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.atzuche.order.accountownerincome.service.AccountOwnerIncomeHandleService;
import com.atzuche.order.cashieraccount.service.MemberSecondSettleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.accountownerincome.service.AddIncomeExamineService;
import com.atzuche.order.accountownerincome.service.AddIncomeExcelService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelContextEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.enums.ExamineStatusEnum;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeDetailType;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.AddImportExamineException;
import com.atzuche.order.commons.exceptions.ImportAddIncomeExcelException;
import com.atzuche.order.coreapi.entity.dto.MemberSimpleDTO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.service.RemoteOldSysDebtService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountOldDebtReqVO;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.autoyol.member.detail.vo.res.MemberCoreInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddIncomeService {

	@Autowired
	private AddIncomeExcelService addIncomeExcelService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private OwnerOrderService ownerOrderService;
	@Autowired
	private OwnerMemberService ownerMemberService;
	@Autowired
	private MemProxyService memProxyService;
	@Autowired
	private AddIncomeExamineService addIncomeExamineService;
	@Autowired
	private AccountDebtService accountDebtService;
	@Autowired
	private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;
	@Autowired
	private RemoteOldSysDebtService remoteOldSysDebtService;
    @Autowired
    private MemberSecondSettleService memberSecondSettleService;
    @Autowired
    private AccountOwnerIncomeHandleService accountOwnerIncomeHandleService;
	
	private static final Integer RENTER_MEM_TYPE = 0;
	
	/**
	 * 保存追加收益
	 * @param addIncomeImportDTO
	 */
	public void saveAddIncomeExcel(AddIncomeImportDTO addIncomeImportDTO) {
		List<AddIncomeExcelContextEntity> list = addIncomeImportDTO.getContentList();
		for (int i=0;i<list.size();i++) {
			AddIncomeExcelContextEntity context = list.get(i);
			String orderNo = context.getOrderNo();
			Integer memNo = context.getMemNo();
			Long mobile = context.getMobile();
			Integer memType = context.getMemType();
			MemberSimpleDTO memberSimpleDTO = null;
			if (StringUtils.isNotBlank(orderNo)) {
				memberSimpleDTO = getMemberSimpleDTOByOrderNo(orderNo, memType, i);
			} else if (memNo != null) {
				memberSimpleDTO = getMemberSimpleDTOByMemNo(memNo, i);
			} else if (mobile != null) {
				memberSimpleDTO = getMemberSimpleDTOByMobile(mobile, i);
			}
			if (memberSimpleDTO != null) {
				context.setMemNo(memberSimpleDTO.getMemNo());
				context.setMobile(memberSimpleDTO.getMobile());
				context.setName(memberSimpleDTO.getName());
			}
		}
		addIncomeExcelService.saveAddIncomeExcel(addIncomeImportDTO);
	}
	
	
	/**
	 * 根据订单号获取会员信息
	 * @param orderNo
	 * @param memType
	 * @param index
	 * @return MemberSimpleDTO
	 */
	public MemberSimpleDTO getMemberSimpleDTOByOrderNo(String orderNo, Integer memType, int index) {
		MemberSimpleDTO memberSimpleDTO = null;
		if (RENTER_MEM_TYPE.equals(memType)) {
			RenterMemberEntity renter = renterMemberService.getRenterMemberByOrderNo(orderNo);
			if (renter == null) {
				throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,订单号不正确！");
			}
			memberSimpleDTO = new MemberSimpleDTO(Integer.valueOf(renter.getMemNo()), renter.getRealName(), Long.valueOf(renter.getPhone()));
		} else {
			// 获取修改前有效车主订单信息
			OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
			if (ownerOrderEntity == null) {
				throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,订单号不正确！");
			}
			OwnerMemberDTO owner = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo(), false);
			memberSimpleDTO = new MemberSimpleDTO(Integer.valueOf(owner.getMemNo()), owner.getRealName(), Long.valueOf(owner.getPhone()));
		}
		return memberSimpleDTO;
	}
	
	
	/**
	 * 根据会员号获取会员信息
	 * @param memNo
	 * @param index
	 * @return MemberSimpleDTO
	 */
	public MemberSimpleDTO getMemberSimpleDTOByMemNo(Integer memNo, int index) {
		MemberCoreInfo memberCoreInfo = memProxyService.getMemberCoreInfoByMemNo(memNo);
		if (memberCoreInfo == null) {
			throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,会员号不正确！");
		}
		return new MemberSimpleDTO(memNo, memberCoreInfo.getRealName(), memberCoreInfo.getMobile());
	}
	
	
	/**
	 * 根据手机号获取会员信息
	 * @param mobile
	 * @param index
	 * @return MemberSimpleDTO
	 */
	public MemberSimpleDTO getMemberSimpleDTOByMobile(Long mobile, int index) {
		Integer memNo = memProxyService.getMemNoByMoileEx(mobile);
		if (memNo == null) {
			throw new ImportAddIncomeExcelException("第"+(index+1)+"行数据,手机号不正确！");
		}
		return getMemberSimpleDTOByMemNo(memNo, index);
	}
	
	
	/**
	 * 追加收益审核操作
	 * @param req
	 */
	@Transactional(rollbackFor=Exception.class)
	public void examineOpt(AddIncomeExamineOptDTO req) {
		if (req == null) {
			return;
		}
		int flag = req.getFlag() == null ? 0:req.getFlag();
		if (flag == ExamineStatusEnum.UNDER_REVIEW_EXCEPTION.getCode()) {
			// 审核中
			req.setFlag(req.getWaitFlag());
		}
		// 更新状态
		int updateFlag = addIncomeExamineService.updateExamine(req);
		if (updateFlag <= 0) {
			throw new AddImportExamineException("请勿重复操作。");
		}
		// 记录审核日志
		addIncomeExamineService.saveAddIncomeExamineLog(req);
		// 审核通过
		passExamine(req);
	}

    /**
     * 追加收益审核通过
     *
     * @param req 请求参数
     */
    public void passExamine(AddIncomeExamineOptDTO req) {
        if (req == null || req.getFlag() == null || req.getFlag() != ExamineStatusEnum.APPROVED.getCode()) {
            return;
        }
        // 根据id获取追加的收益
        AddIncomeExamine addIncomeExamine = addIncomeExamineService.getAddIncomeExamine(req.getId());
        if (addIncomeExamine == null) {
            log.info("审核通过 passExamine AddIncomeExamine获取为空，req=[{}]", req);
            throw new AddImportExamineException("审核记录不存在");
        }
        int amt = addIncomeExamine.getAmt() == null ? 0 : addIncomeExamine.getAmt();
        if (amt == 0) {
            log.info("审核通过 passExamine 该条记录收益为0，req=[{}]", req);
            return;
        }
        if (addIncomeExamine.getMemNo() == null) {
            log.info("审核通过 passExamine 该条记录会员号为空，req=[{}]", req);
            throw new AddImportExamineException("该条记录会员号为空");
        }

        boolean isSecondFlag =
                memberSecondSettleService.judgeIsSecond(addIncomeExamine.getMemNo(), addIncomeExamine.getOrderNo());
        if (amt > 0) {
            greaterThanZero(addIncomeExamine, req.getOperator(), isSecondFlag);
        } else {
            // 优先使用先有收益扣除,剩余进入欠款
            int surplusAddIncomeAmt = incomeCompensate(addIncomeExamine, isSecondFlag);
            // 产生欠款
            lessThanZero(surplusAddIncomeAmt, addIncomeExamine.getOrderNo(), addIncomeExamine.getMemNo().toString());
        }
    }
	
	
	/**
	 * 追加的收益大于零处理逻辑
	 * @param addIncomeExamine
	 * @param operator
	 */
	public void greaterThanZero(AddIncomeExamine addIncomeExamine, String operator, boolean isSecondFlag) {
		int amt = addIncomeExamine.getAmt() == null ? 0:addIncomeExamine.getAmt();
		if (amt <= 0) {
			return;
		}
		// 抵扣新欠款
		// 查询历史总欠款
        int historyDebtAmt = accountDebtService.getAccountDebtNumByMemNo(addIncomeExamine.getMemNo().toString());
        if(historyDebtAmt < 0){
        	AccountDeductDebtReqVO debtReq = new AccountDeductDebtReqVO(addIncomeExamine.getMemNo().toString(), amt, RenterCashCodeEnum.ADD_INCOME_OWNER_INCOME_TO_HISTORY_AMT.getCashNo(), RenterCashCodeEnum.ADD_INCOME_OWNER_INCOME_TO_HISTORY_AMT.getTxt(), operator);
    		debtReq.setUniqueNo(String.valueOf(addIncomeExamine.getId()));
    		int realDebtAmt = accountDebtService.deductDebt(debtReq);
    		amt = amt - realDebtAmt;
        }
		int oldRealDebtAmt = 0;
		if (amt > 0) {
			// 继续抵扣老欠款
			List<AccountOldDebtReqVO> oldDebtList = new ArrayList<AccountOldDebtReqVO>();
    		AccountOldDebtReqVO accountOldDebtReqVO = new AccountOldDebtReqVO();
    		accountOldDebtReqVO.setMemNo(addIncomeExamine.getMemNo().toString());
    		accountOldDebtReqVO.setSurplusAmt(amt);
    		accountOldDebtReqVO.setCahsCodeEnum(RenterCashCodeEnum.ADD_INCOME_OWNER_INCOME_TO_OLD_HISTORY_AMT);
    		oldDebtList.add(accountOldDebtReqVO);
    		List<AccountOldDebtResVO> debtResList = accountDebtService.deductOldDebt(oldDebtList);
			oldRealDebtAmt = (debtResList == null || debtResList.isEmpty() || debtResList.get(0).getRealDebtAmt() == null) ? 0:debtResList.get(0).getRealDebtAmt();
			if (oldRealDebtAmt > 0) {
				amt = amt - oldRealDebtAmt;
			}
		}
		if (amt > 0) {
			// 更新会员收益
			AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail = new AccountOwnerIncomeDetailEntity();
			accountOwnerIncomeDetail.setMemNo(addIncomeExamine.getMemNo().toString());
			accountOwnerIncomeDetail.setAmt(amt);
			accountOwnerIncomeDetail.setDetail("追加收益审核通过");
	        accountOwnerIncomeDetail.setOrderNo(addIncomeExamine.getOrderNo());
	        accountOwnerIncomeDetail.setTime(LocalDateTime.now());
	        accountOwnerIncomeDetail.setType(AccountOwnerIncomeDetailType.INCOME.getType());
			accountOwnerIncomeDetail.setIncomeExamineId(addIncomeExamine.getId());
			accountOwnerIncomeDetail.setCostCode(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME.getCashNo());
			accountOwnerIncomeDetail.setCostDetail(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME.getTxt());
			accountOwnerIncomeNoTService.updateTotalIncomeAndSaveDetail(accountOwnerIncomeDetail, isSecondFlag);
		}
		if (oldRealDebtAmt > 0) {
			// 调远程抵扣
			remoteOldSysDebtService.deductBalance(addIncomeExamine.getMemNo().toString(), oldRealDebtAmt);
		}
	}


    /**
     * 小于零逻辑，产生欠款
     *
     * @param surplusAddIncomeAmt 剩余追加收益金额
     * @param orderNo             订单号
     * @param memNo               会员号
     */
    public void lessThanZero(int surplusAddIncomeAmt, String orderNo, String memNo) {
        if (surplusAddIncomeAmt >= 0) {
            return;
        }
        AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
        accountInsertDebt.setAmt(surplusAddIncomeAmt);
        accountInsertDebt.setMemNo(memNo);
        accountInsertDebt.setOrderNo(orderNo);
        accountInsertDebt.setSourceCode(RenterCashCodeEnum.ADD_INCOME_PRODUCE_DEBUT.getCashNo());
        accountInsertDebt.setSourceDetail(RenterCashCodeEnum.ADD_INCOME_PRODUCE_DEBUT.getTxt());
        accountInsertDebt.setType(2);
        accountDebtService.insertDebt(accountInsertDebt);
    }

    /**
     * 追加收益为负值时,优先使用现有收益抵充
     *
     * @param addIncomeExamine 追加收益信息
     * @return int 剩余追加收益金额
     */
    public int incomeCompensate(AddIncomeExamine addIncomeExamine, boolean isSecondFlag) {
        int surplusAddIncomeAmt = addIncomeExamine.getAmt() == null ? 0 : addIncomeExamine.getAmt();
        if (surplusAddIncomeAmt >= 0) {
            return surplusAddIncomeAmt;
        }
        AccountOwnerIncomeDetailEntity incomeDetail = new AccountOwnerIncomeDetailEntity();
        incomeDetail.setOrderNo(addIncomeExamine.getOrderNo());
        incomeDetail.setMemNo(addIncomeExamine.getMemNo().toString());
        incomeDetail.setDetail("追加收益审核通过(负值抵充)");
        incomeDetail.setTime(LocalDateTime.now());
        incomeDetail.setType(AccountOwnerIncomeDetailType.INCOME.getType());
        incomeDetail.setIncomeExamineId(addIncomeExamine.getId());

        // 老收益抵充处理
        surplusAddIncomeAmt = accountOwnerIncomeHandleService.oldIncomeCompensateHandle(incomeDetail, surplusAddIncomeAmt);
        // 新收益抵充处理
        surplusAddIncomeAmt = accountOwnerIncomeHandleService.newIncomeCompensateHandle(incomeDetail,
                surplusAddIncomeAmt);
        // 二清收益抵充处理
        if (isSecondFlag) {
            surplusAddIncomeAmt = accountOwnerIncomeHandleService.secondaryIncomeCompensateHandle(incomeDetail,
                    surplusAddIncomeAmt);
        }
        return surplusAddIncomeAmt;
    }


}
