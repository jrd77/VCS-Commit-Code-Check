package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 配送费用
 */
@Data
@ToString
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
