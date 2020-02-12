/**
 * 
 */
package com.atzuche.order.commons.entity.ownerOrderDetail;

import java.util.List;

import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class RenterRentDetailDTO {
	@AutoDocProperty("请求时间")
    private String reqTimeStr;
    @AutoDocProperty("开始时间")
    private String rentTimeStr;
    @AutoDocProperty("结束时间")
    private String revertTimeStr;
    @AutoDocProperty("车牌号")
    private String carPlateNum;
    @AutoDocProperty("日均价")
    private Integer dayAverageAmt;
    @AutoDocProperty("一天一价列表")
    List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOS;
    
}
