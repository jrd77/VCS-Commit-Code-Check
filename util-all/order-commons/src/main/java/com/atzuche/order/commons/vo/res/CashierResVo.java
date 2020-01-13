package com.atzuche.order.commons.vo.res;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 收银表
 * 
 * @author ZhangBin
 * @date 2019-12-25 11:35:17
 * @Description:
 */
public class CashierResVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 
	 */
	private String orderNo;
	/**
	 * 会员号
	 */
	private String memNo;
	/**
	 * 支付业务，只对接业务的APPID 短租：20,长租：21,PMS：22,套餐：23
	 */
	private String atappId;
	/**
	 * 支付app类型：ANDROID,IOS,H5,WEB,MICROPROGRAM,UNKOWN,
	 */
	private String os;
	/**
	 * 支付环境（PRO:10,DEV:30,TEST1:11,TEST2:12,TEST3:13,TEST4:14,TEST5:15,TEST6:16,TEST7:17,,TEST9:19）
	 */
	private String payEvn;
	/**
	 * 租车押金:01,违章押金:02,补付租车押金:03,坦客-租车费用:04,坦客-押金费用:05,充值:06,欠款:07,补付租车押金,管理后台v5.11:08,长租线上费用支付:09,PMS:10,默认:99
	 */
	private String payKind;
	/**
	 *  00：钱包 ，01：手机银联 02.:新银联（含银联和applepay统一商户号） 06:支付宝支付， 07:微信支付(App),  08:快捷支付（快钱） 11.快捷支付（H5）     仅仅是source值不同。 12:Apple Pay 13. 微信支付(公众号) 14.连连支付 15. 微信支付(H5)
	 */
	private String paySource;
	/**
	 * 支付方式：transType "01"：消费，"02"：预授权， 消费方式："31"：消费撤销，"32"：预授权撤销，"03"：预授权完成，"04"：退货
	 */
	private String payType;
	/**
	 * 支付凭证
	 */
	private String payTransNo;
	/**
	 * 支付时间
	 */
	private String payTime;
	/**
	 * 是否补付
	 */
	private Integer isAgainPay;
	/**
	 * 补付次数
	 */
	private Integer paySn;
	/**
	 * 支付系统 支付标题
	 */
	private String payTitle;
	/**
	 * 支付金额
	 */
	private Integer payAmt;
	/**
	 * 支付系统internalNo
	 */
	private String internalNo;
	/**
	 * 状态
	 */
	private String transStatus;
	/**
	 * 幂等字段
	 */
	private String payMd5;
	/**
	 * 支付流水号
	 */
	private String qn;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 
	 */
	private Integer version;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getMemNo() {
		return memNo;
	}
	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}
	public String getAtappId() {
		return atappId;
	}
	public void setAtappId(String atappId) {
		this.atappId = atappId;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getPayEvn() {
		return payEvn;
	}
	public void setPayEvn(String payEvn) {
		this.payEvn = payEvn;
	}
	public String getPayKind() {
		return payKind;
	}
	public void setPayKind(String payKind) {
		this.payKind = payKind;
	}
	public String getPaySource() {
		return paySource;
	}
	public void setPaySource(String paySource) {
		this.paySource = paySource;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayTransNo() {
		return payTransNo;
	}
	public void setPayTransNo(String payTransNo) {
		this.payTransNo = payTransNo;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public Integer getIsAgainPay() {
		return isAgainPay;
	}
	public void setIsAgainPay(Integer isAgainPay) {
		this.isAgainPay = isAgainPay;
	}
	public Integer getPaySn() {
		return paySn;
	}
	public void setPaySn(Integer paySn) {
		this.paySn = paySn;
	}
	public String getPayTitle() {
		return payTitle;
	}
	public void setPayTitle(String payTitle) {
		this.payTitle = payTitle;
	}
	public Integer getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(Integer payAmt) {
		this.payAmt = payAmt;
	}
	public String getInternalNo() {
		return internalNo;
	}
	public void setInternalNo(String internalNo) {
		this.internalNo = internalNo;
	}
	public String getTransStatus() {
		return transStatus;
	}
	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}
	public String getPayMd5() {
		return payMd5;
	}
	public void setPayMd5(String payMd5) {
		this.payMd5 = payMd5;
	}
	public String getQn() {
		return qn;
	}
	public void setQn(String qn) {
		this.qn = qn;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public String getCreateOp() {
		return createOp;
	}
	public void setCreateOp(String createOp) {
		this.createOp = createOp;
	}
	public LocalDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateOp() {
		return updateOp;
	}
	public void setUpdateOp(String updateOp) {
		this.updateOp = updateOp;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	
	
}
