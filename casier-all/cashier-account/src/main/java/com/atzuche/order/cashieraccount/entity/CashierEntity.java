package com.atzuche.order.cashieraccount.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 收银表
 * 
 * @author ZhangBin
 * @date 2019-12-25 11:35:17
 * @Description:
 */
@Data
public class CashierEntity implements Serializable {
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

}
