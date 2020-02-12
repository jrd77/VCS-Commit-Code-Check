/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost.detail;

import java.util.List;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class ReductionDetailResVO {
	@AutoDocProperty(value="减免前车辆押金")
    String reductionBeforeRentDepost;
	
	@AutoDocProperty(value="减免后车辆押金")
    String reductionAfterRentDepost;
    
	
	@AutoDocProperty(value="减免项目列表")
	List<ReductionTaskResVO> reductTaskList;
	
	@AutoDocProperty(value="年份系数")
    String yearCoefficient;
    
	@AutoDocProperty(value="品牌系数")
    String brandCoefficient;
    
	
	@AutoDocProperty(value="平台减免金额")
    String reductionAmt;
    
}
