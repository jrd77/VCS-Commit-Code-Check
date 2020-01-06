package com.atzuche.order.admin.vo.req.delivery;

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
    public String ownRealReturnAddr;
    @AutoDocProperty("租客实际送车地址/租客实际还车地址")
    @NotBlank(message="orderNo不能为空")
    public String renterRealGetAddr;
    @AutoDocProperty("租客实际送车地址备注")
    public String renterRealGetAddrReamrk;
    @AutoDocProperty("车主实际取车地址备注")
    public String ownerRealGetAddrReamrk;
    @AutoDocProperty("是否使用取车")
    private String isUsedGetCar;
    @AutoDocProperty("是否使用还车服务")
    private String isUsedReturnCar;
    @AutoDocProperty("orderNo不能为空")
    @NotBlank(message="orderNo不能为空")
    private String orderNo;


}
