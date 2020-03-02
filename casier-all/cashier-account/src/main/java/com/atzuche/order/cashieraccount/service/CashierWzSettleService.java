package com.atzuche.order.cashieraccount.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostSettleEntity;
import com.atzuche.order.accountrenterclaim.service.notservice.AccountRenterClaimCostSettleNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.exception.RenterWZDepositCostException;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositCostService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostSettleDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.accountrenterwzdepost.vo.req.AccountRenterWzCostDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.DetainRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.commons.enums.SysOrHandEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.CashierRefundApplyStatus;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author jhuang
 *
 */
@Service
@Slf4j
public class CashierWzSettleService {
    @Autowired 
    private AccountRenterClaimCostSettleNoTService accountRenterClaimCostSettleNoTService;
    @Autowired 
    private AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired 
    private AccountDebtService accountDebtService;
    @Autowired 
    private CashierRefundApplyNoTService cashierRefundApplyNoTService;
    
    @Autowired 
    private AccountRenterWzDepositCostService accountRenterWzDepositCostService;
    @Autowired 
    private AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired 
    private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;
    @Autowired 
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    private AccountRenterWzDepositCostSettleDetailNoTService accountRenterWzDepositCostSettleDetailNoTService;
    @Autowired
    private AccountRenterWzDepositCostNoTService accountRenterWzDepositCostNoTService;
    
    /**
     * 根据订单号查询订单 理赔信息
     * @param orderNo
     */
    public boolean getOrderClaim(String orderNo) {
        boolean result = false;
        AccountRenterClaimCostSettleEntity accountRenterClaimCostSettle = accountRenterClaimCostSettleNoTService.getRenterClaimCost(orderNo);
        if(Objects.nonNull(accountRenterClaimCostSettle) && Objects.nonNull(accountRenterClaimCostSettle.getId())){
            result = true;
        }
        return result;
    }
    
    public void insertAccountRenterCostSettleDetail(AccountRenterCostSettleDetailEntity entity) {
        if(Objects.nonNull(entity)){
            accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(entity);
        }
    }
    
    /**
	     * 车俩结算 租客费用明细落库
	* @param accountRenterCostSettleDetails
	*/
	public void insertAccountRenterCostSettleDetails(List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails) {
		if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
			accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
		}
	}
	
    /**
     	* 结算时候，应付金额大于实付金额，存在欠款，车辆押金抵扣
     */
    public void deductWzDepositToWzCost(DeductDepositToRentCostReqVO vo) {
        // 1 记录押金流水记录
    	DetainRenterWZDepositReqVO detainRenterDepositReqVO = new DetainRenterWZDepositReqVO();
        BeanUtils.copyProperties(vo,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_WZ_COST);
        int renterDepositDetailId = accountRenterWzDepositService.detainRenterWzDeposit(detainRenterDepositReqVO);
        log.info("(出账)更新违章押金和违章押金资金进出明细表，detainRenterDepositReqVO params=[{}],renterDepositDetailId=[{}]",GsonUtils.toJson(detainRenterDepositReqVO),renterDepositDetailId);
      
        
        //2 记录 更新 租客户头 租车费用
        AccountRenterWzCostDetailReqVO accountRenterCostChangeReqVO = new AccountRenterWzCostDetailReqVO();
        BeanUtils.copyProperties(detainRenterDepositReqVO,accountRenterCostChangeReqVO);
        accountRenterCostChangeReqVO.setUniqueNo(String.valueOf(renterDepositDetailId));
        accountRenterCostChangeReqVO.setAmt(Math.abs(vo.getAmt()));
        accountRenterCostChangeReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT);
        int wzCostDetailId = accountRenterWzDepositCostService.deductDepositToWzCostDetail(accountRenterCostChangeReqVO);
        log.info("(入账)新增违章押金费用COST资金进出明细表，accountRenterCostChangeReqVO params=[{}]",GsonUtils.toJson(accountRenterCostChangeReqVO));
        
        
        //3 更新违章押金流水 UniqueNo 字段
        accountRenterWzDepositService.updateRenterDepositUniqueNo(String.valueOf(wzCostDetailId),renterDepositDetailId);
        log.info("(账户ID关联)rentCostDetailId params=[{}],renterDepositDetailId params=[{}] ",wzCostDetailId,renterDepositDetailId);
        
        //wzTotalCost-todo
        // 4 租客结算费用明细 落库  account_renter_wz_deposit_cost_settle_detail 表结构不同。。 先记入费用总表
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(vo,entity);
        entity.setAmt(Math.abs(vo.getAmt()));
        entity.setCostCode(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_WZ_COST.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_WZ_COST.getTxt());
        entity.setUniqueNo(String.valueOf(renterDepositDetailId));
        entity.setType(10); //默认10
        insertAccountRenterCostSettleDetail(entity);
        log.info("(记录租客费用总账明细)新增租客COST明细表(抵扣费用)，accountRenterCostSettleDetailEntity params=[{}]",GsonUtils.toJson(entity));
    }
    
    /**
     * 7）违章押金抵扣历史欠款
     */
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductWZDebt(CashierDeductDebtReqVO cashierDeductDebtReqVO){
        Assert.notNull(cashierDeductDebtReqVO, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReqVO.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReqVO.getMemNo());
        if(debtAmt>=0){
            return null;
        }
        //2 抵扣
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReqVO,accountDeductDebt);
        accountDeductDebt.setSourceCode(cashierDeductDebtReqVO.getRenterCashCodeEnum().getCashNo());
        accountDeductDebt.setSourceDetail(cashierDeductDebtReqVO.getRenterCashCodeEnum().getTxt());
        //公共抵扣方法。
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        log.info("抵扣历史欠款,params=[{}],debtedAmt=[{}]",GsonUtils.toJson(accountDeductDebt),debtedAmt);
        
        //3 记录结算费用抵扣记录
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail = new PayedOrderRenterDepositWZDetailReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReqVO,payedOrderRenterWZDepositDetail);
        payedOrderRenterWZDepositDetail.setAmt(-debtedAmt);
        int id = accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
        log.info("(动账)更新违章押金和新增违章押金资金明细, params=[{}]",GsonUtils.toJson(payedOrderRenterWZDepositDetail));
        return new CashierDeductDebtResVO(cashierDeductDebtReqVO, debtedAmt,id);
    }
    
    
    @Transactional(rollbackFor=Exception.class)
    public int refundWzDeposit(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        
        //2 记录费用平账
        DetainRenterWZDepositReqVO detainRenterDepositReqVO = new DetainRenterWZDepositReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setUniqueNo(id.toString());
        
        //2 押金账户资金转移接口
        int depositDetailId = accountRenterWzDepositService.detainRenterWzDeposit(detainRenterDepositReqVO);
        log.info("(退款出账)更新违章押金和违章押金资金进出明细表，detainRenterDepositReqVO params=[{}],depositDetailId=[{}]",GsonUtils.toJson(detainRenterDepositReqVO),depositDetailId);
        
        // 3 更新押金结算状态
        accountRenterWzDepositService.updateOrderDepositSettle(detainRenterDepositReqVO);
        return depositDetailId;
    }
    
    //仅仅新增记录。
    public int refundWzDepositPreAuth(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        return id;
    }
    
    
    /////从CashierService代码
    /**
     * 违章押金预授权退款方法
     * @param rentSurplusWzDepositAmt
     * @param cashierEntity
     * @param cashierRefundApply
     * @param cashCode
     */
    public int refundWzDepositPreAuthAll(int rentSurplusWzDepositAmt,CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply,RenterCashCodeEnum cashCode) {
		//是否存在预授权完成操作
		boolean isExistsPreAuthFinish = (cashierEntity.getPayAmt() - Math.abs(rentSurplusWzDepositAmt) > 0);
		int id = 0;
		
		//预授权  退款就是解冻，余下的就是预授权完成
		//预授权解冻，金额不允许为0
		//考虑全额预授权解冻
		if(Math.abs(rentSurplusWzDepositAmt) != 0){
			//超出金额的做限制,退款的超出做全额解冻。
			int refundAmt = -rentSurplusWzDepositAmt;
			if(Math.abs(refundAmt) > cashierEntity.getPayAmt()) {
				refundAmt = cashierEntity.getPayAmt();
			}
			
			cashierRefundApply.setPayType(DataPayTypeConstant.PRE_VOID); //解冻
			cashierRefundApply.setAmt(refundAmt);
		    cashierRefundApply.setRenterCashCodeEnum(cashCode);
		    cashierRefundApply.setRemake(cashCode.getTxt());
		    cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
		    cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		    cashierRefundApply.setQn(cashierEntity.getQn());
		    cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);
		    
		    //需要在预授权完成之后再做解冻操作
		    if(isExistsPreAuthFinish) {
		    	cashierRefundApply.setStatus(CashierRefundApplyStatus.STOP_FOR_REFUND.getCode());
		    }
		    
		    id = this.refundWzDeposit(cashierRefundApply);
		}
		
		//添加预授权完成记录 
		//添加预授权完成记录,金额不允许为0 
		//考虑全额预授权完成
		if(isExistsPreAuthFinish) {
		    cashierRefundApply.setPayType(DataPayTypeConstant.PRE_FINISH); //预授权完成
			cashierRefundApply.setAmt( cashierEntity.getPayAmt() - Math.abs(rentSurplusWzDepositAmt) );
			cashierRefundApply.setRenterCashCodeEnum(cashCode);
		    cashierRefundApply.setRemake(cashCode.getTxt());
		    cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
		    cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		    cashierRefundApply.setQn(cashierEntity.getQn());
		    cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);
		    id = this.refundWzDepositPreAuth(cashierRefundApply);  //仅仅提交预授权完成记录 
		}
		return id;
	}
    
    /**
     * 违章押金的消费退货处理
     * @param rentSurplusWzDepositAmt
     * @param cashierEntity
     * @param cashierRefundApply
     * @param cashCode
     */
	public int refundWzDepositPurchase(int rentSurplusWzDepositAmt,
			CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply,RenterCashCodeEnum cashCode) {
        //缺少会员号,已经通过beanutils方式赋值。
		cashierRefundApply.setPayType(DataPayTypeConstant.PUR_RETURN); //退货
		cashierRefundApply.setAmt(-rentSurplusWzDepositAmt);
		cashierRefundApply.setRenterCashCodeEnum(cashCode);
		cashierRefundApply.setRemake(cashCode.getTxt());
		cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
		cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		cashierRefundApply.setQn(cashierEntity.getQn());
		cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);
		return this.refundWzDeposit(cashierRefundApply);
	}
	
	
    
    /**
     	* 用户订单结算 产生历史欠款
     */
    public int createWzDebt(AccountInsertDebtReqVO accountInsertDebt){
       return accountDebtService.insertDebt(accountInsertDebt);
    }
    
    /**
     	* 查询实付 违章押金金额()
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getSurplusWZDepositCostAmt(String orderNo, String renterMemNo) {
        return accountRenterWzDepositDetailNoTService.getSurplusWZDepositCostAmt(orderNo,renterMemNo);
    }
    
    /**
     * 计算租客 租车费用  平台补贴费用  车主补贴费用 手续费 基础保障费用 等 并落库
     * @param account_renter_wz_deposit_cost
     */
    public AccountRenterWzDepositCostEntity updateWzRentSettleCost(String orderNo,String renterMemNo,int wzTotalAmt) {
    	//account_renter_wz_deposit
    	AccountRenterWzDepositEntity depositEntity = accountRenterWzDepositNoTService.getAccountRenterWZDeposit(orderNo, renterMemNo);
    	if(Objects.isNull(depositEntity) || Objects.isNull(depositEntity.getId())){
            throw new RenterWZDepositCostException() ;
        }

    	AccountRenterWzDepositCostEntity entity = accountRenterWzDepositCostNoTService.queryWzDeposit(orderNo,renterMemNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getId())){
//            throw new AccountRenterRentCostSettleException() ;
        	entity = new AccountRenterWzDepositCostEntity();
        	entity.setOrderNo(orderNo);
        	entity.setMemNo(renterMemNo);
        	entity.setYingfuAmt(wzTotalAmt);
        	entity.setShifuAmt(depositEntity.getShishouDeposit()); //需要获取实付金额
        	entity.setDebtAmt(wzTotalAmt-depositEntity.getShishouDeposit()>0?(wzTotalAmt-depositEntity.getShishouDeposit()):0); //当前默认为0，后续产生欠款的时候，需要回写该字段。
        	entity.setIsDelete(0);

        	//新增记录
        	accountRenterWzDepositCostNoTService.insertAccountRenterWzDepositCost(entity);
        }else {
        	//修改记录
        	entity.setYingfuAmt(wzTotalAmt);
        	if(!(entity.getShifuAmt() != null && entity.getShifuAmt().intValue() != 0)) {
        		//没有数据的时候才赋值。
        		entity.setShifuAmt(depositEntity.getShishouDeposit()); //需要获取实付金额
        		entity.setDebtAmt(wzTotalAmt-depositEntity.getShishouDeposit()>0?(wzTotalAmt-depositEntity.getShishouDeposit()):0); //当前默认为0，后续产生欠款的时候，需要回写该字段。
        	}else { //存在的情况下，重新计算欠款。
        		entity.setDebtAmt(wzTotalAmt-entity.getShifuAmt()>0?(wzTotalAmt-entity.getShifuAmt()):0); //当前默认为0，后续产生欠款的时候，需要回写该字段。
        	}
        	accountRenterWzDepositCostNoTService.updateAccountRenterWzDepositCost(entity);
        }
        return entity;


    }
    
    public void insertAccountRenterWzDepoistCostSettleDetails(List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetails) {
        if(!CollectionUtils.isEmpty(accountRenterWzDepositCostSettleDetails)){
        	accountRenterWzDepositCostSettleDetailNoTService.insertAccountRenterWzDepositCostSettleDetails(accountRenterWzDepositCostSettleDetails);
        }

    }
    

    
    // ---------------------------------------------------------------------------------------------
    
}
