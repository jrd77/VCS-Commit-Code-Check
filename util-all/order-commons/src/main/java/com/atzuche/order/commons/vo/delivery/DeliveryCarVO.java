package com.atzuche.order.commons.vo.delivery;


import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 取还车信息
 */
@Data
@ToString
public class DeliveryCarVO {

    @AutoDocProperty("取车服务信息")
    private GetHandoverCarDTO getHandoverCarDTO;
    @AutoDocProperty("还车服务信息")
    private ReturnHandoverCarDTO returnHandoverCarDTO;
    @AutoDocProperty("车主取还车服务")
    private OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO;
    @AutoDocProperty("租客取还车服务")
    private RenterGetAndReturnCarDTO renterGetAndReturnCarDTO;
    @AutoDocProperty("是否取车 0:否 1:是")
    private Integer isGetCar;
    @AutoDocProperty("是否还车 0:否 1:是")
    private Integer isReturnCar;
    @AutoDocProperty("orderNo订单号")
    private String orderNo;
    // 租客会员号
    private String renterMemNo;
    // 车主会员号
    private String ownerMemNo;



}