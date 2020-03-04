package com.atzuche.order.cashieraccount.entity;

import java.time.LocalDateTime;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.autoyol.platformcost.CommonUtils;

public class AccountOwnerCashExamine {
    private Integer id;
    
    @AutoDocProperty(value="提现金额")
    private Integer amt;
    
    @AutoDocProperty(value="1:中国银行,2:中国工商银行,3:中国农业银行,4:中国建设银行,5:中国邮政储蓄银行,6:交通银行,7:招商银行,8:浦发银行,9:兴业银行,10:华夏银行,11:广发银行,12:中信银行,13:中国光大银行,14:上海银行,15:上海农商银行,16:宁波银行,17:中国民生银行,18:杭州银行,19:南京银行")
    private Integer bankName;
    
    @AutoDocProperty(value="卡号")
    private String cardNo;
    
    @AutoDocProperty(value="流水号")
    private String serialNumber;
    
    @AutoDocProperty(value="用户注册号")
    private Integer memNo;
    
    @AutoDocProperty(value="用户手机号")
    private Long mobile;
    
    @AutoDocProperty(value="姓名")
    private String realName;
    
    @AutoDocProperty(value="持卡人姓名")
    private String cardHolder;
    
    @AutoDocProperty(value="开户支行名称")
    private String branchBankName;
    
    @AutoDocProperty(value="开户省份")
    private String province;
    
    @AutoDocProperty(value="开户城市")
    private String city;
    
    @AutoDocProperty(value="0:未处理，1:已处理，2:暂停, 11连接失败，12打款中，13打款成功，14打款失败，15人工处理。")
    private Integer status;
    
    @AutoDocProperty(value="提现时间")
    private String createTimeStr;
    
    @AutoDocProperty(value="处理时间")
    private String updateTimeStr;
    
    private Integer importFlag;

    private Integer resCode;

    private String resCodeTxt;

    private String resMsg;

    private String errCode;

    private String errTxt;

    private Integer queryCode;

    private String queryCodeTxt;

    private String flowReqstsTxt;

    private String flowRtnflgTxt;

    private Integer weight;

    private Integer handshakePushFlag;

    private Integer handshakePullFlag;

    private Integer handshakeCheckFlag;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String createOp;

    private String updateOp;

    private Byte isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmt() {
        return amt;
    }

    public void setAmt(Integer amt) {
        this.amt = amt;
    }

    public Integer getBankName() {
        return bankName;
    }

    public void setBankName(Integer bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo == null ? null : cardNo.trim();
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber == null ? null : serialNumber.trim();
    }

    public Integer getMemNo() {
        return memNo;
    }

    public void setMemNo(Integer memNo) {
        this.memNo = memNo;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder == null ? null : cardHolder.trim();
    }

    public String getBranchBankName() {
        return branchBankName;
    }

    public void setBranchBankName(String branchBankName) {
        this.branchBankName = branchBankName == null ? null : branchBankName.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getImportFlag() {
        return importFlag;
    }

    public void setImportFlag(Integer importFlag) {
        this.importFlag = importFlag;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getResCodeTxt() {
        return resCodeTxt;
    }

    public void setResCodeTxt(String resCodeTxt) {
        this.resCodeTxt = resCodeTxt == null ? null : resCodeTxt.trim();
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg == null ? null : resMsg.trim();
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode == null ? null : errCode.trim();
    }

    public String getErrTxt() {
        return errTxt;
    }

    public void setErrTxt(String errTxt) {
        this.errTxt = errTxt == null ? null : errTxt.trim();
    }

    public Integer getQueryCode() {
        return queryCode;
    }

    public void setQueryCode(Integer queryCode) {
        this.queryCode = queryCode;
    }

    public String getQueryCodeTxt() {
        return queryCodeTxt;
    }

    public void setQueryCodeTxt(String queryCodeTxt) {
        this.queryCodeTxt = queryCodeTxt == null ? null : queryCodeTxt.trim();
    }

    public String getFlowReqstsTxt() {
        return flowReqstsTxt;
    }

    public void setFlowReqstsTxt(String flowReqstsTxt) {
        this.flowReqstsTxt = flowReqstsTxt == null ? null : flowReqstsTxt.trim();
    }

    public String getFlowRtnflgTxt() {
        return flowRtnflgTxt;
    }

    public void setFlowRtnflgTxt(String flowRtnflgTxt) {
        this.flowRtnflgTxt = flowRtnflgTxt == null ? null : flowRtnflgTxt.trim();
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHandshakePushFlag() {
        return handshakePushFlag;
    }

    public void setHandshakePushFlag(Integer handshakePushFlag) {
        this.handshakePushFlag = handshakePushFlag;
    }

    public Integer getHandshakePullFlag() {
        return handshakePullFlag;
    }

    public void setHandshakePullFlag(Integer handshakePullFlag) {
        this.handshakePullFlag = handshakePullFlag;
    }

    public Integer getHandshakeCheckFlag() {
        return handshakeCheckFlag;
    }

    public void setHandshakeCheckFlag(Integer handshakeCheckFlag) {
        this.handshakeCheckFlag = handshakeCheckFlag;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp == null ? null : createOp.trim();
    }

    public String getUpdateOp() {
        return updateOp;
    }

    public void setUpdateOp(String updateOp) {
        this.updateOp = updateOp == null ? null : updateOp.trim();
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

	public String getCreateTimeStr() {
		if (createTime != null) {
			createTimeStr = CommonUtils.formatTime(createTime, CommonUtils.FORMAT_STR_DEFAULT);
		}
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getUpdateTimeStr() {
		if (updateTime != null) {
			updateTimeStr = CommonUtils.formatTime(updateTime, CommonUtils.FORMAT_STR_DEFAULT);
		}
		return updateTimeStr;
	}

	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}
    
}