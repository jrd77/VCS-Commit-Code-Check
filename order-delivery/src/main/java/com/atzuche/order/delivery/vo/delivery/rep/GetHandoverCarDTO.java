package com.atzuche.order.delivery.vo.delivery.rep;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 取车服务
 */
@Data
@ToString
public class GetHandoverCarDTO {

    @AutoDocProperty("车主默认取车地点")
    public String ownDefaultGetCarAddr;
    @AutoDocProperty("车主实际交车地址")
    public String ownRealReturnAddr;
    @AutoDocProperty("车主实际取车地址备注")
    public String ownRealGetRemark;
    @AutoDocProperty("租客实际送车地址")
    public String renterRealGetAddr;
    @AutoDocProperty("租客实际送车地址备注")
    public String renterRealGetAddrReamrk;
    @AutoDocProperty("预计提前取车时间")
    public String rentTime;
    @AutoDocProperty("取车距离公里数")
    public String getCarKM;
    @AutoDocProperty("取车费用")
    public String getCarCrash;
    @AutoDocProperty("是否超运能")
    public String isChaoYunNeng;
    @AutoDocProperty("超运能加价")
    public String chaoYunNengAddCrash;
    @AutoDocProperty("车主取车费用")
    public String ownerGetCarCrash;



}
