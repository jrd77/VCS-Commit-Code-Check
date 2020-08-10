package com.atzuche.order.cashieraccount.vo.req;

/**
 * @author jing.huang
 */
public class WithdrawalsReqVO {

    private String serialNumber;

    private String memNo;

    private String bindCardNo;

    private String amount;

    private String usage;

    private String remark;

    private String smsCode;


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMemNo() {
        return memNo;
    }

    public void setMemNo(String memNo) {
        this.memNo = memNo;
    }

    public String getBindCardNo() {
        return bindCardNo;
    }

    public void setBindCardNo(String bindCardNo) {
        this.bindCardNo = bindCardNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }


    @Override
    public String toString() {
        return "WithdrawalsReqVO{" +
                "serialNumber='" + serialNumber + '\'' +
                ", memNo='" + memNo + '\'' +
                ", bindCardNo='" + bindCardNo + '\'' +
                ", amount='" + amount + '\'' +
                ", usage='" + usage + '\'' +
                ", remark='" + remark + '\'' +
                ", smsCode='" + smsCode + '\'' +
                '}';
    }
}
