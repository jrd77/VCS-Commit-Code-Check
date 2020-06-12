/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost.detail;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class OrderRenterFineAmtDetailResVO {
	//租客提前还车罚金  租客延迟还车罚金   租客违约罚金   租客取还车违约金
	@AutoDocProperty(value="租客提前还车罚金")
	private String renterBeforeReturnCarFineAmt;
	
	@AutoDocProperty(value="租客延迟还车罚金")
	private String renterDelayReturnCarFineAmt;
	
	@AutoDocProperty(value="租客违约罚金(取消订单违约金)")
	private String renterFineAmt;
	
	@AutoDocProperty(value="租客取还车违约金")
	private String renterGetReturnCarFineAmt;

    /*
     * @Author ZhangBin
     * @Date 2020/6/10 17:07
     * @Description: 获取总金额
     *
     **/
	public static int getTotlal(OrderRenterFineAmtDetailResVO data){
        int total = 0;
        if(data.getRenterBeforeReturnCarFineAmt() != null){
            total += Integer.valueOf(data.getRenterBeforeReturnCarFineAmt());
        }
        if(data.getRenterDelayReturnCarFineAmt() != null){
            total += Integer.valueOf(data.getRenterDelayReturnCarFineAmt());
        }
        if(data.getRenterFineAmt() != null){
            total += Integer.valueOf(data.getRenterFineAmt());
        }
        if(data.getRenterGetReturnCarFineAmt() != null){
            total += Integer.valueOf(data.getRenterGetReturnCarFineAmt());
        }
        return total;
    }
}
