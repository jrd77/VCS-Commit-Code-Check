package com.atzuche.order.open.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class BaoFeiInfoVO {
    @AutoDocProperty("原始单价")
    private Integer unitOrignPrice;
    @AutoDocProperty("保费类型，1：基础保障费，2：补偿保障费")
    private Integer baoFeiType;
    @AutoDocProperty("驾龄系数")
    private Double jiaLinCoefficient;
    @AutoDocProperty("易出险车系数")
    private Double yiChuXianCheCoefficient;
    @AutoDocProperty("驾驶行为系数")
    private Double jiaShiXingWeiCoefficient;

}
