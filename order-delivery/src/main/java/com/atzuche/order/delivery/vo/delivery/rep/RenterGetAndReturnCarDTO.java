package com.atzuche.order.delivery.vo.delivery.rep;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 *
 */
@Data
@ToString
@Builder
public class RenterGetAndReturnCarDTO {

    @AutoDocProperty("实际交车时间")
    public String realGetTime;
    @AutoDocProperty("实际收车时间")
    public String realReturnTime;
    @AutoDocProperty("交车里程数")
    public String getKM;
    @AutoDocProperty("收车里程数")
    public String returnKM;
    @AutoDocProperty("行驶里程数")
    public String drivingKM;
    @AutoDocProperty("日限历程")
    public String dayKM;
    @AutoDocProperty("租期")
    public String zuQi;
    @AutoDocProperty("超里程费")
    public String overKNCrash;
    @AutoDocProperty("交车油表刻度")
    public String getCarOil;
    @AutoDocProperty("收车油表刻度")
    public String returnCarOil;
    @AutoDocProperty("燃料")
    public String ranLiao;
    @AutoDocProperty("油箱容量")
    public String oilContainer;
    @AutoDocProperty("油量差额")
    public String oilDifference;
    @AutoDocProperty("油量差价")
    public String oilDifferenceCrash;
    @AutoDocProperty("加油服务费")
    public String oilServiceCharge;
    @AutoDocProperty("租客总油费")
    public String carOwnerOilCrash;
}
