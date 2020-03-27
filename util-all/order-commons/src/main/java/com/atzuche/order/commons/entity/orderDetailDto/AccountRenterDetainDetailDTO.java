package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 暂扣费用进出明细表
 * 
 * @author ZhangBin
 * @date 2020-01-11 16:41:19
 * @Description:
 */
@Data
public class AccountRenterDetainDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 主订单号
     */
    @AutoDocProperty(value="主订单号",required=true)
    private String orderNo;
    /**
     * 会员号
     */
    @AutoDocProperty(value="会员号",required=true)
    private String memNo;
    /**
     * 暂扣金额
     */
    @AutoDocProperty(value="暂扣金额",required=true)
    private Integer amt;
    /**
     * 暂扣时间
     */
    @AutoDocProperty(value="暂扣时间",required=true)
    private LocalDateTime time;
    /**
     * 暂扣费用来源编码
     */
    @AutoDocProperty(value="暂扣费用来源编码",required=true)
    private String sourceCode;
    /**
     * 暂扣费用来源编码描述
     */
    @AutoDocProperty(value="暂扣费用来源编码描述",required=true)
    private String sourceDetail;
    /**
     * 暂扣凭证
     */
    @AutoDocProperty(value="暂扣凭证",required=true)
    private String uniqueNo;
    /**
     * 备注
     */
    @AutoDocProperty(value="备注",required=true)
    private String remake;

    public AccountRenterDetainDetailDTO(Integer amt) {
        this.amt = amt;
    }

    public AccountRenterDetainDetailDTO() {

    }
}
