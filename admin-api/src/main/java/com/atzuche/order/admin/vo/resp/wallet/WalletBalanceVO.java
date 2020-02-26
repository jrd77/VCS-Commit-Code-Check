package com.atzuche.order.admin.vo.resp.wallet;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 钱包的余额
 *
 *
 */
@Data
@ToString
public class WalletBalanceVO implements Serializable{
	@AutoDocProperty(value="钱包余额")
	private int balance;
	@AutoDocProperty(value="会员号")
	private String memNo;
}
