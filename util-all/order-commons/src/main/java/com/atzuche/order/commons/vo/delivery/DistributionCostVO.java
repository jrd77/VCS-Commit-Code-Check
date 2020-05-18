package com.atzuche.order.commons.vo.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.*;

/**
 * @author 胡春林
 * 配送费用
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributionCostVO {

    @AutoDocProperty("取车费用")
    private String getCarAmt;
    @AutoDocProperty("还车费用")
    private String renturnCarAmt;
    @AutoDocProperty("取车-超运能加价")
    private String getCarChaoYunNeng;
    @AutoDocProperty("还车-超运能加价")
    private String returnCarChaoYunNeng;

}
