package com.atzuche.order.admin.vo.rep.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 取还车信息
 */
@Data
@ToString
public class OwnerGetAndReturnCarDTO {

    @AutoDocProperty("实际取车时间")
    public String realGetTime;
    @AutoDocProperty("实际还车时间")
    public String realReturnTime;
    @AutoDocProperty("取车里程数")
    public String getKM;
    @AutoDocProperty("还车里程数")
    public String returnKM;
    @AutoDocProperty("行驶里程数")
    public String drivingKM;
    @AutoDocProperty("日限历程")
    public String dayKM;
    @AutoDocProperty("租期")
    public String zuQi;
    @AutoDocProperty("超里程费")
    public String overKNCrash;
    @AutoDocProperty("取车油表刻度")
    public String getCarOil;
    @AutoDocProperty("还车油表刻度")
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
    @AutoDocProperty("平台加油服务费")
    public String platFormOilServiceCharge;
    @AutoDocProperty("代管车-油量差价")
    public String carOilDifferenceCrash;
    @AutoDocProperty("代管车-加油服务费")
    public String carOilServiceCharge;
    @AutoDocProperty("代管车-车主总油费")
    public String carOwnerOilCrash;





}
