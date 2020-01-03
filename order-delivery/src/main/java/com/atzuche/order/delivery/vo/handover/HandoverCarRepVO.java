package com.atzuche.order.delivery.vo.handover;

import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 胡春林
 * 返回给service的数据
 */
@Data
@ToString
public class HandoverCarRepVO {
    List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities;
    List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities;
}
