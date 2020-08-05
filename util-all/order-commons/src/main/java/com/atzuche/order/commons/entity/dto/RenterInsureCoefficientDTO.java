package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class RenterInsureCoefficientDTO {
   @AutoDocProperty(value = "驾驶行为评分")
    private String driverScore;

    @AutoDocProperty(value = "驾驶行为系数")
    private Double driverCoefficient= 0D;

    @AutoDocProperty(value = "驾龄系数")
    private Double driverAgeCoefficient=0D;

    @AutoDocProperty(value = "保费打折折扣")
    private Double InsureDiscount= 0D;

    @AutoDocProperty(value = "易出险系数")
    private Double easilyDangerCoefficient= 0D;
}
