package com.atzuche.order.commons.vo.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 还车服务
 */
@Data
@ToString
public class ReturnHandoverCarDTO {

    @AutoDocProperty("车主默认取车地点")
    public String ownDefaultReturnCarAddr;
    @AutoDocProperty("车主默认取车地点纬度")
    public String ownDefaultReturnCarLat;
    @AutoDocProperty("车主默认取车地点经度")
    public String ownDefaultReturnCarLng;
    @AutoDocProperty("租客实际还车地址")
    public String renterRealReturnAddr;
    @AutoDocProperty("租客实际还车地址经度")
    public String renterRealReturnLat;
    @AutoDocProperty("租客实际还车地址维度")
    public String renterRealReturnLng;
    @AutoDocProperty("租客实际送车地址备注")
    public String renterRealGetRemark;
    @AutoDocProperty("车主实际收车地址")
    public String ownerRealGetAddr;
    @AutoDocProperty("车主实际收车地址经度")
    public String ownerRealGetLat;
    @AutoDocProperty("车主实际收车地址纬度")
    public String ownerRealGetLng;
    @AutoDocProperty("车主实际取车地址备注")
    public String ownerRealGetAddrReamrk;
    @AutoDocProperty("预计延后还车时间")
    public String rentTime;
    @AutoDocProperty("还车距离公里数")
    public String returnCarKM;
    @AutoDocProperty("还车费用")
    public String returnCarCrash;
    @AutoDocProperty("是否超运能")
    public String isChaoYunNeng;
    @AutoDocProperty("超运能加价")
    public String chaoYunNengAddCrash;
    @AutoDocProperty("车主还车费用")
    public String ownerReturnCarCrash;
}
