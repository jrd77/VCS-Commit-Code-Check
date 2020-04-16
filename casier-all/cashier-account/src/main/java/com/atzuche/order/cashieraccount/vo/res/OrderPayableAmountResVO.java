package com.atzuche.order.cashieraccount.vo.res;

import java.util.ArrayList;
import java.util.List;

import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.autoyol.autopay.gateway.vo.res.PayInfoVo;
import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.ToString;

/**
 * 个人代付信息
 * @author haibao.yan
 */
@ToString
//@Data
public class OrderPayableAmountResVO {

    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 主订单号
     */
    @AutoDocProperty("主订单号")
    private String orderNo;
    /**
     * 总待付款项
     */
    @AutoDocProperty("总待付款项")
    private int amtTotal;
    /**
     * 已付款项
     */
    @AutoDocProperty("已付款项")
    private int amtPay;
    /**
     * 实际真实待款项
     */
    @AutoDocProperty("实际真实待款项")
    private int amt;
    /**
     * 支付标题
     */
    @AutoDocProperty("支付标题")
    private String title;
    /**
     * 待付费用明细（包含应付租车费用明细）
     */
    @AutoDocProperty("待付费用明细（包含应付租车费用明细）")
    private List<AccountPayAbleResVO> accountPayAbles;

    /**
     * 待付费用明细（包含应付租车费用明细）
     */
    @AutoDocProperty("是否补付 1 是 0 否")
    private Integer isPayAgain;

    /**
     * 应付租车费用明细
     */
    @AutoDocProperty("租车费应付用明细")
    List<PayableVO> payableVOs;

    /**
     * 应付租车费用
     */
    @AutoDocProperty("应付租车费用")
    private int amtRent;
    
    @AutoDocProperty("应付租车费用补充")
    private int amtRentAfter;
    
    @AutoDocProperty("应付修改订单补付租车费用")
    private int amtIncrementRent;
    
    @AutoDocProperty("应付补付租车费用")
    private int amtIncrementRentSupplement;
    
    @AutoDocProperty("应付支付欠款费用")
    private int amtIncrementRentDebt;
    /**
     * 应付租车车俩押金
     */
    @AutoDocProperty("应付租车车辆押金")
    private int amtDeposit;
    /**
     * 应付租车违章押金
     */
    @AutoDocProperty("应付租车违章押金")
    private int amtWzDeposit;

    /**
     * 钱包抵扣金额
     */
    @AutoDocProperty("钱包抵扣金额")
    private int amtWallet;

    /**
     * 是否使用钱包 0-否，1-是
     */
    @AutoDocProperty("是否使用钱包 0-否，1-是")
    private int isUseWallet;

    /**
     * 按钮文案
     */
    @AutoDocProperty("按钮文案")
    private String buttonName;

    /**
     * costText 支付文案
     */
    @AutoDocProperty("支付文案")
    private String costText;
    /**
     * 支付时间倒计时 单位：秒
     */
    @AutoDocProperty("支付时间倒计时 单位：秒")
    private long countdown;

    /**
     * hints
     */
    @AutoDocProperty("提示文案")
    private String hints;
    
    // ---------------------------------支付收银台的扩展参数，兼容APP对接 ---------------------------------
    private List<PayInfoVo> pays = new ArrayList<PayInfoVo>();  //支付渠道，列表集合。
	private String amount; //支付金额(元)
	private String payTitle; //支付订金/支付租车押金/支付违章押金，app显示用
	private String payMemo;  //显示文案，根据支付项不同而不同（后台动态获取配置项）
	private String payKind;  //支付类型
	
	//去掉订单号，该字段已经存在。
//	private String orderNo; //订单号
	
    
	public int getAmtIncrementRent() {
		return amtIncrementRent;
	}

	public void setAmtIncrementRent(int amtIncrementRent) {
		this.amtIncrementRent = amtIncrementRent;
	}

	public String getMemNo() {
		return memNo;
	}

	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getAmtTotal() {
		return amtTotal;
	}

	public void setAmtTotal(int amtTotal) {
		this.amtTotal = amtTotal;
	}

	public int getAmtPay() {
		return amtPay;
	}

	public void setAmtPay(int amtPay) {
		this.amtPay = amtPay;
	}

	public int getAmt() {
		return amt;
	}

	public void setAmt(int amt) {
		this.amt = amt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<AccountPayAbleResVO> getAccountPayAbles() {
		return accountPayAbles;
	}

	public void setAccountPayAbles(List<AccountPayAbleResVO> accountPayAbles) {
		this.accountPayAbles = accountPayAbles;
	}

	public Integer getIsPayAgain() {
		return isPayAgain;
	}

	public void setIsPayAgain(Integer isPayAgain) {
		this.isPayAgain = isPayAgain;
	}

	public List<PayableVO> getPayableVOs() {
		return payableVOs;
	}

	public void setPayableVOs(List<PayableVO> payableVOs) {
		this.payableVOs = payableVOs;
	}

	public int getAmtRent() {
		return amtRent;
	}

	public void setAmtRent(int amtRent) {
		this.amtRent = amtRent;
	}

	public int getAmtDeposit() {
		return amtDeposit;
	}

	public void setAmtDeposit(int amtDeposit) {
		this.amtDeposit = amtDeposit;
	}

	public int getAmtWzDeposit() {
		return amtWzDeposit;
	}

	public void setAmtWzDeposit(int amtWzDeposit) {
		this.amtWzDeposit = amtWzDeposit;
	}

	public int getAmtWallet() {
		return amtWallet;
	}

	public void setAmtWallet(int amtWallet) {
		this.amtWallet = amtWallet;
	}

	public int getIsUseWallet() {
		return isUseWallet;
	}

	public void setIsUseWallet(int isUseWallet) {
		this.isUseWallet = isUseWallet;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String getCostText() {
		return costText;
	}

	public void setCostText(String costText) {
		this.costText = costText;
	}

	public long getCountdown() {
		return countdown;
	}

	public void setCountdown(long countdown) {
		this.countdown = countdown;
	}

	public String getHints() {
		return hints;
	}

	public void setHints(String hints) {
		this.hints = hints;
	}


	public int getAmtIncrementRentDebt() {
		return amtIncrementRentDebt;
	}

	public void setAmtIncrementRentDebt(int amtIncrementRentDebt) {
		this.amtIncrementRentDebt = amtIncrementRentDebt;
	}

	public int getAmtIncrementRentSupplement() {
		return amtIncrementRentSupplement;
	}

	public void setAmtIncrementRentSupplement(int amtIncrementRentSupplement) {
		this.amtIncrementRentSupplement = amtIncrementRentSupplement;
	}

	public int getAmtRentAfter() {
		return amtRentAfter;
	}

	public void setAmtRentAfter(int amtRentAfter) {
		this.amtRentAfter = amtRentAfter;
	}

	public List<PayInfoVo> getPays() {
		return pays;
	}

	public void setPays(List<PayInfoVo> pays) {
		this.pays = pays;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPayTitle() {
		return payTitle;
	}

	public void setPayTitle(String payTitle) {
		this.payTitle = payTitle;
	}

	public String getPayMemo() {
		return payMemo;
	}

	public void setPayMemo(String payMemo) {
		this.payMemo = payMemo;
	}

	public String getPayKind() {
		return payKind;
	}

	public void setPayKind(String payKind) {
		this.payKind = payKind;
	}
    
    
}