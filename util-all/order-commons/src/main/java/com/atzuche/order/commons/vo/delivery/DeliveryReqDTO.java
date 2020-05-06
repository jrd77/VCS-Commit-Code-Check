package com.atzuche.order.commons.vo.delivery;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 胡春林
 * 取还车数据
 */
@Data
@ToString
public class DeliveryReqDTO {

    @AutoDocProperty("车主实际交车地址/车主实际收车地址")
    public String ownerGetReturnAddr;
    @AutoDocProperty("车主实际交车地址/车主实际收车地址经度")
    public String ownerGetReturnLat;
    @AutoDocProperty("车主实际交车地址/车主实际收车地址纬度")
    public String ownerGetReturnLng;
    @AutoDocProperty("租客实际送车地址/租客实际还车地址")
    public String renterGetReturnAddr;
    @AutoDocProperty("租客实际送车地址/租客实际还车地址经度")
    public String renterGetReturnLat;
    @AutoDocProperty("租客实际送车地址/租客实际还车地址纬度")
    public String renterGetReturnLng;
    @AutoDocProperty("租客实际送车地址备注")
    public String renterRealGetAddrReamrk;
    @AutoDocProperty("车主实际取车地址备注")
    public String ownerRealGetAddrReamrk;
    @AutoDocProperty("是否使用取还车服务 0:不使用 1:使用")
    private String isUsedGetAndReturnCar;
    @AutoDocProperty("orderNo不能为空")
    @NotBlank(message="orderNo不能为空")
    private String orderNo;


}
